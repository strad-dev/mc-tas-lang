package plugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.ProfileResult;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import instructions.Actions;

public class FakePlayerManager {
	private static final Map<String, Player> fakePlayers = new HashMap<>();
	private static boolean fakeTickerStarted = false;

	// Each fake's inbound pipeline. Simulated packets enter here, so the server sees them exactly like a real
	// client's (M7 installs its interceptor before "packet_handler" on join).
	private static final Map<UUID, EmbeddedChannel> channels = new HashMap<>();

	private static final Map<String, String> SKIN_DATA = Map.of("Archer", "0b0fa6bc-69ee-4f6c-a4f8-7cac79f1871a", "Mage3", "f0b1d2e5-dfd2-4d33-9768-4d2b259a4743", "Mage4", "6715b245-be6e-496c-87eb-1d2c19066403", "Mage1", "cdb9e9c6-c096-4f58-9c49-35395d7b897c", "Mage2", "5d142c3a-bdf1-418b-b907-797bbaaed188");

	public static Map<String, Player> getFakePlayers() {
		return fakePlayers;
	}

	public static void spawnAllFakes(World world) {
		kickAllFakes();
		fakePlayers.clear();

		for(var entry : SKIN_DATA.entrySet()) {
			String role = entry.getKey();
			String skin = entry.getValue();

			Player fake = spawnFakePlayer(world, role, UUID.fromString(skin));
			fakePlayers.put(role, fake);
		}
		startFakePlayerTicker();
	}

	public static Player spawnFakePlayer(World world, String fakePlayerName, UUID skinOwner) {
		MinecraftServer nmsServer = ((CraftServer) mcTASLang.getInstance().getServer()).getServer();
		ServerLevel nmsWorld = ((CraftWorld) world).getHandle();

		// Stable per name: M7 keys class and loadout by UUID, so a random one left new files every spawn.
		UUID id = UUID.nameUUIDFromBytes(("mc-tas-lang:" + fakePlayerName).getBytes(StandardCharsets.UTF_8));
		GameProfile profile = buildFakeProfileWithSkin(id, fakePlayerName, skinOwner);

		// View distance 0 - ChunkMap.getPlayerViewDistance clamps to min 2 internally,
		// but this still drops the per-fake chunk load from 17x17=289 to 5x5=25.
		ClientInformation clientInfo = new ClientInformation("en_us", 0, ChatVisiblity.FULL, true, 0x7F, HumanoidArm.RIGHT, false, false, ParticleStatus.MINIMAL);
		ServerPlayer nmsPlayer = new ServerPlayer(nmsServer, nmsWorld, profile, clientInfo);
		try {
			java.lang.reflect.Field firstTick = net.minecraft.world.entity.Entity.class.getDeclaredField("firstTick");
			firstTick.setAccessible(true);
			firstTick.set(nmsPlayer, false);
		} catch (Exception e) {
			throw new RuntimeException("Failed to clear firstTick on fake player", e);
		}

		EmbeddedChannel channel = new EmbeddedChannel();
		channel.pipeline().addLast("packet_handler", new ServerboundDispatcher(nmsPlayer));
		Connection nm = new Connection(PacketFlow.SERVERBOUND) {
			{
				this.channel = channel;
				this.address = new InetSocketAddress("127.0.0.1", 0);
			}

			// The listener sends through this overload; the fake has no client, so nothing is written. A real client
			// confirms teleports and reports loaded after a respawn, and 26.2 ignores interactions until it does.
			@Override
			public void send(Packet<?> packet, ChannelFutureListener listener, boolean flush) {
				answer(nmsPlayer, packet);
			}

			@Override
			public boolean isConnected() {
				return true;
			}
		};
		CommonListenerCookie cookie = CommonListenerCookie.createInitial(profile, false);

		org.bukkit.Location spawn = world.getSpawnLocation();
		nmsPlayer.setPos(spawn.getX(), spawn.getY(), spawn.getZ());
		nmsPlayer.setNoGravity(false);

		nmsServer.getPlayerList().placeNewPlayer(nm, nmsPlayer, cookie);
		channels.put(nmsPlayer.getUUID(), channel);
		reply(nmsPlayer, new ServerboundPlayerLoadedPacket());

		return nmsPlayer.getBukkitEntity();
	}

