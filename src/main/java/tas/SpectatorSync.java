package tas;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import plugin.PlayerInventoryBackup;
import plugin.Utils;
import tas.commands.Spectate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpectatorSync {
	private static final Map<Player, PlayerInventoryBackup> originalInventories = new HashMap<>();

	public static void backup(Player player) {
		originalInventories.put(player, new PlayerInventoryBackup(player));
	}

	public static void restoreAndRemove(Player player) {
		PlayerInventoryBackup backup = originalInventories.remove(player);
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
				AbstractContainerMenu targetMenu = targetHandle.containerMenu;
				ClientboundContainerSetContentPacket pkt = new ClientboundContainerSetContentPacket(
					targetMenu.containerId, targetMenu.incrementStateId(),
					sourceHandle.containerMenu.getItems(), sourceHandle.containerMenu.getCarried()
				);
				targetHandle.connection.send(pkt);
			}
		}
	}

	public static void syncHand(Player fake) {
		ServerPlayer npc = ((CraftPlayer) fake).getHandle();
		net.minecraft.world.item.ItemStack handStack = npc.getInventory().getSelectedItem();

		ClientboundSetEquipmentPacket equipPkt = new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND, handStack)));
		Utils.broadcastPacket(equipPkt);

		int heldSlot = fake.getInventory().getHeldItemSlot();
		Set<Player> spectators = Spectate.getSpectatingPlayers(fake);
		for(Player spectator : spectators) {
			if(spectator instanceof CraftPlayer craftSpectator) {
				craftSpectator.getHandle().connection.send(new ClientboundSetHeldSlotPacket(heldSlot));
			}
		}
	}
}
