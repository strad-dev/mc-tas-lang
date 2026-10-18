package tas.listeners;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tas.FakePlayerManager;
import tas.mcTASLang;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class FakeJoinListener implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent ev) {
		Bukkit.getScheduler().runTaskLater(mcTASLang.getInstance(), () -> {
			Player joiningPlayer = ev.getPlayer();
			if(!joiningPlayer.isOnline() || FakePlayerManager.getFakePlayers().containsValue(joiningPlayer)) {
				return;
			}

			ServerGamePacketListenerImpl conn = ((CraftPlayer) joiningPlayer).getHandle().connection;

			// Re-send each fake NPC's "add + spawn" packets just to this connection:
			for(Player fake : FakePlayerManager.getFakePlayers().values()) {
				// 1) NMS handles
				ServerPlayer npc = ((CraftPlayer) fake).getHandle();
				ServerLevel world = ((CraftWorld) Objects.requireNonNull(fake.getWorld())).getHandle();

				ServerEntity entry = new ServerEntity(world, npc, 0, false,
						new ServerEntity.Synchronizer() {
							@Override
							public void sendToTrackingPlayers(Packet<? super ClientGamePacketListener> packet) {
								// No-op for fake players
							}

							@Override
							public void sendToTrackingPlayersAndSelf(Packet<? super ClientGamePacketListener> packet) {
								// No-op for fake players
							}

							@Override
							public void sendToTrackingPlayersFiltered(Packet<? super ClientGamePacketListener> packet,
																	  Predicate<ServerPlayer> filter) {
								// No-op for fake players
							}
						}, new HashSet<>());

				// 2) Build the "ADD_PLAYER" info packet
				EnumSet<ClientboundPlayerInfoUpdatePacket.Action> addAction = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
				ClientboundPlayerInfoUpdatePacket addPkt = new ClientboundPlayerInfoUpdatePacket(addAction, List.of(npc));

				// 3) Build the spawn packet (uses the same entry you used at creation)
				ClientboundAddEntityPacket spawnPkt = new ClientboundAddEntityPacket(npc, entry);

				// 4) Send JUST to the joining player
				conn.send(addPkt);
				conn.send(spawnPkt);
			}
		}, 1);
	}
}
