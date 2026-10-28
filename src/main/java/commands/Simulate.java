package commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.world.inventory.ContainerInput;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import plugin.TASUtils;
import instructions.Actions;
import plugin.FakePlayerManager;

public class Simulate implements CommandExecutor {
	private static Player lastSimulated;
	private static Location lastSimulatedLocation;

	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
		if(!(sender instanceof Player p)) {
			sender.sendMessage(TASUtils.msg("Only players can run this"));
			return true;
		}

		if(args.length < 1) {
			p.sendMessage(TASUtils.msg("<red>Please specify a movement to simulate"));
			return true;
		}
		switch(args[0]) {
			case "undo" -> {
				if(lastSimulated == null || lastSimulatedLocation == null) {
					p.sendMessage(TASUtils.msg("<red>No previous movement to undo!"));
					return true;
				}
				lastSimulated.teleport(lastSimulatedLocation);
				lastSimulated = null;
				lastSimulatedLocation = null;
				p.sendMessage(TASUtils.msg("<green>Undid previous simulation instruction"));
				return true;
			}
			case "command" -> {
				Player applyTo = fakeArg(p, args, "<red>Please specify a player to run the command");
				if(applyTo == null) return true;
				if(args.length < 3) {
					p.sendMessage(TASUtils.msg("<red>Please specify the command to run"));
					return true;
				}
				String command = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
				new Actions(applyTo).runCommand(command);
				p.sendMessage(TASUtils.msg("<green><v0> ran /<v1>", Placeholder.unparsed("v0", applyTo.getName()), Placeholder.unparsed("v1", command)));
				return true;
			}
			case "gui" -> {
				Player applyTo = fakeArg(p, args, "<red>Please specify a player to click for");
				if(applyTo == null) return true;
				if(args.length < 3) {
					p.sendMessage(TASUtils.msg("<red>Please specify a slot"));
					return true;
				}
				int slot, button = 0;
				ContainerInput input = ContainerInput.PICKUP;
				try {
					slot = Integer.parseInt(args[2]);
					if(args.length >= 4) button = Integer.parseInt(args[3]);
					if(args.length >= 5) input = ContainerInput.valueOf(args[4].toUpperCase());
				} catch(Exception exception) {
					p.sendMessage(TASUtils.msg("<red>Usage: /simulate gui <player> <slot> [button] [pickup|quick_move|swap|clone|throw|pickup_all]"));
					return true;
				}
				new Actions(applyTo).clickGui(slot, button, input);
				p.sendMessage(TASUtils.msg("<green><v0> clicked slot <v1>", Placeholder.unparsed("v0", applyTo.getName()), Placeholder.unparsed("v1", String.valueOf(slot))));
				return true;
			}
			case "closegui" -> {
				Player applyTo = fakeArg(p, args, "<red>Please specify a player");
				if(applyTo == null) return true;
				new Actions(applyTo).closeGui();
				p.sendMessage(TASUtils.msg("<green><v0> closed their GUI", Placeholder.unparsed("v0", applyTo.getName())));
				return true;
			}
			case "move" -> {
				if(args.length < 2) {
					p.sendMessage(TASUtils.msg("<red>Please specify a player to apply the movement to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFake(args[1]);
				if(applyTo == null) {
					p.sendMessage(TASUtils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 3) {
					p.sendMessage(TASUtils.msg("<red>Please specify which keys are held down"));
					return true;
				}
				String inputString = args[2].toUpperCase();
				if(!inputString.matches("[WASDJPN]*")) {
					p.sendMessage(TASUtils.msg("<red>Bad characters detected!  Accepted characters: WASDJPN"));
					return true;
				}
				if(args.length < 4) {
					p.sendMessage(TASUtils.msg("<red>Must provide a valid duration"));
					return true;
				}
				int duration;
				try {
					duration = Integer.parseInt(args[3]);
				} catch(Exception exception) {
					p.sendMessage(TASUtils.msg("<red>Duration must be an integer"));
					return true;
				}
				if(duration <= 0) {
					p.sendMessage(TASUtils.msg("<red>Duration must be one or higher"));
					return true;
				}
				lastSimulated = applyTo;
				lastSimulatedLocation = applyTo.getLocation();
				// From the tick-end inputs, as a routine does: run between ticks, the release came due before the first step.
				TASUtils.scheduleTask(() -> new Actions(applyTo).move(inputString, duration), 1);
				p.sendMessage(TASUtils.msg("<green>Moved <v0> for <v1> ticks", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(duration))));
				return true;
			}
			case "click" -> {
				if(args.length < 2) {
					p.sendMessage(TASUtils.msg("<red>Please specify a player to apply the click to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFake(args[1]);
				if(applyTo == null) {
					p.sendMessage(TASUtils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 3) {
					p.sendMessage(TASUtils.msg("<red>Please specify which click is being simulated"));
					return true;
				}
				String click = args[2].toLowerCase();
				if(!click.equals("left") && !click.equals("right") && !click.equals("release")) {
					p.sendMessage(TASUtils.msg("<red>Invalid click specified.  Valid clicks: left, right, release"));
					return true;
				}
				lastSimulated = applyTo;
				lastSimulatedLocation = applyTo.getLocation();
				if(click.equals("left")) {
					new Actions(applyTo).leftClick();
				} else if(click.equals("right")) {
					new Actions(applyTo).rightClick();
				} else {
					new Actions(applyTo).stopRightClick();
				}
				p.sendMessage(TASUtils.msg("<green><v0> <v1> clicked", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(click))));
				return true;
			}
			case "turnhead" -> {
				if(args.length < 2) {
					p.sendMessage(TASUtils.msg("<red>Please specify a player to apply the turn to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFake(args[1]);
				if(applyTo == null) {
					p.sendMessage(TASUtils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 3) {
					p.sendMessage(TASUtils.msg("<red>Please specify a yaw"));
					return true;
				}
				if(args.length < 4) {
					p.sendMessage(TASUtils.msg("<red>Please specify a pitch"));
					return true;
				}
				float yaw, pitch;
				try {
					yaw = Float.parseFloat(args[2]);
					pitch = Float.parseFloat(args[3]);
				} catch(Exception exception) {
					p.sendMessage(TASUtils.msg("<red>Yaw and pitch must be numbers"));
					return true;
				}
				new Actions(applyTo).turnHead(yaw, pitch);
				p.sendMessage(TASUtils.msg("<green><v0> turned to yaw=<v1> pitch=<v2>", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(yaw)), Placeholder.unparsed("v2", String.valueOf(pitch))));
				return true;
			}
			case "hotbar" -> {
				if(args.length < 2) {
					p.sendMessage(TASUtils.msg("<red>Please specify a player to apply hotbar change to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFake(args[1]);
				if(applyTo == null) {
					p.sendMessage(TASUtils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 3) {
					p.sendMessage(TASUtils.msg("<red>Please specify which slot to change to"));
					return true;
				}
				int slot;
				try {
					slot = Integer.parseInt(args[2]);
				} catch(Exception exception) {
					p.sendMessage(TASUtils.msg("<red>Slot must be an integer"));
					return true;
				}
				if(slot < 0 || slot > 8) {
					p.sendMessage(TASUtils.msg("<red>Slot must be between 0 and 8"));
					return true;
				}
				new Actions(applyTo).setHotbarSlot(slot);
				p.sendMessage(TASUtils.msg("<green><v0> hotbar slot changed to <v1>", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(slot))));
				return true;
			}
			// Syntax: /simulate swapitems [player] [slotA] [slotB]
			// Swaps the contents of the two given inventory slots (0-40).
			case "swapitems" -> {
				if(args.length < 2) {
					p.sendMessage(TASUtils.msg("<red>Please specify a player to apply the swap to"));
					return true;
				}
				Player applyTo = FakePlayerManager.getFake(args[1]);
				if(applyTo == null) {
					p.sendMessage(TASUtils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", String.valueOf(args[1]))));
					return true;
				}
				if(args.length < 4) {
					p.sendMessage(TASUtils.msg("<red>Please specify two slots to swap"));
					return true;
				}
				int slotA, slotB;
				try {
					slotA = Integer.parseInt(args[2]);
					slotB = Integer.parseInt(args[3]);
				} catch(Exception exception) {
					p.sendMessage(TASUtils.msg("<red>Slots must be integers"));
					return true;
				}
				if(slotA < 0 || slotA > 40 || slotB < 0 || slotB > 40) {
					p.sendMessage(TASUtils.msg("<red>Slots must be between 0 and 40"));
					return true;
				}
				new Actions(applyTo).swapItems(slotA, slotB);
				p.sendMessage(TASUtils.msg("<green><v0> swapped slots <v1> and <v2>", Placeholder.unparsed("v0", String.valueOf(applyTo.getName())), Placeholder.unparsed("v1", String.valueOf(slotA)), Placeholder.unparsed("v2", String.valueOf(slotB))));
				return true;
			}
		}
		return false;
	}

	private static Player fakeArg(Player p, String[] args, String missing) {
		if(args.length < 2) {
			p.sendMessage(TASUtils.msg(missing));
			return null;
		}
		Player fake = FakePlayerManager.getFake(args[1]);
		if(fake == null) p.sendMessage(TASUtils.msg("<red>Player <v0> is not a fake player", Placeholder.unparsed("v0", args[1])));
		return fake;
	}
}
