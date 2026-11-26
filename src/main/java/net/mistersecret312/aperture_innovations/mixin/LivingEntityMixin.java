package net.mistersecret312.aperture_innovations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
import net.mistersecret312.aperture_innovations.init.AdvancementInit;
import net.mistersecret312.aperture_innovations.init.AttachmentTypeInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
	@ModifyExpressionValue(method = "travel(Lnet/minecraft/world/phys/Vec3;)V",
	at = @At(value = "CONSTANT", args = "floatValue=0.91"))
	public float dontAir(float original)
	{
		LivingEntity living = (LivingEntity) (Object) this;
		boolean hasLongFallBoots = living.getItemBySlot(EquipmentSlot.FEET).is(ItemInit.LONG_FALL_BOOTS.get());
		if(hasLongFallBoots && !living.onGround())
			return 1f;

		return original;
	}

	@ModifyArg(method = "travel(Lnet/minecraft/world/phys/Vec3;)V",
	at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"))
	private float modifyFrictionValue(float friction)
	{
		LivingEntity living = (LivingEntity) (Object) this;
		boolean hasLongFallBoots = living.getItemBySlot(EquipmentSlot.FEET).is(ItemInit.LONG_FALL_BOOTS.get());
		if(hasLongFallBoots && !living.onGround())
			friction = 1f;
		return friction;
	}

	@Inject(method = "shouldDiscardFriction", at = @At("HEAD"), cancellable = true)
	private void discardFriction(CallbackInfoReturnable<Boolean> cir)
	{
		LivingEntity living = (LivingEntity) (Object) this;
		boolean hasLongFallBoots = living.getItemBySlot(EquipmentSlot.FEET).is(ItemInit.LONG_FALL_BOOTS.get());
		if(living instanceof Player player)
		{
			if(player.getAbilities().flying)
				return;
		}

		if(hasLongFallBoots && !living.onGround())
			cir.setReturnValue(true);
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
