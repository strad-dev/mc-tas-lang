package tas.nms;

import com.mojang.logging.LogUtils;
import tas.commands.Spectate;
import listeners.CustomItems;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.PiercingWeapon;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.slf4j.Logger;

public class TASGamePacketListenerImpl extends ServerGamePacketListenerImpl {
	static final Logger LOGGER = LogUtils.getLogger();
	public TASGamePacketListenerImpl(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer entityplayer, CommonListenerCookie commonlistenercookie) {
		super(minecraftserver, networkmanager, entityplayer, commonlistenercookie);
	}

	@Override
	public void handleAnimate(ServerboundSwingPacket packet) {
		super.handleAnimate(packet);
		Player cp = getCraftPlayer();
		CustomItems.handleCustomItems(null, EquipmentSlot.HAND, cp.getInventory().getItemInMainHand(), Action.LEFT_CLICK_AIR, cp);
	}

	@Override
	public void handleAttack(ServerboundAttackPacket packet) {
		if(this.player.isSpectator()) {
			return;
		}

		final ServerLevel worldserver = this.player.level();
		final Entity entity = worldserver.getEntityOrPart(packet.entityId());
		if(entity == this.player) {
			this.disconnect(Component.literal("Cannot interact with self!"), PlayerKickEvent.Cause.SELF_INTERACTION);
			return;
		}

		this.player.resetLastActionTime();
		if(entity == null || !worldserver.getWorldBorder().isWithinBounds(entity.blockPosition())) {
			return;
		}

		AABB axisalignedbb = entity.getBoundingBox();
		ItemStack itemstack = this.player.getMainHandItem();
		if(!this.player.isWithinAttackRange(itemstack, axisalignedbb, 3.0) || itemstack.has(DataComponents.PIERCING_WEAPON)) {
			return;
		}

		if(entity instanceof ServerPlayer targetNms) {
			// Check if target is a spectator of this fake player
			Player targetBukkit = targetNms.getBukkitEntity();
			Player attacker = this.getCraftPlayer();
			if(Spectate.getSpectatingPlayers(attacker).contains(targetBukkit)) {
				PlayerInteractEvent event = new PlayerInteractEvent(attacker, Action.LEFT_CLICK_AIR, attacker.getInventory().getItemInMainHand(), null, null, org.bukkit.inventory.EquipmentSlot.HAND);
				this.cserver.getPluginManager().callEvent(event);
				return;
			}
		}

		if(entity instanceof ItemEntity || entity instanceof ExperienceOrb || (entity instanceof AbstractArrow entityarrow && !entityarrow.isAttackable())) {
			this.disconnect(Component.translatable("multiplayer.disconnect.invalid_entity_attacked"), PlayerKickEvent.Cause.INVALID_ENTITY_ATTACKED);
			LOGGER.warn("Player {} tried to attack an invalid entity", this.player.getPlainTextName());
			return;
		}

		if(!itemstack.isItemEnabled(worldserver.enabledFeatures())) {
			return;
		}

		if(this.player.cannotAttackWithItem(itemstack, 5)) {
			return;
		}

		// Suppress attack if a custom ability already fired on the swing this tick
		if(CustomItems.abilityFiredThisTick(this.getCraftPlayer())) return;

		// Check if the entity won't produce a damage event
		boolean immune;
		if(entity instanceof LivingEntity living) {
			immune = living.isDeadOrDying() || living.invulnerableTime > 0 || (entity instanceof ServerPlayer target && target.isCreative());
		} else {
			immune = false;
		}

		if(immune) {
			Player attacker = this.getCraftPlayer();
			PlayerInteractEvent event = new PlayerInteractEvent(attacker, Action.LEFT_CLICK_AIR, attacker.getInventory().getItemInMainHand(), null, null, org.bukkit.inventory.EquipmentSlot.HAND);
			this.cserver.getPluginManager().callEvent(event);
		}

		this.player.attack(entity);
	}

