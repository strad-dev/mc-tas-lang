package plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Copied from M7's {@code plugin.Utils}; M7 state read through {@link M7Link}. */
public class TASUtils {
	private static final MiniMessage MM = MiniMessage.miniMessage();

	/** M7's tag for a player who watches and is not in the run. */
	public static final String WATCHING_TAG = "m7_watching";

	public static Component msg(String s) {
		return MM.deserialize(s);
	}

	/** Use Placeholder.unparsed(...) for untrusted input (player names, chat) so it can't inject tags. */
	public static Component msg(String template, TagResolver... resolvers) {
		return MM.deserialize(template, resolvers);
	}

	/** Unstyled text of a component. */
	public static String plain(Component c) {
		return c == null ? "" : PlainTextComponentSerializer.plainText().serialize(c);
	}

	/**
	 * runTaskLater; delay in ticks. Dropped if an M7 run starts or ends in between (M7's schedule generation), so
	 * choreography can't bleed into the next run.
	 */
	public static void scheduleTask(Runnable task, long delay) {
		if(!mcTASLang.getInstance().isEnabled()) return;
		Object generation = scheduleGeneration();
		Bukkit.getScheduler().runTaskLater(mcTASLang.getInstance(), () -> {
			if(!Objects.equals(generation, scheduleGeneration())) return;
			task.run();
		}, delay);
	}

	private static Object scheduleGeneration() {
		return M7Link.get("plugin.Utils", "scheduleGeneration");
	}

	public static void broadcastPacket(Packet<?> pkt) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().connection.send(pkt);
		}
	}

	public enum DebugType {
		CLIENT, SERVER, BOSS, ERROR
	}

	/** M7's /verbose ON and SUPER. */
	public static boolean isVerbose() {
		return M7Link.getBoolean("plugin.Utils", "isVerbose");
	}

	public static boolean isSuperVerbose() {
		return M7Link.getBoolean("plugin.Utils", "isSuperVerbose");
	}

	private static int phaseTick() {
		return M7Link.getInt("plugin.Utils", "phaseTick", 0);
	}

	public static void debug(DebugType type, String message) {
		// Unparsed so a stray '<' in the payload can't break parsing or inject tags.
		TagResolver m = Placeholder.unparsed("m", message);
		// ERROR is a bug, not routine output: always fires, always tick-stamped.
		if(type == DebugType.ERROR) {
			Bukkit.broadcast(msg("<gray>[tick: " + phaseTick() + "] <red>[Error] <m>", m));
			return;
		}
		if(!isVerbose()) return;
		String prefix = isSuperVerbose() ? "<gray>[tick: " + phaseTick() + "] " : "";
		switch(type) {
			case CLIENT -> Bukkit.broadcast(msg(prefix + "<dark_aqua>[Client] <m>", m));
			case SERVER -> Bukkit.broadcast(msg(prefix + "<green>[Server] <m>", m));
			case BOSS -> Bukkit.broadcast(msg(prefix + "<light_purple>[Game] <m>", m));
			case ERROR -> { /* handled above */ }
		}
	}

	public static String round(double value, int places) {
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.toPlainString();
	}
}
