package plugin;

import commands.Spectate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** While a /tas script runs every real player watches, so M7 leaves them out of the run (its watching tag). */
public class ScriptWatchers {
	/** Tagged by the script; anyone already watching keeps the tag's other owner. */
	private static final Set<UUID> tagged = new HashSet<>();
	private static BukkitTask endCheck;
	private static boolean listening;

	/** Tags every real player not already watching, until {@link #release}. Call before the script's /m7practice. */
	public static void watch() {
		release();
		for(Player p : Bukkit.getOnlinePlayers()) tag(p);
		listenForRunEnd();
		// A run torn down or replaced without an ending of its own never fires RunCompleteEvent, but the run moves on.
		int run = TASUtils.currentRun();
		endCheck = Bukkit.getScheduler().runTaskTimer(mcTASLang.getInstance(), () -> {
			if(TASUtils.currentRun() != run) release();
		}, 20L, 20L);
	}

	private static void tag(Player p) {
		if(FakePlayerManager.getFakePlayers().containsValue(p) || p.getScoreboardTags().contains(TASUtils.WATCHING_TAG)) return;
		p.addScoreboardTag(TASUtils.WATCHING_TAG);
		tagged.add(p.getUniqueId());
	}

	public static void join(Player p) {
		if(endCheck != null) tag(p);
	}

	/** M7 fires its RunCompleteEvent for every ending, won or not. Registered by name: M7 isn't a dependency. */
	private static void listenForRunEnd() {
		if(listening) return;
		Class<?> event = M7Link.loadClass("plugin.RunCompleteEvent");
		if(event == null || !Event.class.isAssignableFrom(event)) return;
		Bukkit.getPluginManager().registerEvent(event.asSubclass(Event.class), new Listener() {}, EventPriority.MONITOR,
				(listener, e) -> release(), mcTASLang.getInstance());
		listening = true;
	}

	public static void release() {
		if(endCheck != null) {
			endCheck.cancel();
			endCheck = null;
		}
		for(UUID id : tagged) {
			Player p = Bukkit.getPlayer(id);
			// Spectate owns the tag of anyone watching a fake.
			if(p != null && !Spectate.getSpectatorMap().containsKey(p)) p.removeScoreboardTag(TASUtils.WATCHING_TAG);
		}
		tagged.clear();
	}

	public static boolean holds(Player p) {
		return tagged.contains(p.getUniqueId());
	}

	/** Scoreboard tags persist in player data, so a quitter loses it now. */
	public static void quit(Player p) {
		if(tagged.remove(p.getUniqueId())) p.removeScoreboardTag(TASUtils.WATCHING_TAG);
	}
}
