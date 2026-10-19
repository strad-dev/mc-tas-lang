package plugin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMountScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import commands.Spectate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SpectatorSync {
	private record InventoryBackup(ItemStack[] contents, ItemStack[] armorContents, ItemStack offHand, int heldItemSlot) {
		InventoryBackup(PlayerInventory inv) {
			this(inv.getContents().clone(), inv.getArmorContents().clone(), inv.getItemInOffHand().clone(), inv.getHeldItemSlot());
		}

		void restore(Player player) {
			PlayerInventory inv = player.getInventory();
			inv.setContents(contents);
			inv.setArmorContents(armorContents);
			inv.setItemInOffHand(offHand);
			inv.setHeldItemSlot(heldItemSlot);
			player.updateInventory();
		}
	}

	private static final Map<Player, InventoryBackup> originalInventories = new HashMap<>();

	public static void backup(Player player) {
		originalInventories.put(player, new InventoryBackup(player.getInventory()));
	}

	public static void restoreAndRemove(Player player) {
		InventoryBackup backup = originalInventories.remove(player);
		if(backup != null) {
			backup.restore(player);
		}
	}

	public static void clearAll() {
		originalInventories.clear();
	}

	private static BukkitTask inventorySyncTask;

	public static void startInventorySync() {
		if(inventorySyncTask != null) {
			inventorySyncTask.cancel();
		}

		inventorySyncTask = Bukkit.getScheduler().runTaskTimer(mcTASLang.getInstance(), () -> {
			for(Player spectator : Spectate.getSpectatorMap().keySet()) {
				Player fakePlayer = Spectate.getSpectatorMap().get(spectator);
				if(fakePlayer != null) {
					syncInventory(fakePlayer);
				}
			}
		}, 0L, 1L);
	}

	public static void stopInventorySync() {
		if(inventorySyncTask != null) {
			inventorySyncTask.cancel();
			inventorySyncTask = null;
		}
	}

	public static void syncInventory(Player fakePlayer) {
		Set<Player> spectators = Spectate.getSpectatingPlayers(fakePlayer);
		if(!spectators.isEmpty()) {
			forceFullInventorySync(fakePlayer, spectators);
		}
	}

	public static void forceFullInventorySync(Player sourcePlayer, Set<Player> targets) {
		CraftPlayer cp = (CraftPlayer) sourcePlayer;
		ServerPlayer sourceHandle = cp.getHandle();

		for(Player target : targets) {
			if(target instanceof CraftPlayer craftTarget) {
				ServerPlayer targetHandle = craftTarget.getHandle();
				AbstractContainerMenu targetMenu = targetHandle.inventoryMenu;
				ClientboundContainerSetContentPacket pkt = new ClientboundContainerSetContentPacket(
					targetMenu.containerId, targetMenu.incrementStateId(),
					sourceHandle.inventoryMenu.getItems(), sourceHandle.containerMenu.getCarried()
				);
				targetHandle.connection.send(pkt);
			}
		}
	}

	public static void syncHand(Player fake) {
		ServerPlayer npc = ((CraftPlayer) fake).getHandle();
		net.minecraft.world.item.ItemStack handStack = npc.getInventory().getSelectedItem();

		ClientboundSetEquipmentPacket equipPkt = new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND, handStack)));
		TASUtils.broadcastPacket(equipPkt);

		int heldSlot = fake.getInventory().getHeldItemSlot();
		Set<Player> spectators = Spectate.getSpectatingPlayers(fake);
		for(Player spectator : spectators) {
			if(spectator instanceof CraftPlayer craftSpectator) {
				craftSpectator.getHandle().connection.send(new ClientboundSetHeldSlotPacket(heldSlot));
			}
		}
	}

	// The fake's open screen as its client was told it, replayed to a watcher who starts mid-GUI. Trades, if any,
	// follow the open packet.
	private static final Map<UUID, List<Packet<?>>> openScreens = new HashMap<>();

	/**
	 * Called with every packet the server sends a fake. Screens (chest, merchant, anvil, mount, book, sign, dialog)
	 * also go to its watchers, so they see any GUI the fake does. Container 0 is the fake's own inventory, which
	 * {@link #syncInventory} already mirrors.
	 */
	public static void mirrorScreen(ServerPlayer fake, Packet<?> packet) {
		boolean opens = packet instanceof ClientboundOpenScreenPacket || packet instanceof ClientboundMountScreenOpenPacket
				|| packet instanceof ClientboundOpenBookPacket || packet instanceof ClientboundOpenSignEditorPacket
				|| packet instanceof ClientboundShowDialogPacket;
		boolean closes = packet instanceof ClientboundContainerClosePacket || packet instanceof ClientboundClearDialogPacket;
		boolean updates = packet instanceof ClientboundMerchantOffersPacket
				|| packet instanceof ClientboundContainerSetDataPacket
				|| (packet instanceof ClientboundContainerSetContentPacket content && content.containerId() != 0)
				|| (packet instanceof ClientboundContainerSetSlotPacket slot && slot.getContainerId() != 0);
		if(!opens && !closes && !updates) return;

		UUID key = fake.getUUID();
		if(opens) {
			openScreens.put(key, new ArrayList<>(List.of(packet)));
		} else if(closes) {
			openScreens.remove(key);
		} else if(packet instanceof ClientboundMerchantOffersPacket && openScreens.containsKey(key)) {
			openScreens.get(key).add(packet);
		}
		for(Player watcher : Spectate.getSpectatingPlayers(fake.getBukkitEntity())) send(watcher, packet);
	}

	/**
	 * Called with every packet the server sends a fake: sounds also go to its watchers, so they hear what it hears.
	 * A broadcast the watcher got directly is the same instance; the watcher's interceptor drops the second copy.
	 */
	public static void mirrorSound(ServerPlayer fake, Packet<?> packet) {
		if(!(packet instanceof ClientboundSoundPacket || packet instanceof ClientboundSoundEntityPacket
				|| packet instanceof ClientboundStopSoundPacket)) return;
		for(Player watcher : Spectate.getSpectatingPlayers(fake.getBukkitEntity())) send(watcher, packet);
	}

	/** The fake closed a screen itself (its client sent the close), which the server never echoes back. */
	public static void fakeClosedScreen(ServerPlayer fake, int containerId) {
		openScreens.remove(fake.getUUID());
		for(Player watcher : Spectate.getSpectatingPlayers(fake.getBukkitEntity())) {
			send(watcher, new ClientboundContainerClosePacket(containerId));
		}
	}

	public static boolean hasOpenScreen(Player fake) {
		return openScreens.containsKey(fake.getUniqueId());
	}

	/** A new watcher sees the fake's open screen, then its current contents. */
	public static void showOpenScreen(Player fake, Player watcher) {
		List<Packet<?>> screen = openScreens.get(fake.getUniqueId());
		if(screen == null) return;
		for(Packet<?> packet : screen) send(watcher, packet);
		ServerPlayer handle = ((CraftPlayer) fake).getHandle();
		if(handle.containerMenu != handle.inventoryMenu) handle.containerMenu.sendAllDataToRemote();
	}

	/** A leaving watcher's client drops the fake's screen. */
	public static void hideOpenScreen(Player fake, Player watcher) {
		List<Packet<?>> screen = openScreens.get(fake.getUniqueId());
		if(screen == null) return;
		Packet<?> open = screen.getFirst();
		if(open instanceof ClientboundOpenScreenPacket o) send(watcher, new ClientboundContainerClosePacket(o.getContainerId()));
		else if(open instanceof ClientboundMountScreenOpenPacket o) send(watcher, new ClientboundContainerClosePacket(o.getContainerId()));
		else if(open instanceof ClientboundShowDialogPacket) send(watcher, ClientboundClearDialogPacket.INSTANCE);
	}

	public static void forgetScreen(Player fake) {
		openScreens.remove(fake.getUniqueId());
	}

	private static void send(Player watcher, Packet<?> packet) {
		if(watcher instanceof CraftPlayer cp) cp.getHandle().connection.send(packet);
	}
}
