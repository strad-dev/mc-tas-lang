package listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import plugin.FakePlayerManager;
import plugin.SpectatorSync;
import commands.Spectate;
import plugin.mcTASLang;

public class SpectatorListener implements Listener {

	/** A watcher's own /pets (or /pet) opens the watched fake's, view-only (M7's /pets <player>). */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPetsCommand(PlayerCommandPreprocessEvent event) {
		Player fake = Spectate.getSpectatorMap().get(event.getPlayer());
		if(fake == null) return;
		String command = event.getMessage().trim().toLowerCase();
		if(command.equals("/pets") || command.equals("/pet") || command.equals("/m7:pets") || command.equals("/m7:pet")) {
			event.setMessage("/m7:pets " + fake.getName());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.ScriptWatchers.join(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (Spectate.getSpectatorMap().containsKey(player)) {
			Spectate.removeSpectator(player);
		}
		plugin.ScriptWatchers.quit(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player player) {
			if (Spectate.getSpectatorMap().containsKey(player)) {
				// Allow viewing but prevent actual changes
				event.setCancelled(true);

				// Re-sync inventory
				Player fakePlayer = Spectate.getSpectatorMap().get(player);
				if (fakePlayer != null) {
					Bukkit.getScheduler().runTaskLater(mcTASLang.getInstance(), () -> SpectatorSync.syncInventory(fakePlayer), 1L);
				}
			}
		}
	}

	// No item drops for spectators
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (Spectate.getSpectatorMap().containsKey(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent e) {
		Player p = e.getPlayer();
		if(e.getAnimationType().equals(PlayerAnimationType.ARM_SWING) && FakePlayerManager.getFakePlayers().containsValue(p) && Spectate.getReverseSpectatorMap().containsKey(p)) {
			for(Player spectator : Spectate.getReverseSpectatorMap().get(p)) {
				spectator.swingMainHand();
			}
		}
	}
}
