package nms;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import commands.Spectate;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpectatorPacketInterceptor extends ChannelDuplexHandler {
	private static final String NAME = "tas_spectator";
	private static final Map<Player, SpectatorPacketInterceptor> INSTANCES = new ConcurrentHashMap<>();
	private final Player player;

	public SpectatorPacketInterceptor(Player player) {
		this.player = player;
	}

	public static void install(Player p) {
		try {
			Channel ch = getChannel(p);
			if(ch.pipeline().get(NAME) == null) {
				SpectatorPacketInterceptor interceptor = new SpectatorPacketInterceptor(p);
				ch.pipeline().addBefore("packet_handler", NAME, interceptor);
				INSTANCES.put(p, interceptor);
			}
		} catch(Exception ignored) {
		}
	}

	public static void uninstall(Player p) {
		INSTANCES.remove(p);
		try {
			Channel ch = getChannel(p);
			if(ch.pipeline().get(NAME) != null) ch.pipeline().remove(NAME);
		} catch(Exception ignored) {
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof ServerboundMovePlayerPacket pkt) {
			if(Spectate.getSpectatorMap().containsKey(player)) {
				if(pkt.hasPosition()) {
					Spectate.updateClientPosition(player, pkt.getX(0), pkt.getY(0), pkt.getZ(0));
				}
				return;
			}
		}
		if(actsInMirroredScreen(msg)) return;
		if(msg instanceof ServerboundSetCarriedItemPacket) {
			Player fakePlayer = Spectate.getSpectatorMap().get(player);
			if(fakePlayer != null) {
				((CraftPlayer) player).getHandle().connection.send(
					new ClientboundSetHeldSlotPacket(fakePlayer.getInventory().getHeldItemSlot()));
				return;
			}
		}
		super.channelRead(ctx, msg);
	}

	/** Sound packets written lately; one sent both directly and via {@code SpectatorSync.mirrorSound} plays once. */
	private final ArrayDeque<Object> recentSounds = new ArrayDeque<>();
	private static final int RECENT_SOUNDS = 64;

	private boolean isDuplicateSound(Object msg) {
		if(!(msg instanceof ClientboundSoundPacket || msg instanceof ClientboundSoundEntityPacket
				|| msg instanceof ClientboundStopSoundPacket)) return false;
		synchronized(recentSounds) {
			for(Object seen : recentSounds) if(seen == msg) return true;
			recentSounds.addLast(msg);
			if(recentSounds.size() > RECENT_SOUNDS) recentSounds.removeFirst();
			return false;
		}
	}

	/** The fake's HUD packets sent to this watcher ({@link #mirror}); any other HUD packet is the watcher's own. */
	private final ArrayDeque<Object> mirrors = new ArrayDeque<>();
	/** System chat lately written, by content: a broadcast reaches the watcher directly and mirrored, as two instances. */
	private final Map<Object, Long> recentChat = new HashMap<>();
	private static final long CHAT_DEDUPE_NANOS = 100_000_000L;

	/** Sends the fake's HUD packet to {@code watcher}, past the filter that drops the watcher's own. */
	public static void mirror(Player watcher, Packet<?> packet) {
		SpectatorPacketInterceptor interceptor = INSTANCES.get(watcher);
		if(interceptor == null) return;
		synchronized(interceptor.mirrors) {
			interceptor.mirrors.addLast(packet);
			if(interceptor.mirrors.size() > RECENT_SOUNDS) interceptor.mirrors.removeFirst();
		}
		((CraftPlayer) watcher).getHandle().connection.send(packet);
	}

	private boolean takeMirror(Object msg) {
		synchronized(mirrors) {
			for(Iterator<Object> it = mirrors.iterator(); it.hasNext(); ) {
				if(it.next() == msg) {
					it.remove();
					return true;
				}
			}
		}
		return false;
	}

	private boolean isRepeatChat(ClientboundSystemChatPacket chat) {
		long now = System.nanoTime();
		synchronized(recentChat) {
			recentChat.values().removeIf(t -> now - t > CHAT_DEDUPE_NANOS);
			return recentChat.putIfAbsent(chat.content(), now) != null;
		}
	}

	/** While spectating: HUD only from the fake; system chat from both, once. */
	private boolean isUnwantedHud(Object msg) {
		if(!Spectate.getSpectatorMap().containsKey(player) || !plugin.SpectatorSync.isHud(msg)) return false;
		boolean mirrored = takeMirror(msg);
		if(msg instanceof ClientboundSystemChatPacket chat && !chat.overlay()) return isRepeatChat(chat);
		return !mirrored;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(isDuplicateSound(msg) || isUnwantedHud(msg)) {
			promise.trySuccess();
			return;
		}
		if(msg instanceof ClientboundContainerSetContentPacket pkt && pkt.containerId() == 0) {
			Player fakePlayer = Spectate.getSpectatorMap().get(player);
			if(fakePlayer instanceof CraftPlayer craftFake) {
				ServerPlayer nmsFake = craftFake.getHandle();
				msg = new ClientboundContainerSetContentPacket(
					0, pkt.stateId(),
					nmsFake.inventoryMenu.getItems(), nmsFake.containerMenu.getCarried()
				);
			}
		}
		super.write(ctx, msg, promise);
	}

	/**
	 * Clicks, closes and edits aimed at the fake's screen (its container id, not one the watcher has open), plus
	 * trade/rename/sign edits while one is shown. The server would otherwise apply them to the watcher.
	 */
	private boolean actsInMirroredScreen(Object msg) {
		Player fake = Spectate.getSpectatorMap().get(player);
		if(fake == null) return false;
		int own = ((CraftPlayer) player).getHandle().containerMenu.containerId;
		Integer id = msg instanceof ServerboundContainerClickPacket p ? p.containerId()
				: msg instanceof ServerboundContainerButtonClickPacket p ? p.containerId()
				: msg instanceof ServerboundContainerClosePacket p ? p.getContainerId()
				: msg instanceof ServerboundPlaceRecipePacket p ? p.containerId()
				: msg instanceof ServerboundContainerSlotStateChangedPacket p ? p.containerId()
				: null;
		if(id != null) return id != 0 && id != own;
		boolean screenEdit = msg instanceof ServerboundSelectTradePacket || msg instanceof ServerboundRenameItemPacket
				|| msg instanceof ServerboundSignUpdatePacket;
		return screenEdit && plugin.SpectatorSync.hasOpenScreen(fake);
	}

	private static Channel getChannel(Player player) throws Exception {
		ServerPlayer nms = ((CraftPlayer) player).getHandle();
		Field connField = ServerCommonPacketListenerImpl.class.getDeclaredField("connection");
		connField.setAccessible(true);
		Connection conn = (Connection) connField.get(nms.connection);
		Field channelField = Connection.class.getDeclaredField("channel");
		channelField.setAccessible(true);
		return (Channel) channelField.get(conn);
	}
}
