package nms;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.LookAt;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TeleportRotation {
	private static Method teleportToPos;

	public static void install() {
		CommandDispatcher<CommandSourceStack> dispatcher = MinecraftServer.getServer().getCommands().getDispatcher();
		for(String name : List.of("teleport", "minecraft:teleport")) {
			CommandNode<CommandSourceStack> teleport = dispatcher.getRoot().getChild(name);
			CommandNode<CommandSourceStack> location = teleport == null ? null : teleport.getChild("location");
			if(location == null || location.getChild("rotation") != null) continue;
			location.addChild(Commands.argument("rotation", RotationArgument.rotation()).executes(TeleportRotation::run).build());
		}
	}

	public static void reinstall() {
		install();
		for(Player p : Bukkit.getOnlinePlayers()) p.updateCommands();
	}

	private static int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSourceStack source = ctx.getSource();
		Collection<?> self = Collections.singleton(source.getEntityOrException());
		try {
			if(teleportToPos == null) {
				teleportToPos = TeleportCommand.class.getDeclaredMethod("teleportToPos", CommandSourceStack.class, Collection.class,
						ServerLevel.class, Coordinates.class, Coordinates.class, LookAt.class);
				teleportToPos.setAccessible(true);
			}
			return (int) teleportToPos.invoke(null, source, self, source.getLevel(), Vec3Argument.getCoordinates(ctx, "location"),
					RotationArgument.getRotation(ctx, "rotation"), null);
		} catch(InvocationTargetException e) {
			if(e.getCause() instanceof CommandSyntaxException cause) throw cause;
			throw new RuntimeException(e.getCause());
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
