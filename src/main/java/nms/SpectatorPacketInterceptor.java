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
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
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

public class SpectatorPacketInterceptor extends ChannelDuplexHandler {
	private static final String NAME = "tas_spectator";
	private final Player player;

	public SpectatorPacketInterceptor(Player player) {
		this.player = player;
	}

	public static void install(Player p) {
		try {
			Channel ch = getChannel(p);
			if(ch.pipeline().get(NAME) == null) ch.pipeline().addBefore("packet_handler", NAME, new SpectatorPacketInterceptor(p));
		} catch(Exception ignored) {
		}
	}

	public static void uninstall(Player p) {
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

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(isDuplicateSound(msg)) {
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