	public void handlePlayerAction(ServerboundPlayerActionPacket packetplayinblockdig) {
		BlockPos blockposition = packetplayinblockdig.getPos();
		this.player.resetLastActionTime();
		ServerboundPlayerActionPacket.Action packetplayinblockdig_enumplayerdigtype = packetplayinblockdig.getAction();
		switch(packetplayinblockdig_enumplayerdigtype) {
			case STAB:
				if(!this.player.isSpectator()) {
					ItemStack itemstack = this.player.getItemInHand(InteractionHand.MAIN_HAND);
					if(this.player.cannotAttackWithItem(itemstack, 5)) {
						return;
					}

					PiercingWeapon piercingweapon = itemstack.get(DataComponents.PIERCING_WEAPON);
					if(piercingweapon != null) {
						piercingweapon.attack(this.player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
					}

				}
				return;
			case SWAP_ITEM_WITH_OFFHAND:
				if(!this.player.isSpectator()) {
					ItemStack itemstack1 = this.player.getItemInHand(InteractionHand.OFF_HAND);
					CraftItemStack mainHand = CraftItemStack.asCraftMirror(itemstack1);
					CraftItemStack offHand = CraftItemStack.asCraftMirror(this.player.getItemInHand(InteractionHand.MAIN_HAND));
					PlayerSwapHandItemsEvent swapItemsEvent = new PlayerSwapHandItemsEvent(this.getCraftPlayer(), mainHand.clone(), offHand.clone());
					this.cserver.getPluginManager().callEvent(swapItemsEvent);
					if(swapItemsEvent.isCancelled()) {
						return;
					}

					if(swapItemsEvent.getOffHandItem().equals(offHand)) {
						this.player.setItemInHand(InteractionHand.OFF_HAND, this.player.getItemInHand(InteractionHand.MAIN_HAND));
					} else {
						this.player.setItemInHand(InteractionHand.OFF_HAND, CraftItemStack.asNMSCopy(swapItemsEvent.getOffHandItem()));
					}

					if(swapItemsEvent.getMainHandItem().equals(mainHand)) {
						this.player.setItemInHand(InteractionHand.MAIN_HAND, itemstack1);
					} else {
						this.player.setItemInHand(InteractionHand.MAIN_HAND, CraftItemStack.asNMSCopy(swapItemsEvent.getMainHandItem()));
					}

					this.player.stopUsingItem();
				}

				return;
			case DROP_ITEM:
				if(!this.player.isSpectator()) {
					CustomItems.handleDrop(this.player.getBukkitEntity(), true);
				}

				return;
			case DROP_ALL_ITEMS:
				if(!this.player.isSpectator()) {
					CustomItems.handleDrop(this.player.getBukkitEntity(), false);
				}

				return;
			case RELEASE_USE_ITEM:
				this.player.releaseUsingItem();
				return;
			case START_DESTROY_BLOCK:
			case ABORT_DESTROY_BLOCK:
			case STOP_DESTROY_BLOCK:
				this.player.gameMode.handleBlockBreakAction(blockposition, packetplayinblockdig_enumplayerdigtype, packetplayinblockdig.getDirection(), this.player.level().getMaxY(), packetplayinblockdig.getSequence());
				return;
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	public void handleUseItemOn(ServerboundUseItemOnPacket packetplayinuseitem) {
		this.player.gameMode.firedInteract = false;
		ServerLevel worldserver = this.player.level();
		InteractionHand enumhand = packetplayinuseitem.getHand();
		ItemStack itemstack = this.player.getItemInHand(enumhand);
		if(itemstack.isItemEnabled(worldserver.enabledFeatures())) {
			BlockHitResult movingobjectpositionblock = packetplayinuseitem.getHitResult();
			Vec3 vec3d = movingobjectpositionblock.getLocation();
			if(!vec3d.isFinite()) {
				return;
			}

			BlockPos blockposition = movingobjectpositionblock.getBlockPos();
			if(this.player.isWithinBlockInteractionRange(blockposition, 1.0F)) {
				Vec3 vec3d1 = vec3d.subtract(Vec3.atCenterOf(blockposition));
				if(Math.abs(vec3d1.x()) < 1.0000001 && Math.abs(vec3d1.y()) < 1.0000001 && Math.abs(vec3d1.z()) < 1.0000001) {
					Direction enumdirection = movingobjectpositionblock.getDirection();
					this.player.resetLastActionTime();
					int i = this.player.level().getMaxY();
					int j = this.player.level().getMinY();
					if(blockposition.getY() > i) {
						this.player.sendBuildLimitMessage(true, i);
					} else if(blockposition.getY() < j) {
						this.player.sendBuildLimitMessage(false, j);
					} else {
						this.player.stopUsingItem();
						InteractionResult enuminteractionresult = this.player.gameMode.useItemOn(this.player, worldserver, itemstack, enumhand, movingobjectpositionblock);
						if(enuminteractionresult == InteractionResult.PASS) {
							ServerboundUseItemPacket packet = new ServerboundUseItemPacket(enumhand, 0, this.player.getYRot(), this.player.getXRot());
							this.handleUseItem(packet);
						} else {
							if(enuminteractionresult.consumesAction()) {
								CriteriaTriggers.ANY_BLOCK_USE.trigger(this.player, movingobjectpositionblock.getBlockPos(), itemstack.copy());
							}

							if(!enuminteractionresult.consumesAction() && wasBlockPlacementAttempt(this.player, itemstack)) {
								if(enumdirection == Direction.UP && blockposition.getY() >= i) {
									this.player.sendBuildLimitMessage(true, i);
								} else if(enumdirection == Direction.DOWN && blockposition.getY() <= j) {
									this.player.sendBuildLimitMessage(false, j);
								}
							} else if(enuminteractionresult instanceof InteractionResult.Success enuminteractionresult_d) {
								if(enuminteractionresult_d.swingSource() == InteractionResult.SwingSource.SERVER && !this.player.gameMode.interactResult) {
									this.player.swing(enumhand, true);
								}
							}
						}
					}

					this.send(new ClientboundBlockUpdatePacket(worldserver, blockposition));
					this.send(new ClientboundBlockUpdatePacket(worldserver, blockposition.relative(enumdirection)));
				} else {
					LOGGER.warn("Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.", this.player.getGameProfile().name(), vec3d, blockposition);
				}
			}
		}
	}

	public void handleUseItem(ServerboundUseItemPacket packetplayinblockplace) {
		this.player.gameMode.firedInteract = false;
		ServerLevel worldserver = this.player.level();
		InteractionHand enumhand = packetplayinblockplace.getHand();
		ItemStack itemstack = this.player.getItemInHand(enumhand);
		this.player.resetLastActionTime();
		if(!itemstack.isEmpty() && itemstack.isItemEnabled(worldserver.enabledFeatures())) {
			float f = Mth.wrapDegrees(packetplayinblockplace.getYRot());
			float f1 = Mth.wrapDegrees(packetplayinblockplace.getXRot());
			if(f1 != this.player.getXRot() || f != this.player.getYRot()) {
				this.player.absSnapRotationTo(f, f1);
			}

			Vec3 vec3d = this.player.getEyePosition();
			Vec3 vec3d1 = vec3d.add(this.player.getViewVector(1.0F).scale(this.player.blockInteractionRange()));
			BlockHitResult movingobjectposition = this.player.level().clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.player));
			boolean cancelled;
			if(movingobjectposition != null && movingobjectposition.getType() == HitResult.Type.BLOCK) {
				if(this.player.gameMode.firedInteract && this.player.gameMode.interactPosition.equals(movingobjectposition.getBlockPos()) && this.player.gameMode.interactHand == enumhand && ItemStack.isSameItemSameComponents(this.player.gameMode.interactItemStack, itemstack)) {
					cancelled = this.player.gameMode.interactResult;
				} else {
					PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK, movingobjectposition.getBlockPos(), movingobjectposition.getDirection(), itemstack, true, enumhand, movingobjectposition.getLocation());
					cancelled = event.useItemInHand() == Event.Result.DENY;
				}

				this.player.gameMode.firedInteract = false;
			} else {
				PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, org.bukkit.event.block.Action.RIGHT_CLICK_AIR, itemstack, enumhand);
				cancelled = event.useItemInHand() == Event.Result.DENY;
			}

			if(cancelled) {
				this.player.stopUsingItem(); // prevent lingering bow-draw state
				this.player.containerMenu.sendAllDataToRemote();
				return;
			}

			itemstack = this.player.getItemInHand(enumhand);
			if(itemstack.isEmpty()) {
				return;
			}

			InteractionResult enuminteractionresult = this.player.gameMode.useItem(this.player, worldserver, itemstack, enumhand);
			if(enuminteractionresult instanceof InteractionResult.Success enuminteractionresult_d) {
				if(enuminteractionresult_d.swingSource() == InteractionResult.SwingSource.SERVER) {
					this.player.swing(enumhand, true);
				}
			}
		}
	}

	private static boolean wasBlockPlacementAttempt(ServerPlayer entityplayer, ItemStack itemstack) {
		if(!itemstack.isEmpty()) {
			label21:
			{
				Item item = itemstack.getItem();
				if(!(item instanceof BlockItem)) {
					if(!(item instanceof BucketItem itembucket)) {
						break label21;
					}

					if(itembucket.getContent() == Fluids.EMPTY) {
						break label21;
					}
				}

				if(!entityplayer.getCooldowns().isOnCooldown(itemstack)) {
					return true;
				}
			}

		}
		return false;
	}
}
