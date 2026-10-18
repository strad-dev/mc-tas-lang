package tas.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import plugin.Utils;
import tas.Actions;
import tas.FakePlayerManager;

public class Simulate implements CommandExecutor {
	private static Player lastSimulated;
	private static Location lastSimulatedLocation;

	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
		if(!(sender instanceof Player p)) {
			sender.sendMessage(Utils.msg("Only players can run this"));
			return true;
		}

		if(args.length < 1) {
			p.sendMessage(Utils.msg("<red>Please specify a movement to simulate"));
			return true;
		}
		switch(args[0]) {
			case "undo" -> {
				if(lastSimulated == null || lastSimulatedLocation == null) {
					p.sendMessage(Utils.msg("<red>No previous movement to undo!"));
					return true;
				}
				lastSimulated.teleport(lastSimulatedLocation);
				lastSimulated = null;
				lastSimulatedLocation = null;
				p.sendMessage(Utils.msg("<green>Undid previous simulation instruction"));
				return true;
			}
			case "allblessings" -> {
				Utils.broadcastBlessing(p, Utils.BlessingType.LIFE, 1);
				Utils.broadcastBlessing(p, Utils.BlessingType.LIFE, 2);
				Utils.broadcastBlessing(p, Utils.BlessingType.LIFE, 5);
				Utils.broadcastBlessing(p, Utils.BlessingType.POWER, 1);
				Utils.broadcastBlessing(p, Utils.BlessingType.POWER, 2);
				Utils.broadcastBlessing(p, Utils.BlessingType.POWER, 5);
				Utils.broadcastBlessing(p, Utils.BlessingType.STONE, 1);
				Utils.broadcastBlessing(p, Utils.BlessingType.STONE, 2);
				Utils.broadcastBlessing(p, Utils.BlessingType.STONE, 5);
				Utils.broadcastBlessing(p, Utils.BlessingType.WISDOM, 1);
				Utils.broadcastBlessing(p, Utils.BlessingType.WISDOM, 2);
				Utils.broadcastBlessing(p, Utils.BlessingType.WISDOM, 5);
				Utils.broadcastBlessing(p, Utils.BlessingType.TIME, 5);
				return true;
			}
			// Syntax: /simulate move [player] [set from: {W, A, S, D, J, P, N}] [ticks]
			// WASD - self explainatory
			// J - jump
			// P - sprint
			// N - sneak
			// ticks - number of ticks to hold down, must be greater than zero
			// Example: /simulate move WDP 5
			// Moves the plaer forward and to the right while sprinting for 5 ticks
			case "move" -> {
				if(args.length < 2) {
					p.sendMessage(Utils.msg("<red>Please specify a player to apply the movement to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFakePlayers().get(Character.toUpperCase(args[1].charAt(0)) + args[1].substring(1).toLowerCase());
				if(applyTo == null) {
					p.sendMessage(Utils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 3) {
					p.sendMessage(Utils.msg("<red>Please specify which keys are held down"));
					return true;
				}
				String inputString = args[2].toUpperCase();
				if(!inputString.matches("[WASDJPN]*")) {
					p.sendMessage(Utils.msg("<red>Bad characters detected!  Accepted characters: WASDJPN"));
					return true;
				}
				if(args.length < 4) {
					p.sendMessage(Utils.msg("<red>Must provide a valid duration"));
					return true;
				}
				int duration;
				try {
					duration = Integer.parseInt(args[3]);
				} catch(Exception exception) {
					p.sendMessage(Utils.msg("<red>Duration must be an integer"));
					return true;
				}
				if(duration <= 0) {
					p.sendMessage(Utils.msg("<red>Duration must be one or higher"));
					return true;
				}
				lastSimulated = applyTo;
				lastSimulatedLocation = applyTo.getLocation();
				Actions.move(applyTo, inputString, duration);
				p.sendMessage(Utils.msg("<green>Moved <v0> for <v1> ticks", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(duration))));
				return true;
			}
			case "click" -> {
				if(args.length < 2) {
					p.sendMessage(Utils.msg("<red>Please specify a player to apply the click to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFakePlayers().get(Character.toUpperCase(args[1].charAt(0)) + args[1].substring(1).toLowerCase());
				if(applyTo == null) {
					p.sendMessage(Utils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 3) {
					p.sendMessage(Utils.msg("<red>Please specify which click is being simulated"));
					return true;
				}
				String click = args[2].toLowerCase();
				if(!click.equals("left") && !click.equals("right") && !click.equals("release")) {
					p.sendMessage(Utils.msg("<red>Invalid click specified.  Valid clicks: left, right, release"));
					return true;
				}
				lastSimulated = applyTo;
				lastSimulatedLocation = applyTo.getLocation();
				if(click.equals("left")) {
					Actions.leftClick(applyTo);
				} else if(click.equals("right")) {
					Actions.rightClick(applyTo);
				} else {
					Actions.stopRightClick(applyTo);
				}
				p.sendMessage(Utils.msg("<green><v0> <v1> clicked", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(click))));
				return true;
			}
			case "turnhead" -> {
				if(args.length < 2) {
					p.sendMessage(Utils.msg("<red>Please specify a player to apply the turn to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFakePlayers().get(Character.toUpperCase(args[1].charAt(0)) + args[1].substring(1).toLowerCase());
				if(applyTo == null) {
					p.sendMessage(Utils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 3) {
					p.sendMessage(Utils.msg("<red>Please specify a yaw"));
					return true;
				}
				if(args.length < 4) {
					p.sendMessage(Utils.msg("<red>Please specify a pitch"));
					return true;
				}
				float yaw, pitch;
				try {
					yaw = Float.parseFloat(args[2]);
					pitch = Float.parseFloat(args[3]);
				} catch(Exception exception) {
					p.sendMessage(Utils.msg("<red>Yaw and pitch must be numbers"));
					return true;
				}
				Actions.turnHead(applyTo, yaw, pitch);
				p.sendMessage(Utils.msg("<green><v0> turned to yaw=<v1> pitch=<v2>", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(yaw)), Placeholder.unparsed("v2", String.valueOf(pitch))));
				return true;
			}
			case "hotbar" -> {
				if(args.length < 2) {
					p.sendMessage(Utils.msg("<red>Please specify a player to apply hotbar change to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFakePlayers().get(Character.toUpperCase(args[1].charAt(0)) + args[1].substring(1).toLowerCase());
				if(applyTo == null) {
					p.sendMessage(Utils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 3) {
					p.sendMessage(Utils.msg("<red>Please specify which slot to change to"));
					return true;
				}
				int slot;
				try {
					slot = Integer.parseInt(args[2]);
				} catch(Exception exception) {
					p.sendMessage(Utils.msg("<red>Slot must be an integer"));
					return true;
				}
				if(slot < 0 || slot > 8) {
					p.sendMessage(Utils.msg("<red>Slot must be between 0 and 8"));
					return true;
				}
				Actions.setHotbarSlot(applyTo, slot);
				p.sendMessage(Utils.msg("<green><v0> hotbar slot changed to <v1>", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(slot))));
				return true;
			}
			// Syntax: /simulate swapitems [player] [slotA] [slotB]
			// Swaps the contents of the two given inventory slots (0-40).
			case "swapitems" -> {
				if(args.length < 2) {
					p.sendMessage(Utils.msg("<red>Please specify a player to apply the swap to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFakePlayers().get(Character.toUpperCase(args[1].charAt(0)) + args[1].substring(1).toLowerCase());
				if(applyTo == null) {
					p.sendMessage(Utils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 4) {
					p.sendMessage(Utils.msg("<red>Please specify two slots to swap"));
					return true;
				}
				int slotA, slotB;
				try {
					slotA = Integer.parseInt(args[2]);
					slotB = Integer.parseInt(args[3]);
				} catch(Exception exception) {
					p.sendMessage(Utils.msg("<red>Slots must be integers"));
					return true;
				}
				if(slotA < 0 || slotA > 40 || slotB < 0 || slotB > 40) {
					p.sendMessage(Utils.msg("<red>Slots must be between 0 and 40"));
					return true;
				}
				Actions.swapItems(applyTo, slotA, slotB);
				p.sendMessage(Utils.msg("<green><v0> swapped slots <v1> and <v2>", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(slotA)), Placeholder.unparsed("v2", String.valueOf(slotB))));
				return true;
			}
		}
		return false;
	}
}