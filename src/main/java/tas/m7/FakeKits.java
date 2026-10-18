package tas.m7;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugin.FakePlayerInventory;
import plugin.Utils;
import tas.FakePlayerManager;
import tas.SpectatorSync;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FakeKits {
	public static void setInventories() {
		Map<String, Player> fakePlayers = FakePlayerManager.getFakePlayers();
		for(String entry : fakePlayers.keySet()) {
			Player p = fakePlayers.get(entry);

			p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1, 255, true, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 255, true, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 3, true, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255, true, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, -1, 2, true, false));
			Objects.requireNonNull(p.getAttribute(Attribute.ARMOR)).setBaseValue(20.0);
			Objects.requireNonNull(p.getAttribute(Attribute.ARMOR_TOUGHNESS)).setBaseValue(20.0);
			Objects.requireNonNull(p.getAttribute(Attribute.KNOCKBACK_RESISTANCE)).setBaseValue(1);
			Objects.requireNonNull(p.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(40);
			p.setHealth(40);

			// M7 reads class from the tag; every MageN fake is mechanically a Mage.
			p.addScoreboardTag(entry.startsWith("Mage") ? "Mage" : entry);
			applyClassLoadout(p, entry);

			PlayerInventory inventory = p.getInventory();
			CraftPlayer player = (CraftPlayer) p;
			ServerPlayer nmsPlayer = player.getHandle();

			net.minecraft.world.item.ItemStack helmet = CraftItemStack.asNMSCopy(Objects.requireNonNull(inventory).getHelmet());
			net.minecraft.world.item.ItemStack chestplate = CraftItemStack.asNMSCopy(Objects.requireNonNull(inventory).getChestplate());
			net.minecraft.world.item.ItemStack leggings = CraftItemStack.asNMSCopy(Objects.requireNonNull(inventory).getLeggings());
			net.minecraft.world.item.ItemStack boots = CraftItemStack.asNMSCopy(Objects.requireNonNull(inventory).getBoots());
			net.minecraft.world.item.ItemStack hand = CraftItemStack.asNMSCopy(inventory.getItemInMainHand());

			nmsPlayer.setItemSlot(EquipmentSlot.HEAD, helmet, false);
			nmsPlayer.setItemSlot(EquipmentSlot.CHEST, chestplate, false);
			nmsPlayer.setItemSlot(EquipmentSlot.LEGS, leggings, false);
			nmsPlayer.setItemSlot(EquipmentSlot.FEET, boots, false);
			nmsPlayer.setItemSlot(EquipmentSlot.MAINHAND, hand, false);

			List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> gear = List.of(Pair.of(EquipmentSlot.HEAD, helmet), Pair.of(EquipmentSlot.CHEST, chestplate), Pair.of(EquipmentSlot.LEGS, leggings), Pair.of(EquipmentSlot.FEET, boots), Pair.of(EquipmentSlot.MAINHAND, hand));

			ClientboundSetEquipmentPacket equipmentPacket = new ClientboundSetEquipmentPacket(nmsPlayer.getId(), gear);

			Utils.broadcastPacket(equipmentPacket);

			SpectatorSync.syncInventory(p);
			SpectatorSync.syncHand(p);
		}
	}

	/** Clears {@code p}'s inventory and applies the class kit. Every {@code MageN} fake carries the Mage kit. */
	private static void applyClassLoadout(Player p, String role) {
		ItemStack[] arr = FakePlayerInventory.classLoadoutContents(role.startsWith("Mage") ? "Mage" : role);
		PlayerInventory inventory = p.getInventory();
		inventory.clear();

		// A kit sets the TAS speed (400).
		Utils.setSpeed(p, 400);

		for(int i = 0; i < 36; i++) inventory.setItem(i, arr[i]);
		inventory.setHelmet(arr[36]);
		inventory.setChestplate(arr[37]);
		inventory.setLeggings(arr[38]);
		inventory.setBoots(arr[39]);
		if(arr[40] != null) inventory.setItemInOffHand(arr[40]);
	}
}
