package net.mistersecret312.aperture_innovations.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.init.AdvancementInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
	@Inject(method = "travel", at = @At("HEAD"))
	private void aperture$applySourcePhysics(Vec3 travelVector, CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;

		boolean hasLongFallBoots = entity.getItemBySlot(EquipmentSlot.FEET).is(ItemInit.LONG_FALL_BOOTS.get());

		if (hasLongFallBoots) {
			Vec3 motion = entity.getDeltaMovement();

			if (!entity.onGround()) {
				double airDragFactor = 0.91;
				double newX = motion.x() / airDragFactor;
				double newZ = motion.z() / airDragFactor;

				double newY = motion.y();
				double maxFallSpeed = 3.92; // Standard Minecraft / Portal high-speed freefall limit
				if (Math.abs(newY) > maxFallSpeed) {
					newY = Math.signum(newY) * Math.min(Math.abs(newY), 3.92);
				}

				entity.setDeltaMovement(new Vec3(newX, newY, newZ));
			}
			else if (entity instanceof Player player) {
				double maxInputSpeed = 0.42;
				Vec3 horizontalVel = new Vec3(motion.x(), 0, motion.z());
				double currentSpeed = horizontalVel.length();

				if (currentSpeed > maxInputSpeed) {
					Vec3 normalizedVel = horizontalVel.normalize();
					double projectedInput = travelVector.dot(new Vec3(normalizedVel.x, 0, normalizedVel.z));

					if (projectedInput > 0) {
						double restrictedX = motion.x() - (normalizedVel.x * projectedInput * 0.05);
						double restrictedZ = motion.z() - (normalizedVel.z * projectedInput * 0.05);
						entity.setDeltaMovement(new Vec3(restrictedX, motion.y(), restrictedZ));
					}
				}
			}
		}
	}

	@Inject(method = "checkTotemDeathProtection(Lnet/minecraft/world/damagesource/DamageSource;)Z",
	at = @At("HEAD"), cancellable = true)
	private void totemCheck(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir)
	{
		LivingEntity living = (LivingEntity) (Object) this;
		if(damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
			return;

		ItemStack stack = null;
		for(InteractionHand hand : InteractionHand.values())
		{
			ItemStack handStack = living.getItemInHand(hand);
			if(handStack.getItem() instanceof PortalGunItem portalGun)
			{
				ItemStack piercing = portalGun.getPiercingItem(handStack).copy();
				if(piercing.is(Items.TOTEM_OF_UNDYING))
				{
					stack = piercing;
					portalGun.setPiercingItem(handStack, ItemStack.EMPTY);
				}
			}
		}

		if (stack != null) {
			if (living instanceof ServerPlayer serverplayer) {
				serverplayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
				CriteriaTriggers.USED_TOTEM.trigger(serverplayer, stack);
				AdvancementInit.TOTEM_GUN_USE.get().trigger(serverplayer, stack);
				living.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
			}

			living.setHealth(1.0F);
			living.removeEffectsCuredBy(net.neoforged.neoforge.common.EffectCures.PROTECTED_BY_TOTEM);
			living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
			living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
			living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
			living.level().broadcastEntityEvent(living, (byte)35);
		}

		cir.setReturnValue(stack != null);
	}
}
