package instructions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import plugin.TASUtils;
import commands.Spectate;

import java.util.*;
import java.util.function.Predicate;
import plugin.FakePlayerManager;
import plugin.SpectatorSync;

@SuppressWarnings("unused")
public class Actions {
	protected final Player player;

	public Actions(Player player) {
		this.player = player;
	}

	private static final Map<UUID, String> activeInputs = new HashMap<>();
	/** Fakes whose own inventory (E) is open from a swapItems this tick; closed by {@link #closeInventory}. */
	private static final java.util.Set<UUID> inventoryOpen = new java.util.HashSet<>();
	/** Bumped by {@link #cancelAllMovement()}; older move() cleanups must not zero the new run's inputs. */
	private static int movementGeneration = 0;
	/** Mob claimed by an in-flight Terminator arrow → server tick it should land. */
	private static final Map<UUID, Integer> terminatorClaimedUntil = new HashMap<>();

	/** Mage beam range, as M7's beam in the arena. Helpers skip if nothing's inside. */
	private static final double MAGE_BEAM_RANGE = 50.0;
	/** Auto-aim search radius: past this the arrow hits terrain or flies long enough that the target moves. */
	private static final double TERMINATOR_MAX_RANGE = 100.0;

	public static String getActiveInput(UUID id) {
		return activeInputs.getOrDefault(id, "");
	}

	// Spigot-mojang compile name is detectEquipmentUpdatesPublic; Paper runtime has only detectEquipmentUpdates.
	// Resolve whichever exists at class-load time.
	private static final java.lang.reflect.Method DETECT_EQUIPMENT_UPDATES = resolveDetectEquipmentUpdates();

	private static java.lang.reflect.Method resolveDetectEquipmentUpdates() {
		for(String name : new String[]{"detectEquipmentUpdates", "detectEquipmentUpdatesPublic"}) {
			try {
				java.lang.reflect.Method m = net.minecraft.world.entity.LivingEntity.class.getMethod(name);
				m.setAccessible(true);
				return m;
			} catch (NoSuchMethodException ignored) {
			}
		}
		throw new IllegalStateException("LivingEntity.detectEquipmentUpdates[Public] not found on runtime");
	}

	/** Holds {@code input} (see {@link #move(String, int)} for the keys) until the next move; {@code ""} releases. */
	@SuppressWarnings("deprecation")
	public void move(String input) {
		move(input, 0);
	}

	/**
	 * Simulates a Player pressing movement input keys.  Only call this when the player changes which keys are pressed.
	 *
	 * @param input         The input keys being held down<br>Valid Input contains the following characters: W, A, S, D, J, P, N<br>WASD: movement<br>J: Jump<br>P: sPrint<br>N: sNeak
	 * @param durationTicks How long the keys are held down for, or 0 for manual override
	 * @deprecated use {@link #move(String)}: keys stay held until the next move ({@code ""} releases) or the server
	 *             stops them
	 */
	@Deprecated
	public void move(String input, int durationTicks) {
		// only handle CraftLivingEntity/NMS and positive duration
		TASUtils.debug(TASUtils.DebugType.CLIENT, player.getName() + (input.isEmpty() ? " stopped moving" : " moving " + input));
		if(!(player instanceof CraftLivingEntity craftEntity)) return;
		activeInputs.put(player.getUniqueId(), input);

		net.minecraft.world.entity.LivingEntity serverPlayer = craftEntity.getHandle();
		if(input.contains("A") && input.contains("D")) {
			serverPlayer.xxa = 0.0F;
		} else {
			serverPlayer.xxa = input.contains("A") ? 1.0F : (input.contains("D") ? -1.0F : 0.0F);
		}

		if(input.contains("W") && input.contains("S")) {
			serverPlayer.zza = 0.0F;
		} else {
			serverPlayer.zza = input.contains("W") ? 1.0F : (input.contains("S") ? -1.0F : 0.0F);
		}

		if(input.contains("W") && !input.contains("S") && !input.contains("N")) {
			serverPlayer.setSprinting(input.contains("P"));
		} else {
			serverPlayer.setSprinting(false);
		}

		serverPlayer.setShiftKeyDown(input.contains("N"));
		if(input.contains("N")) {
			serverPlayer.xxa *= 0.3F;
			serverPlayer.zza *= 0.3F;
		}

		// A held J jumps from FakePlayerManager.tickFakes, the physics step right after this input.
		if(!input.contains("J")) serverPlayer.setJumping(false);
		if(durationTicks > 0) {
			int generation = movementGeneration;
			TASUtils.scheduleTask(() -> {
				if(generation != movementGeneration) return; // stale cleanup from a cancelled run
				if(player.isValid()) {
					serverPlayer.xxa = 0.0F;
					serverPlayer.zza = 0.0F;
					serverPlayer.setSprinting(false);
					serverPlayer.setShiftKeyDown(false);
					serverPlayer.setJumping(false);
				}
				activeInputs.remove(player.getUniqueId());
			}, durationTicks);
		}
	}

