package tas.nms;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import tas.commands.Spectate;

import java.lang.reflect.Field;

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

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg instanceof ClientboundContainerSetContentPacket pkt && pkt.containerId() == 0) {
			Player fakePlayer = Spectate.getSpectatorMap().get(player);
			if(fakePlayer instanceof CraftPlayer craftFake) {
				ServerPlayer nmsFake = craftFake.getHandle();
				msg = new ClientboundContainerSetContentPacket(
					0, pkt.stateId(),
					nmsFake.containerMenu.getItems(), nmsFake.containerMenu.getCarried()
				);
			}
		}
		super.write(ctx, msg, promise);
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
