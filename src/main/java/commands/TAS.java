package commands;

import instructions.Actions;
import instructions.players.Archer;
import instructions.players.Berserk;
import instructions.players.Healer;
import instructions.players.Mage;
import instructions.players.Tank;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
	/** Temporarily 126 (the routines' tick 0 is 26 before Mort's start button) until the clear is redone; then 100. */
	private static final int CLEAR_COUNTDOWN = 126;
	// Difficulty, mayor, Fix Watcher Bug, Bad Bonzo, in /m7practice's own argument forms.
	private static final String SETTINGS = "perfect_rng jerry_paul off badbonzo=off";

	/** Chained run (all, boss): ticks from a segment's start to the next's. A routine may pass its own instead. */
	public static final int CLEAR_TICKS = 742;
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
			p.sendMessage(TASUtils.msg("<red>Could not run TAS!  Spawn the fakes first with /spawnfakes"));
			return true;
		}

		TASUtils.endRun();
		// M7 refuses a run while anyone lacks a class. The server queues a chat command to run after this tick, so the
		// run starts next tick.
		fakes.forEach((name, fake) -> {
			Actions actions = new Actions(fake);
			// A chest GUI left open by the last run makes the server ignore every own-inventory click (swapItems).
			if(((org.bukkit.craftbukkit.entity.CraftPlayer) fake).getHandle().containerMenu.containerId != 0) actions.closeGui();
			actions.runCommand(name.equals("akc0303") ? "class archer" : "class mage");
		});
		TASUtils.scheduleTask(() -> start(p, section, fakes), 1);
		return true;
	}

	private static void start(Player p, String section, Map<String, Player> fakes) {
		if(!p.isOnline()) return;
		boolean clear = section.equals("all") || section.equals("clear");
		ScriptWatchers.watch();
		// Before the routines: it hands out the kits their first inputs use.
		if(practice(p, section, clear ? CLEAR_COUNTDOWN : COUNTDOWN)) startRoutines(section, fakes);
		else ScriptWatchers.release();
	}

	/**
	 * M7 does the kits, countdown and bosses. Fixed settings, so every TAS run is the same game. --no-teleport: the
	 * fakes start where they stand. False if M7 refused (and said why).
	 */
	private static boolean practice(Player p, String section, int countdown) {
		TASUtils.dispatchInRun(p, "m7:m7practice " + section + " " + countdown + " " + SETTINGS + " --no-teleport");
		return M7Link.getBoolean("commands.Practice", "isActive");
	}

	private static void startRoutines(String section, Map<String, Player> fakes) {
		new Archer(fakes.get("akc0303")).archerInstructions(section);
		new Mage(fakes.get("Beethoven_")).mageInstructions(section);
		new Tank(fakes.get("cookiethebald")).tankInstructions(section);
		new Berserk(fakes.get("RedFish2471")).berserkInstructions(section);
		new Healer(fakes.get("NoseThe")).healerInstructions(section);
	}
}