	/** Feeds a serverbound packet into {@code fake}'s pipeline. False if it isn't one of ours. */
	public static boolean receive(Player fake, Packet<?> packet) {
		EmbeddedChannel channel = channels.get(fake.getUniqueId());
		if(channel == null) return false;
		channel.pipeline().fireChannelRead(packet);
		if(packet instanceof ServerboundContainerClosePacket close) {
			SpectatorSync.fakeClosedScreen(((CraftPlayer) fake).getHandle(), close.getContainerId());
		}
		return true;
	}

	private static void answer(ServerPlayer fake, Packet<?> packet) {
		if(packet instanceof BundlePacket<?> bundle) {
			for(Packet<?> sub : bundle.subPackets()) answer(fake, sub);
		} else if(packet instanceof ClientboundPlayerPositionPacket position) {
			reply(fake, new ServerboundAcceptTeleportationPacket(position.id()));
		} else {
			SpectatorSync.mirrorScreen(fake, packet);
			if(packet instanceof ClientboundRespawnPacket) reply(fake, new ServerboundPlayerLoadedPacket());
		}
	}

	// Next tick, as a client's reply would be; never re-enters the listener mid-send.
	private static void reply(ServerPlayer fake, Packet<?> packet) {
		Bukkit.getScheduler().runTask(mcTASLang.getInstance(), () -> {
			if(!fake.isRemoved()) receive(fake.getBukkitEntity(), packet);
		});
	}

	/** End of a fake's pipeline: hands each packet to its listener, as a real Connection does. */
	private static final class ServerboundDispatcher extends ChannelInboundHandlerAdapter {
		private final ServerPlayer fake;

		ServerboundDispatcher(ServerPlayer fake) {
			this.fake = fake;
		}

