package commands;

import instructions.Actions;
import instructions.players.Archer;
import instructions.players.Berserk;
import instructions.players.Healer;
import instructions.players.Mage;
import instructions.players.Tank;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import plugin.FakePlayerManager;
import plugin.M7Link;
import plugin.TASUtils;

import java.util.List;
import java.util.Map;

public class TAS implements CommandExecutor {
	private static final List<String> SECTIONS = List.of("all", "clear", "boss", "maxor", "storm", "goldor", "necron", "witherking");
	private static final int COUNTDOWN = 60;
	private static final int CLEAR_COUNTDOWN = 100;
	// Difficulty, mayor, Fix Watcher Bug, Bad Bonzo, in /m7practice's own argument forms.
	private static final String SETTINGS = "perfect_rng jerry_paul off badbonzo=off";

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

		// M7 refuses a run while anyone lacks a class. A chat command runs on the spot, so this lands first.
		fakes.forEach((name, fake) -> new Actions(fake).runCommand(name.equals("Archer") ? "class archer" : "class mage"));

		// M7 does the kits, countdown and bosses; the routines start this same tick. Fixed settings, so every TAS
		// run is the same game. --no-teleport: the fakes start where they stand.
		int countdown = section.equals("all") || section.equals("clear") ? CLEAR_COUNTDOWN : COUNTDOWN;
		Bukkit.dispatchCommand(p, "m7:m7practice " + section + " " + countdown + " " + SETTINGS + " --no-teleport");
		if(!M7Link.getBoolean("commands.Practice", "isActive")) return true; // M7 refused (and said why)

		new Archer(fakes.get("Archer")).archerInstructions(section);
		new Mage(fakes.get("Mage1")).mageInstructions(section);
		new Tank(fakes.get("Mage2")).tankInstructions(section);
		new Berserk(fakes.get("Mage3")).berserkInstructions(section);
		new Healer(fakes.get("Mage4")).healerInstructions(section);
		return true;
	}
}
