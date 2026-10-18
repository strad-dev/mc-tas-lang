package tas.listeners;

import abilities.ClassAbilities;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tas.FakePlayerManager;

public class FakeListener implements Listener {

	// M7 cancels every drop and fires real players' drop abilities from its packet interceptor, which fakes bypass.
	@EventHandler(priority = EventPriority.HIGH)
	public void onFakeDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if(!FakePlayerManager.getFakePlayers().containsValue(p)) return;
		ClassAbilities.dispatch(p, !p.isSprinting());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onFakeKnockback(EntityKnockbackEvent e) {
		if(e.getEntity() instanceof Player p && FakePlayerManager.getFakePlayers().containsValue(p)) {
			e.setCancelled(true);
		}
	}

	// Fakes use simulated pearls + scripted teleports, so M7's boss-arena pearl ban doesn't apply to them.
	@EventHandler(priority = EventPriority.HIGH)
	public void onFakePearl(PlayerInteractEvent e) {
		if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		ItemStack item = e.getItem();
		if(item == null || item.getType() != Material.ENDER_PEARL) return;
		if(!FakePlayerManager.getFakePlayers().containsValue(e.getPlayer())) return;
		e.setUseItemInHand(Event.Result.ALLOW);
	}
}