	/**
	 * Hard-stops fake-player movement: keys (jump included), momentum. Older move() cleanups become no-ops.
	 * TAS start.
	 */
	public static void cancelAllMovement() {
		movementGeneration++;

		activeInputs.clear();

		for(Player p : FakePlayerManager.getFakePlayers().values()) {
			if(!(p instanceof CraftPlayer craftPlayer)) continue;
			ServerPlayer serverPlayer = craftPlayer.getHandle();
			serverPlayer.xxa = 0.0F;
			serverPlayer.zza = 0.0F;
			serverPlayer.setSprinting(false);
			serverPlayer.setShiftKeyDown(false);
			serverPlayer.setJumping(false);
			serverPlayer.setDeltaMovement(Vec3.ZERO);
			serverPlayer.hurtMarked = true;
		}
	}

	/**
	 * Change which hotbar slot (0-8) the player is “holding”,
	 * and broadcast that slot change.
	 *
	 * @param hotbarIndex The index of the new hotbar slot (0-8).
	 */
	public void setHotbarSlot(int hotbarIndex) {
		if(!(player instanceof CraftPlayer cp)) {
			return;
		}
		send(new ServerboundSetCarriedItemPacket(hotbarIndex));

		// A fake's entity tick never runs, so apply item attribute modifiers (Dungeonbreaker's BLOCK_BREAK_SPEED) here.
		try {
			DETECT_EQUIPMENT_UPDATES.invoke(cp.getHandle());
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

		SpectatorSync.syncHand(player);
	}

	/**
	 * Swaps two inventory slots in one tick, as a client would: open the inventory (E; closes any chest GUI first,
	 * opening sends nothing), a number key (SWAP) when one side is the hotbar or off-hand, else pick up / put down /
	 * put back, then close it.
	 *
	 * @param slotA any slot index (0-8 hotbar, 9-35 main inv, 36-39 armor, 40 offhand)
	 * @param slotB same range
	 */
	public void swapItems(int slotA, int slotB) {
		if(!(player instanceof CraftPlayer cp)) return;
		if(!inventoryOpen.contains(player.getUniqueId())) {
			if(cp.getHandle().containerMenu.containerId != 0) closeGui();
			inventoryOpen.add(player.getUniqueId());
		}
		if(slotB < 9 || slotB == 40) {
			int t = slotA;
			slotA = slotB;
			slotB = t;
		}
		if(slotA < 9 || slotA == 40) {
			clickSlot(0, menuSlot(slotB), slotA, ContainerInput.SWAP);
		} else {
			clickSlot(0, menuSlot(slotA), 0, ContainerInput.PICKUP);
			clickSlot(0, menuSlot(slotB), 0, ContainerInput.PICKUP);
			clickSlot(0, menuSlot(slotA), 0, ContainerInput.PICKUP);
		}
	}

	/**
	 * Sends the close for an inventory {@link #swapItems} left open. Every swap on one tick shares one open: the close
	 * goes out before this fake's next packet that isn't an inventory click ({@link #send}), as a client must close E
	 * to act, or at the end of the tick's inputs ({@link #closeOpenInventories}).
	 */
	private void closeInventory() {
		if(inventoryOpen.remove(player.getUniqueId())) send(new ServerboundContainerClosePacket(0));
	}

	/** Tick end, after the inputs and before the fakes move. */
	public static void closeOpenInventories() {
		for(Player fake : FakePlayerManager.getFakePlayers().values()) new Actions(fake).closeInventory();
		inventoryOpen.clear();
	}

	/** Player-inventory index (0-8 hotbar, 9-35 main, 36-39 boots..helmet, 40 off-hand) to InventoryMenu slot. */
	private static int menuSlot(int invIndex) {
		if(invIndex < 9) return 36 + invIndex;
		if(invIndex < 36) return invIndex;
		if(invIndex < 40) return 8 - (invIndex - 36);
		return 45;
	}

	/**
	 * A click in the open container screen: {@code containerId} 0 is the player's own inventory, otherwise the open
	 * chest GUI (terminals, Spirit Leap, /stats). {@code button} is the mouse button (0 left, 1 right), or the
	 * hotbar number for SWAP (40 = off-hand).
	 */
	public void clickSlot(int containerId, int slot, int button, ContainerInput input) {
		if(!(player instanceof CraftPlayer cp)) return;
		var menu = cp.getHandle().containerMenu;
		if(containerId != 0 && menu.containerId != containerId) return;
		var target = containerId == 0 ? cp.getHandle().inventoryMenu : menu;
		send(new ServerboundContainerClickPacket(target.containerId, target.getStateId(), (short) slot, (byte) button,
				input, new Int2ObjectOpenHashMap<>(), HashedStack.EMPTY));
	}

	/** Left-clicks {@code slot} in whatever chest GUI is open; no-op if none is. */
	public void clickGui(int slot) {
		clickGui(slot, 0, ContainerInput.PICKUP);
	}

	public void clickGui(int slot, int button, ContainerInput input) {
		if(!(player instanceof CraftPlayer cp)) return;
		int id = cp.getHandle().containerMenu.containerId;
		if(id == 0) return;
		clickSlot(id, slot, button, input);
	}

	/** Closes the open chest GUI (Escape). */
	public void closeGui() {
		if(!(player instanceof CraftPlayer cp)) return;
		send(new ServerboundContainerClosePacket(cp.getHandle().containerMenu.containerId));
	}

	/**
	 * Spirit Leap: right-click the held Infinileap, then click {@code target}'s head in the menu M7 opens.
	 *
	 * @deprecated finds the head by reading the menu server-side. Script it as a client does: {@link #rightClick()},
	 * then {@link #clickGui(int)} on the target's slot.
	 */
	@Deprecated
	public void leap(Player target) {
		rightClick();
		org.bukkit.inventory.Inventory top = player.getOpenInventory().getTopInventory();
		for(int slot = 0; slot < top.getSize(); slot++) {
			org.bukkit.inventory.ItemStack item = top.getItem(slot);
			if(item == null || item.getType() != Material.PLAYER_HEAD || !item.hasItemMeta()) continue;
			if(target.getName().equals(TASUtils.plain(item.getItemMeta().displayName()))) {
				clickGui(slot);
				return;
			}
		}
	}

	/** Puts this player on its section start. Setup before the run, not a client action (/tas passes --no-teleport). */
	protected void startAt(double x, double y, double z, float yaw, float pitch) {
		player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch), PlayerTeleportEvent.TeleportCause.PLUGIN);
		((CraftPlayer) player).getHandle().updatePlayerPose();
		Spectate.snapSpectatorsToFake(player);
	}

	/** Another fake by name ({@code akc0303}, {@code Beethoven}, ...). */
	protected static Player fake(String name) {
		return FakePlayerManager.getFakePlayers().get(name);
	}

	/** Runs {@code command} (no leading slash) as if typed in chat. */
	public void runCommand(String command) {
		send(new ServerboundChatCommandPacket(command.startsWith("/") ? command.substring(1) : command));
	}

	/**
	 * Rotates a player’s body & head to the given yaw/pitch
	 * and broadcasts that new orientation to every real client.
	 *
	 * @param yaw    body + head yaw, in degrees (0 = south, 90 = west…)
	 * @param pitch  look pitch, in degrees (-90 = straight up, +90 = straight down)
	 */
	public void turnHead(float yaw, float pitch) {
		ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
		TASUtils.debug(TASUtils.DebugType.CLIENT, player.getName() + " turning head to " + yaw + " " + pitch);
		if(FakePlayerManager.getFakePlayers().containsValue(player)) FakePlayerManager.syncReportedPosition(serverPlayer);
		send(new ServerboundMovePlayerPacket.Rot(yaw, pitch, serverPlayer.onGround(), serverPlayer.horizontalCollision));

		for (Player spectator : Spectate.getSpectatingPlayers(player)) {
			if (spectator instanceof CraftPlayer craftSpectator) {
				ServerPlayer nmsSpectator = craftSpectator.getHandle();
				nmsSpectator.connection.send(new ClientboundPlayerRotationPacket(yaw, false, pitch, false));
				Spectate.updateLastSentRotation(spectator, yaw, pitch);
			}
		}
	}

	/** Aims at the nearest visible non-player mob, for hitscan abilities (mage beam). */
	public void snapHeadToNearestEnemy() {
		LivingEntity target = findNearestEnemy(player, MAGE_BEAM_RANGE, le -> hasLineOfSight(player, le));
		if(target == null) return;
		snapHeadAtEntity(target);
	}

	/** Aims at the target's box centre; no LOS or filtering (caller picked it). */
	public void snapHeadAtEntity(LivingEntity target) {
		Location eye = player.getEyeLocation();
		Vector center = target.getBoundingBox().getCenter();
		double dx = center.getX() - eye.getX();
		double dy = center.getY() - eye.getY();
		double dz = center.getZ() - eye.getZ();

		float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
		float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
		turnHead(yaw, pitch);
	}

	/** Nearest mob in a 32-block cube whose name contains {@code nameContains} (case-insensitive). No filtering. */
	public void snapHeadAtNearestNamed(String nameContains) {
		LivingEntity nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		for(Entity entity : player.getNearbyEntities(32, 32, 32)) {
			if(!(entity instanceof LivingEntity living)) continue;
			String livingName = TASUtils.plain(living.customName());
			if(livingName.isEmpty() || !livingName.toLowerCase().contains(nameContains.toLowerCase())) continue;
			double distance = player.getLocation().distance(living.getLocation());
			if(distance < nearestDistance) {
				nearestDistance = distance;
				nearest = living;
			}
		}
		if(nearest != null) snapHeadAtEntity(nearest);
	}

	/**
	 * Aims a Terminator arrow (spawn {@code eye + (0, -0.1, 0)}, speed 3.175) at the nearest reachable non-player
	 * mob. Solves the angle iteratively for gravity 0.05/t and drag 0.99/t; skips terrain-blocked targets.
	 */
	public void aimTerminatorAtNearestEnemy() {
		int now = MinecraftServer.currentTick;
		terminatorClaimedUntil.entrySet().removeIf(e -> e.getValue() <= now);

		LivingEntity target = findNearestEnemy(player, TERMINATOR_MAX_RANGE, le ->
				!terminatorClaimedUntil.containsKey(le.getUniqueId()) && canTerminatorReach(player, le));
		if(target == null) return;
		aimTerminatorAtEntity(target);
	}

	/**
	 * Aims a Terminator arrow at a chosen target; no filter or terrain check, silent if out of range.
	 * Claims the mob for the flight time so the auto-pick variant won't re-target it.
	 */
	public void aimTerminatorAtEntity(LivingEntity target) {
		Location spawn = player.getEyeLocation().add(0, -0.1, 0);
		Vector center = target.getBoundingBox().getCenter();
		double dx = center.getX() - spawn.getX();
		double dy = center.getY() - spawn.getY();
		double dz = center.getZ() - spawn.getZ();
		double dh = Math.sqrt(dx * dx + dz * dz);

		final double speed = 3.175;
		Double angle = solveLaunchAngle(dh, dy);
		if(angle == null) return; // out of ballistic range

		int now = MinecraftServer.currentTick;
		int flightTicks = simulateArrowFlightTicks(angle, dh);
		terminatorClaimedUntil.put(target.getUniqueId(), now + flightTicks + 3);

		float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
		float pitch = (float) -Math.toDegrees(angle); // angle>0 (upward) → MC pitch<0
		turnHead(yaw, pitch);
	}

	private static LivingEntity findNearestEnemy(Player p, double range, Predicate<LivingEntity> canHit) {
		Vector origin = p.getEyeLocation().toVector();
		double rangeSq = range * range;
		List<LivingEntity> candidates = new ArrayList<>();
		// Half-extents: a 2·range cube, the smallest AABB around the sphere.
		for(Entity e : p.getNearbyEntities(range, range, range)) {
			if(!(e instanceof LivingEntity le)) continue;
			if(le instanceof Player) continue;
			if(le.isDead()) continue;
			if(le.getBoundingBox().getCenter().distanceSquared(origin) > rangeSq) continue;
			candidates.add(le);
		}
		if(candidates.isEmpty()) return null;
		candidates.sort(Comparator.comparingDouble(le -> le.getBoundingBox().getCenter().distanceSquared(origin)));
		for(LivingEntity le : candidates) {
			if(canHit.test(le)) return le;
		}
		return null;
	}

	/**
	 * {@link #leftClick} that only swings if a beam from the current facing would hit something within
	 * {@link #MAGE_BEAM_RANGE}; avoids thousands of no-op packets during beam spam.
	 * <br>
	 * Mirrors M7's {@code ItemUtils.mageBeam} filter: no players, dead or resistance-255 mobs. Skips if a block is
	 * closer than the entity.
	 */
	public void loopLeftClick() {
		if(!mageBeamWouldHit(player)) return;
		leftClick();
	}

	private static boolean mageBeamWouldHit(Player p) {
		Location eye = p.getEyeLocation();
		Vector dir = eye.getDirection();
		// raySize 0.5 = ItemUtils.MAGE_BEAM_LENIENCY, so the gate is never stricter than the real beam.
		RayTraceResult entityHit = p.getWorld().rayTraceEntities(eye, dir, MAGE_BEAM_RANGE, 0.5, entity -> {
			if(!(entity instanceof LivingEntity le)) return false;
			if(le instanceof Player) return false;
			if(le.isDead()) return false;
			return !(le.hasPotionEffect(PotionEffectType.RESISTANCE)
					&& le.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() == 255);
		});
		if(entityHit == null) return false;
		// Beam stops at a closer block.
		RayTraceResult blockHit = p.getWorld().rayTraceBlocks(eye, dir, MAGE_BEAM_RANGE, FluidCollisionMode.NEVER, true);
		if(blockHit == null) return true;
		Vector eyeVec = eye.toVector();
		return entityHit.getHitPosition().distanceSquared(eyeVec) <= blockHit.getHitPosition().distanceSquared(eyeVec);
	}

	/**
	 * {@link #rightClick} that only fires if the middle Terminator arrow would hit a mob before terrain or
	 * {@link #TERMINATOR_MAX_RANGE}; saves per-tick arrow spawns with nothing in line.
	 * <br>
	 * Same filter as {@link #mageBeamWouldHit}. Walks arrow physics per tick ({@code v.xz *= 0.99},
	 * {@code v.y = v.y * 0.99 - 0.05}). Middle arrow only: the side ones are ±5° and rarely matter if it misses.
	 */
	public void loopRightClick() {
		if(!terminatorWouldHit(player)) return;
		rightClick();
	}

	private static boolean terminatorWouldHit(Player p) {
		Location spawn = p.getEyeLocation().add(0, -0.1, 0);
		Vector dir = p.getEyeLocation().getDirection();

		final double speed = 3.175;
		double vx = dir.getX() * speed;
		double vy = dir.getY() * speed;
		double vz = dir.getZ() * speed;

		Vector pos = spawn.toVector();
		Vector start = pos.clone();
		World world = p.getWorld();
		double maxRangeSq = TERMINATOR_MAX_RANGE * TERMINATOR_MAX_RANGE;

		for(int t = 0; t < 200; t++) {
			Vector vel = new Vector(vx, vy, vz);
			double stepLen = vel.length();
			if(stepLen < 1e-4) return false;
			Vector dirNorm = vel.clone().multiply(1.0 / stepLen);

			RayTraceResult entityHit = world.rayTraceEntities(pos.toLocation(world), dirNorm, stepLen, 0, entity -> {
				if(!(entity instanceof LivingEntity le)) return false;
				if(le instanceof Player) return false;
				if(le.isDead()) return false;
				return !(le.hasPotionEffect(PotionEffectType.RESISTANCE)
						&& le.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() == 255);
			});
			RayTraceResult blockHit = world.rayTraceBlocks(pos.toLocation(world), dirNorm, stepLen, FluidCollisionMode.NEVER, true);

			if(entityHit != null) {
				if(blockHit == null) return true;
				double entityD2 = entityHit.getHitPosition().distanceSquared(pos);
				double blockD2 = blockHit.getHitPosition().distanceSquared(pos);
				return entityD2 <= blockD2;
			}
			if(blockHit != null) return false;

			pos.add(vel);
			vx *= 0.99;
			vz *= 0.99;
			vy = vy * 0.99 - 0.05;

			double dx = pos.getX() - start.getX();
			double dz = pos.getZ() - start.getZ();
			if(dx * dx + dz * dz > maxRangeSq) return false;
		}
		return false;
	}

	private static boolean hasLineOfSight(Player p, LivingEntity target) {
		Location eye = p.getEyeLocation();
		Vector dir = target.getBoundingBox().getCenter().subtract(eye.toVector());
		double dist = dir.length();
		if(dist < 1e-6) return true;
		RayTraceResult hit = p.getWorld().rayTraceBlocks(eye, dir.multiply(1.0 / dist), dist, FluidCollisionMode.NEVER, true);
		return hit == null;
	}

	private static boolean canTerminatorReach(Player p, LivingEntity target) {
		Location spawn = p.getEyeLocation().add(0, -0.1, 0);
		Vector center = target.getBoundingBox().getCenter();
		double dx = center.getX() - spawn.getX();
		double dy = center.getY() - spawn.getY();
		double dz = center.getZ() - spawn.getZ();
		double dh = Math.sqrt(dx * dx + dz * dz);
		if(dh < 1e-6) return hasLineOfSight(p, target);

		final double speed = 3.175;
		Double angle = solveLaunchAngle(dh, dy);
		if(angle == null) return false;

		// Per-tick walk; a block before the target's box aborts.
		double yawRad = Math.atan2(-dx, dz);
		double vh = speed * Math.cos(angle);
		double vy = speed * Math.sin(angle);
		double vx = -Math.sin(yawRad) * vh;
		double vz = Math.cos(yawRad) * vh;

		Vector pos = spawn.toVector();
		BoundingBox box = target.getBoundingBox();
		World world = p.getWorld();

		for(int t = 0; t < 200; t++) {
			Vector vel = new Vector(vx, vy, vz);
			double stepLen = vel.length();
			if(stepLen < 1e-6) return false;
			Vector dirNorm = vel.clone().multiply(1.0 / stepLen);

			RayTraceResult boxHit = box.rayTrace(pos, dirNorm, stepLen);
			RayTraceResult blockHit = world.rayTraceBlocks(pos.toLocation(world), dirNorm, stepLen, FluidCollisionMode.NEVER, true);

			if(boxHit != null) {
				if(blockHit == null) return true;
				double boxD2 = boxHit.getHitPosition().distanceSquared(pos);
				double blockD2 = blockHit.getHitPosition().distanceSquared(pos);
				return boxD2 <= blockD2;
			}
			if(blockHit != null) return false;

			pos.add(vel);
			vx *= 0.99;
			vz *= 0.99;
			vy = vy * 0.99 - 0.05;

			// Safety: arrow has clearly fallen past the target
			if(pos.getY() < box.getMinY() - 20) return false;
		}
		return false;
	}

	/** Launch angle (radians) hitting offset (dh, dy), or null if none. */
	private static Double solveLaunchAngle(double dh, double dy) {
		double aimY = dy;
		double angle = Math.atan2(aimY, dh);
		for(int i = 0; i < 12; i++) {
			Double yHit = simulateArrowYAtDistance(angle, dh);
			if(yHit == null) return null;
			double err = dy - yHit;
			if(Math.abs(err) < 0.01) return angle;
			aimY += err;
			angle = Math.atan2(aimY, dh);
		}
		return angle;
	}

	/** Ticks to travel {@code targetH} horizontally at {@code angleRad}; 60 as fallback if it can't reach. */
	private static int simulateArrowFlightTicks(double angleRad, double targetH) {
		double vh = 3.175 * Math.cos(angleRad);
		double vy = 3.175 * Math.sin(angleRad);
		double h = 0;
		for(int t = 1; t <= 400; t++) {
			h += vh;
			vh *= 0.99;
			vy = vy * 0.99 - 0.05;
			if(h >= targetH) return t;
			if(vh < 1e-4) return 60;
		}
		return 60;
	}

	/** Y offset from spawn when the arrow crosses {@code targetH}, or null if it never does. */
	private static Double simulateArrowYAtDistance(double angleRad, double targetH) {
		double vh = 3.175 * Math.cos(angleRad);
		double vy = 3.175 * Math.sin(angleRad);
		double h = 0, y = 0;
		double prevH, prevY;
		for(int t = 0; t < 400; t++) {
			prevH = h;
			prevY = y;
			h += vh;
			y += vy;
			vh *= 0.99;
			vy = vy * 0.99 - 0.05;
			if(h >= targetH) {
				double frac = (targetH - prevH) / (h - prevH);
				return prevY + frac * (y - prevY);
			}
			if(vh < 1e-4) return null;
		}
		return null;
	}

	/**
	 * What the client's crosshair can land on, as Paper's swing ray trace decides it: anything this player can see
	 * that isn't in spectator mode. Teammates count (M7 never damages players); watchers are hidden from fakes.
	 */
	private boolean targetable(org.bukkit.entity.Entity entity) {
		if(!player.canSee(entity)) return false;
		return !(entity instanceof Player target && target.getGameMode() == GameMode.SPECTATOR);
	}

	/**
	 * Simulates a fake player performing a left click.
	 *
	 */
	public void leftClick() {
		if(!(player instanceof CraftPlayer cp)) return;

		ServerPlayer serverPlayer = cp.getHandle();

		ServerboundSwingPacket swingPacket = new ServerboundSwingPacket(InteractionHand.MAIN_HAND);
		send(swingPacket);

		// Vanilla's crosshair pick raytraces blocks first and caps the entity ray at the block
		// hit, so entities occluded by a block can't be targeted.
		RayTraceResult blockRay = player.rayTraceBlocks(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue());
		double entityRange = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getValue();
		if(blockRay != null) {
			entityRange = Math.min(entityRange, player.getEyeLocation().toVector().distance(blockRay.getHitPosition()));
		}

		RayTraceResult entityRay = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), entityRange, entity -> {
			if(entity == player) return false;
			if(entity.isDead()) return false;
			// Living only: handleAttack disconnects the fake with "Attempting to attack an invalid entity" on
			// items, XP orbs and arrows, which a client's crosshair never picks.
			if(!(entity instanceof LivingEntity le)) return false;
			return targetable(le);
		});

		if(entityRay != null && entityRay.getHitEntity() != null) {
			net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entityRay.getHitEntity()).getHandle();
			// 26.2: attacks are their own packet.
			ServerboundAttackPacket attackPacket = new ServerboundAttackPacket(nmsEntity.getId());
			send(attackPacket);
			return;
		}

		// The swing alone is the click otherwise (Paper fires LEFT_CLICK_AIR/BLOCK from it). Like the client, dig only
		// where adventure mode lets this item break the block (can_break).
		if(blockRay != null && blockRay.getHitBlock() != null) {
			Block block = blockRay.getHitBlock();
			BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
			if(serverPlayer.blockActionRestricted(serverPlayer.level(), pos, serverPlayer.gameMode.getGameModeForPlayer())) return;
			Direction direction = CraftBlock.blockFaceToNotch(blockRay.getHitBlockFace());

			ServerboundPlayerActionPacket breakPacket = new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, direction, 0);
			send(breakPacket);
			ServerboundPlayerActionPacket breakStopPacket = new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, direction, 0);
			send(breakStopPacket);
		}
	}

	/**
	 * Simulates a fake player performing a right click.
	 *
	 */
	@SuppressWarnings("deprecation") // Material.isInteractable() is deprecated but has no exact replacement
	public void rightClick() {
		if(!(player instanceof CraftPlayer cp)) return;

		ServerPlayer serverPlayer = cp.getHandle();

		// Vanilla's crosshair pick raytraces blocks first and caps the entity ray at the block
		// hit, so entities occluded by a block can't be targeted.
		RayTraceResult blockRay = player.rayTraceBlocks(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue());
		double entityRange = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getValue();
		if(blockRay != null) {
			entityRange = Math.min(entityRange, player.getEyeLocation().toVector().distance(blockRay.getHitPosition()));
		}

		RayTraceResult entityRay = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), entityRange, entity -> entity != player && targetable(entity));

		if(entityRay != null && entityRay.getHitEntity() != null) {
			net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entityRay.getHitEntity()).getHandle();

			Vec3 entityPos = nmsEntity.position();
			Vec3 hitPos = new Vec3(entityRay.getHitPosition().getX(), entityRay.getHitPosition().getY(), entityRay.getHitPosition().getZ());
			Vec3 localHit = hitPos.subtract(entityPos);

			// 26.2: ONE packet, location (relative to entity) required; it fires both the interact-at and interact events.
			ServerboundInteractPacket interactPacket = new ServerboundInteractPacket(nmsEntity.getId(), InteractionHand.MAIN_HAND, localHit, serverPlayer.isShiftKeyDown());
			send(interactPacket);
			for(Player spectator : Spectate.getSpectatingPlayers(player)) spectator.swingMainHand();

			// Vanilla sends no UseItem after a consumed entity interaction: item frames, Interaction entities and
			// villagers (Villager.mobInteract) consume it.
			Entity hit = entityRay.getHitEntity();
			if(hit instanceof ItemFrame || hit instanceof Interaction
					|| (hit instanceof Villager v && !v.isSleeping() && player.getInventory().getItemInMainHand().getType() != Material.VILLAGER_SPAWN_EGG)) {
				return;
			}
		}
		// Check for block target
		else {
			if(blockRay != null && blockRay.getHitBlock() != null) {
				Block block = blockRay.getHitBlock();
				BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
				Direction direction = CraftBlock.blockFaceToNotch(blockRay.getHitBlockFace());

				Vec3 hitVec = new Vec3(blockRay.getHitPosition().getX(), blockRay.getHitPosition().getY(), blockRay.getHitPosition().getZ());

				BlockHitResult blockHit = new BlockHitResult(hitVec, direction, pos, false);

				ServerboundUseItemOnPacket useOnBlockPacket = new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, blockHit, 0);
				send(useOnBlockPacket);
				// The client follows with UseItem unless the block took the click. Sneaking with an item in either hand
				// skips the block (MultiPlayerGameMode.performUseItemOn), so it never takes it.
				boolean skipsBlock = serverPlayer.isShiftKeyDown()
						&& (!serverPlayer.getMainHandItem().isEmpty() || !serverPlayer.getOffhandItem().isEmpty());
				if(block.getType().isInteractable() && !skipsBlock) {
					for(Player spectator : Spectate.getSpectatingPlayers(player)) spectator.swingMainHand();
					return;
				}
			}
		}

		// Right click air (eat food, throw pearl, use item ability)
		ServerboundUseItemPacket useItemPacket = new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, serverPlayer.getYRot(), serverPlayer.getXRot());
		send(useItemPacket);
	}

	/** {@link #turnHead} then {@link #rightClick()}, so the click aims and UseItem carries {@code yaw}/{@code pitch}. */
	public void rightClick(float yaw, float pitch) {
		turnHead(yaw, pitch);
		rightClick();
	}

	public void stopRightClick() {
		ServerPlayer sp = ((CraftPlayer) player).getHandle();
		ServerboundPlayerActionPacket packet = new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, sp.blockPosition(), Direction.DOWN, 0);
		send(packet);
	}

	public void dropItem(boolean dropAll) {
		ServerboundPlayerActionPacket.Action action = dropAll
				? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS
				: ServerboundPlayerActionPacket.Action.DROP_ITEM;
		ServerPlayer sp = ((CraftPlayer) player).getHandle();
		ServerboundPlayerActionPacket packet = new ServerboundPlayerActionPacket(action, sp.blockPosition(), Direction.DOWN, 0);
		send(packet);
	}

	/**
	 * Sends {@code packet} as this player's client. A fake's goes through its own pipeline, so the server (and M7's
	 * interceptor) sees exactly what a real client's would.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void send(Packet<?> packet) {
		if(!(player instanceof CraftPlayer craftPlayer) || !player.isOnline()) return;
		if(!(packet instanceof ServerboundContainerClickPacket click && click.containerId() == 0)) closeInventory();
		// Paper's decoder stamps these and its spam limiter reads the stamp; left at 0, every use after the 8th is dropped.
		if(packet instanceof ServerboundUseItemPacket use) use.timestamp = System.currentTimeMillis();
		if(packet instanceof ServerboundUseItemOnPacket useOn) useOn.timestamp = System.currentTimeMillis();

		TASUtils.debug(TASUtils.DebugType.CLIENT, player.getName() + " Sending Packet " + packet.getClass().getSimpleName() + (TASUtils.isSuperVerbose() ? (" at " + TASUtils.round(player.getLocation().getX(), 3) + " " + TASUtils.round(player.getLocation().getY(), 5) + " " + TASUtils.round(player.getLocation().getZ(), 3) + " " + player.getLocation().getYaw() + " " + player.getLocation().getPitch()) : ""));
		if(FakePlayerManager.receive(player, packet)) return;
		((Packet) packet).handle(craftPlayer.getHandle().connection);
	}

	/** {@code at(20).rightClick()} = {@code rightClick()} 20 ticks from now. Chained calls run on that tick, in order. */
	public Later at(int delay) {
		return new Later(delay);
	}

	@SuppressWarnings("UnusedReturnValue")
	public final class Later {
		private final int delay;

		private Later(int delay) {
			this.delay = delay;
		}

		public Later move(String input) {
			TASUtils.scheduleTask(() -> Actions.this.move(input), delay);
			return this;
		}

		/** @deprecated use {@link #move(String)} */
		@Deprecated
		@SuppressWarnings("deprecation")
		public Later move(String input, int durationTicks) {
			TASUtils.scheduleTask(() -> Actions.this.move(input, durationTicks), delay);
			return this;
		}

		public Later setHotbarSlot(int hotbarIndex) {
			TASUtils.scheduleTask(() -> Actions.this.setHotbarSlot(hotbarIndex), delay);
			return this;
		}

		public Later swapItems(int slotA, int slotB) {
			TASUtils.scheduleTask(() -> Actions.this.swapItems(slotA, slotB), delay);
			return this;
		}

		public Later clickSlot(int containerId, int slot, int button, ContainerInput input) {
			TASUtils.scheduleTask(() -> Actions.this.clickSlot(containerId, slot, button, input), delay);
			return this;
		}

		public Later clickGui(int slot) {
			TASUtils.scheduleTask(() -> Actions.this.clickGui(slot), delay);
			return this;
		}

		public Later clickGui(int slot, int button, ContainerInput input) {
			TASUtils.scheduleTask(() -> Actions.this.clickGui(slot, button, input), delay);
			return this;
		}

		public Later closeGui() {
			TASUtils.scheduleTask(Actions.this::closeGui, delay);
			return this;
		}

		/** @deprecated see {@link Actions#leap(Player)}. */
		@Deprecated
		public Later leap(Player target) {
			TASUtils.scheduleTask(() -> Actions.this.leap(target), delay);
			return this;
		}

		public Later runCommand(String command) {
			TASUtils.scheduleTask(() -> Actions.this.runCommand(command), delay);
			return this;
		}

		public Later turnHead(float yaw, float pitch) {
			TASUtils.scheduleTask(() -> Actions.this.turnHead(yaw, pitch), delay);
			return this;
		}

		public Later snapHeadToNearestEnemy() {
			TASUtils.scheduleTask(Actions.this::snapHeadToNearestEnemy, delay);
			return this;
		}

		public Later snapHeadAtEntity(LivingEntity target) {
			TASUtils.scheduleTask(() -> Actions.this.snapHeadAtEntity(target), delay);
			return this;
		}

		public Later snapHeadAtNearestNamed(String nameContains) {
			TASUtils.scheduleTask(() -> Actions.this.snapHeadAtNearestNamed(nameContains), delay);
			return this;
		}

		public Later aimTerminatorAtNearestEnemy() {
			TASUtils.scheduleTask(Actions.this::aimTerminatorAtNearestEnemy, delay);
			return this;
		}

		public Later aimTerminatorAtEntity(LivingEntity target) {
			TASUtils.scheduleTask(() -> Actions.this.aimTerminatorAtEntity(target), delay);
			return this;
		}

		public Later loopLeftClick() {
			TASUtils.scheduleTask(Actions.this::loopLeftClick, delay);
			return this;
		}

		public Later loopRightClick() {
			TASUtils.scheduleTask(Actions.this::loopRightClick, delay);
			return this;
		}

		public Later leftClick() {
			TASUtils.scheduleTask(Actions.this::leftClick, delay);
			return this;
		}

		public Later rightClick() {
			TASUtils.scheduleTask(Actions.this::rightClick, delay);
			return this;
		}

		public Later rightClick(float yaw, float pitch) {
			TASUtils.scheduleTask(() -> Actions.this.rightClick(yaw, pitch), delay);
			return this;
		}

		public Later stopRightClick() {
			TASUtils.scheduleTask(Actions.this::stopRightClick, delay);
			return this;
		}

		public Later dropItem(boolean dropAll) {
			TASUtils.scheduleTask(() -> Actions.this.dropItem(dropAll), delay);
			return this;
		}

		public Later send(Packet<?> packet) {
			TASUtils.scheduleTask(() -> Actions.this.send(packet), delay);
			return this;
		}

		public Object rogj(float v, float v1) {
			return null;
		}
	}
}
