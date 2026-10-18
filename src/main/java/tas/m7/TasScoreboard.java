package tas.m7;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import plugin.Utils;

public class TasScoreboard {
	public static void print() {
		Utils.timer("<green>Wither King finished in " + formatWithSpaces(Utils.phaseTick()) + " ticks!");
		Bukkit.broadcast(Utils.msg("<green><bold>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
		Bukkit.broadcast(Utils.msg("                <red>Master Mode The Catacombs <dark_gray>- <yellow>Floor VII"));
		Bukkit.broadcast(Utils.msg(""));
		Bukkit.broadcast(Utils.msg("                           <white>Team Score: <green>308 <white>(<aqua><bold>S+</bold><white>)"));
		Bukkit.broadcast(Utils.msg(" <red>☠ <yellow>Defeated <red>Maxor, Storm, Goldor, and Necron <yellow>in <green>4000 ticks"));
		Bukkit.broadcast(Utils.msg("                         <green>200.00 seconds | 3:20.00"));
		Bukkit.broadcast(Utils.msg(""));
		Bukkit.broadcast(Utils.msg("                              <gold>> <yellow><bold>EXTRA INFO </bold><gold>\\<"));
		Bukkit.broadcast(Utils.msg("                                   <green><bold>SPLITS"));
		Bukkit.broadcast(Utils.msg("    <blue><bold>Clear</bold><white>: 738 ticks | <aqua><bold>Maxor</bold><white>: 500 ticks | <red><bold>Storm</bold><white>: 860 ticks"));
		Bukkit.broadcast(Utils.msg(" <yellow><bold>Terminals</bold><white>: 200 ticks | <gold><bold>Goldor</bold><white>: 104 ticks | <dark_red><bold>Necron</bold><white>: 600 ticks"));
		Bukkit.broadcast(Utils.msg("                          <gray><bold>Wither King</bold><white>: 998 ticks"));
		Bukkit.broadcast(Utils.msg(""));
		Bukkit.broadcast(Utils.msg("     <green><bold>TAS by </bold><aqua>Stradivarius Violin<green>, also known as <aqua>Beethoven_"));
		Bukkit.broadcast(Utils.msg("    <red><bold>YOUTUBE</bold><aqua>: <click:open_url:'https://www.youtube.com/@Stradivarius_Violin'><u>https://www.youtube.com/@Stradivarius_Violin</u></click>"));
		Bukkit.broadcast(Utils.msg("               <blue><bold>DISCORD</bold><aqua>: <click:open_url:'https://discord.gg/gNfPwa8'><u>https://discord.gg/gNfPwa8</u></click>"));
		Bukkit.broadcast(Utils.msg("<green><bold>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));

		Utils.playGlobalSound(Sound.ENTITY_PLAYER_LEVELUP);
		Utils.scheduleTask(() -> Utils.playGlobalSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 2f, 1f), 1);
	}

	private static String formatWithSpaces(int n) {
		StringBuilder sb = new StringBuilder();
		String s = String.valueOf(n);
		for(int i = 0; i < s.length(); i++) {
			if(i > 0 && (s.length() - i) % 3 == 0) sb.append(' ');
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}
}