		@Override
		@SuppressWarnings({"unchecked", "rawtypes"})
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			if(!(msg instanceof Packet packet)) return;
			try {
				packet.handle(fake.connection);
			} catch(Exception e) {
				mcTASLang.getInstance().getLogger().warning(fake.getPlainTextName() + " " + packet.getClass().getSimpleName() + ": " + e);
			}
		}
	}

	private static final long SKIN_CACHE_TTL_MS = 24L * 60L * 60L * 1000L;
	private static Map<UUID, CachedSkin> skinCache = null;

	private record CachedSkin(String value, String signature, long fetchedAt) {
		boolean isFresh() {
			return System.currentTimeMillis() - fetchedAt < SKIN_CACHE_TTL_MS;
		}
	}

	public static GameProfile buildFakeProfileWithSkin(UUID fakeUuid, String fakeName, UUID skinOwnerUuid) {
		if(skinCache == null) {
			skinCache = loadSkinCache();
		}

		CachedSkin cached = skinCache.get(skinOwnerUuid);
		Property texture;
		if(cached != null && cached.isFresh()) {
			texture = new Property("textures", cached.value(), cached.signature());
		} else {
			MinecraftServer nms = ((CraftServer) Bukkit.getServer()).getServer();
			ProfileResult result = nms.services().sessionService().fetchProfile(skinOwnerUuid, true);
			if(result == null) {
				throw new IllegalStateException("Failed to fetch profile for " + skinOwnerUuid);
			}
			var fetched = result.profile().properties().get("textures").iterator();
			if(!fetched.hasNext()) {
				throw new IllegalStateException("Profile for " + skinOwnerUuid + " has no textures property");
			}
			texture = fetched.next();
			skinCache.put(skinOwnerUuid, new CachedSkin(texture.value(), texture.signature(), System.currentTimeMillis()));
			saveSkinCache(skinCache);
		}

		Multimap<String, Property> properties = HashMultimap.create();
		properties.put("textures", texture);
		return new GameProfile(fakeUuid, fakeName, new PropertyMap(properties));
	}

	private static File getSkinCacheFile() {
		File folder = mcTASLang.getInstance().getDataFolder();
		if(!folder.exists()) {
			folder.mkdirs();
		}
		return new File(folder, "skins.json");
	}

	private static Map<UUID, CachedSkin> loadSkinCache() {
		Map<UUID, CachedSkin> out = new HashMap<>();
		File file = getSkinCacheFile();
		if(!file.exists()) {
			return out;
		}
		try {
			String raw = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
			JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
			for(var entry : root.entrySet()) {
				JsonObject obj = entry.getValue().getAsJsonObject();
				out.put(UUID.fromString(entry.getKey()), new CachedSkin(obj.get("value").getAsString(), obj.has("signature") && !obj.get("signature").isJsonNull() ? obj.get("signature").getAsString() : null, obj.get("fetchedAt").getAsLong()));
			}
		} catch (Exception e) {
			mcTASLang.getInstance().getLogger().warning("Failed to load skin cache, starting empty: " + e.getMessage());
		}
		return out;
	}

	private static void saveSkinCache(Map<UUID, CachedSkin> cache) {
		JsonObject root = new JsonObject();
		for(var entry : cache.entrySet()) {
			JsonObject obj = new JsonObject();
			obj.addProperty("value", entry.getValue().value());
			obj.addProperty("signature", entry.getValue().signature());
			obj.addProperty("fetchedAt", entry.getValue().fetchedAt());
			root.add(entry.getKey().toString(), obj);
		}
		try {
			Files.write(getSkinCacheFile().toPath(), root.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			mcTASLang.getInstance().getLogger().warning("Failed to save skin cache: " + e.getMessage());
		}
	}

	public static void kickAllFakes() {
		fakePlayers.values().forEach(p -> {
			SpectatorSync.forgetScreen(p);
			if(p.isOnline()) {
				p.kickPlayer("");
			}
		});
		fakePlayers.clear();
		channels.clear();
	}

	private static void startFakePlayerTicker() {
		if(fakeTickerStarted) {
			return;
		}
		fakeTickerStarted = true;

		Method updateFluidInteraction;
		try {
			updateFluidInteraction = net.minecraft.world.entity.Entity.class
					.getDeclaredMethod("updateFluidInteraction");
			updateFluidInteraction.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Failed to find updateFluidInteraction", e);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player fake : fakePlayers.values()) {
					if(!(fake instanceof CraftPlayer)) continue;
					ServerPlayer npc = ((CraftPlayer) fake).getHandle();
					npc.setNoGravity(false);
					npc.updatePlayerPose();
					// Re-assert movement fields before aiStep() - zza/xxa decay by 0.98x/tick otherwise
					String input = Actions.getActiveInput(fake.getUniqueId());
					if(!input.isEmpty()) {
						npc.xxa = (input.contains("A") && !input.contains("D")) ? 1.0F : (!input.contains("A") && input.contains("D")) ? -1.0F : 0.0F;
						npc.zza = (input.contains("W") && !input.contains("S")) ? 1.0F : (!input.contains("W") && input.contains("S")) ? -1.0F : 0.0F;
						if(input.contains("N")) { npc.xxa *= 0.3F; npc.zza *= 0.3F; }
					}
					if(input.contains("P") && npc.zza > 0 && !npc.isShiftKeyDown()) {
						npc.setSprinting(true);
					}
					if(npc.isUsingItem()) {
						int remaining = npc.useItemRemaining;
						if(remaining > 1) {
							npc.useItemRemaining = remaining - 1;
						} else {
							npc.stopUsingItem();
						}
					}
					// 26.2 folded the water/lava push into Entity.updateFluidInteraction (protected)
					try {
						updateFluidInteraction.invoke(npc);
					} catch (Exception e) {
						e.printStackTrace();
					}
					npc.aiStep();
				}
			}
		}.runTaskTimer(mcTASLang.getInstance(), 0, 1);
	}
}
