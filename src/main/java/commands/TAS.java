package commands;

import instructions.Actions;
import instructions.players.Archer;
import instructions.players.Berserk;
import instructions.players.Healer;
import instructions.players.Mage;
import instructions.players.Tank;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jspecify.annotations.NonNull;
import plugin.FakePlayerManager;
import plugin.M7Link;
import plugin.ScriptWatchers;
import plugin.TASUtils;

import java.util.List;
import java.util.Map;

public class TAS implements CommandExecutor {
	private static final List<String> SECTIONS = List.of("all", "clear", "boss", "maxor", "storm", "goldor", "necron", "witherking");
	private static final int COUNTDOWN = 60;
	private static final int CLEAR_COUNTDOWN = 100;
	// Difficulty, mayor, Fix Watcher Bug, Bad Bonzo, Bat Bug, Alpha GDrag Damage, in /m7practice's own argument forms.
	private static final String SETTINGS = "perfect_rng jerry_paul off badbonzo=off batbug=on alphagdrag=on";

	/** Chained run (all, boss): ticks from a segment's start to the next's. A routine may pass its own instead. */
	public static final int CLEAR_TICKS = 727;
	public static final int MAXOR_TICKS = 496;
	public static final int STORM_TICKS = 881;
	public static final int GOLDOR_TICKS = 350;
	public static final int NECRON_TICKS = 600;

	/** Ticks from /tas until the section starts. M7 gives boss and maxor no countdown, only its fixed grace. */
	public static int startDelay(String section) {
		return switch(section) {
			case "all", "clear" -> CLEAR_COUNTDOWN;
			case "boss", "maxor" -> M7Link.getInt("instructions.Server", "MAXOR_GRACE_TICKS", 0);
			default -> COUNTDOWN;
		};
	}

	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
		if(!(sender instanceof Player p)) {
			sender.sendMessage(TASUtils.msg("Only players can run this"));
			return true;
		}
		String section = args.length < 1 ? "all" : args[0].toLowerCase();
		if(!SECTIONS.contains(section)) {
			p.sendMessage(TASUtils.msg("<red>Invalid section.  Valid sections: <v>", Placeholder.unparsed("v", String.join(" ", SECTIONS))));
			return true;
		}
		Map<String, Player> fakes = FakePlayerManager.getFakePlayers();
		if(fakes.size() < 5) {
			p.sendMessage(TASUtils.msg("<red>Could not run TAS!  Spawn the fakes first with /setup"));
			return true;
		}

		// Drops the last run's queued tasks; its held keys and jump timers aren't queued tasks, so stop those too.
		TASUtils.endRun();
		Actions.cancelAllMovement();
		if(M7Link.getBoolean("instructions.Server", "isRunStarted")) TASUtils.dispatchInRun(p, "m7:setup");
		// M7 refuses a run while anyone lacks a class. The server queues a chat command to run after this tick, so the
		// run starts next tick.
		fakes.forEach((name, fake) -> {
			Actions actions = new Actions(fake);
			// A chest GUI left open by the last run makes the server ignore every own-inventory click (swapItems).
			if(((org.bukkit.craftbukkit.entity.CraftPlayer) fake).getHandle().containerMenu.containerId != 0) actions.closeGui();
			actions.runCommand(name.equals("akc0303") ? "class archer" : "class mage");
		});
		// A tick before the run, so M7 never sees a fake off its start (out of bounds, aggro, plates).
		startPositions(section, fakes);
		TASUtils.scheduleTask(() -> start(p, section, fakes), 1);
		return true;
	}

	private static void start(Player p, String section, Map<String, Player> fakes) {
		if(!p.isOnline()) return;
		boolean clear = section.equals("all") || section.equals("clear");
		ScriptWatchers.watch();
		// Before the routines: it hands out the kits their first inputs use.
		if(practice(p, section, clear ? CLEAR_COUNTDOWN : COUNTDOWN)) {
			warnFiltered(p);
			teleportWatchers(p, section);
			startRoutines(section, fakes);
		} else ScriptWatchers.release();
	}

	private static void warnFiltered(Player p) {
		String filter = TASUtils.getVerboseFilter();
		if(filter == null || !TASUtils.isVerbose()) return;
		p.showTitle(Title.title(TASUtils.msg("<red>Verbose Filtered"), TASUtils.msg("<gray>Only logging <n>", Placeholder.unparsed("n", filter))));
		p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
	}

	/**
	 * M7 does the kits, countdown and bosses. Fixed settings, so every TAS run is the same game. --no-teleport: the
	 * fakes start where they stand. False if M7 refused (and said why).
	 */
	private static boolean practice(Player p, String section, int countdown) {
		TASUtils.dispatchInRun(p, "m7:m7practice " + section + " " + countdown + " " + SETTINGS + " --no-teleport");
		return M7Link.getBoolean("commands.Practice", "isActive");
	}

	private static void teleportWatchers(Player sender, String section) {
		if(!(M7Link.get("commands.Practice", "DEFAULT_LOCATIONS") instanceof Map<?, ?> spawns) || !(spawns.get(section) instanceof double[] loc)) return;
		Location spawn = new Location(sender.getWorld(), loc[0], loc[1], loc[2], (float) loc[3], (float) loc[4]);
		boolean boss = !section.equals("all") && !section.equals("clear");
		for(Player watcher : Bukkit.getOnlinePlayers()) {
			if(!watcher.getScoreboardTags().contains(TASUtils.WATCHING_TAG) || FakePlayerManager.getFakePlayers().containsValue(watcher)
					|| Spectate.getSpectatorMap().containsKey(watcher)) continue;
			if(M7Link.call("listeners.LavaJump", "isInBossArena", Location.class, watcher.getLocation()) instanceof Boolean inBoss && inBoss == boss) continue;
			watcher.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
		}
	}

	private static void startPositions(String section, Map<String, Player> fakes) {
		// Setup like the teleport: a fake that died last run is still in spectator.
		for(Player fake : fakes.values()) fake.setGameMode(org.bukkit.GameMode.ADVENTURE);
		new Archer(fakes.get("akc0303")).startPosition(section);
		new Mage(fakes.get("Beethoven")).startPosition(section);
		new Tank(fakes.get("cookiethebald")).startPosition(section);
		new Berserk(fakes.get("RedFish2471")).startPosition(section);
		new Healer(fakes.get("NoseThe")).startPosition(section);
	}

	private static void startRoutines(String section, Map<String, Player> fakes) {
		new Archer(fakes.get("akc0303")).archerInstructions(section);
		new Mage(fakes.get("Beethoven")).mageInstructions(section);
		new Tank(fakes.get("cookiethebald")).tankInstructions(section);
		new Berserk(fakes.get("RedFish2471")).berserkInstructions(section);
		new Healer(fakes.get("NoseThe")).healerInstructions(section);
	}
}
