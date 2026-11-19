package net.mistersecret312.aperture_innovations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.capabilities.ApertureCapability;
import net.mistersecret312.aperture_innovations.data.portal.PortalLink;
import net.mistersecret312.aperture_innovations.init.AttachmentTypeInit;
import net.mistersecret312.aperture_innovations.init.EntityInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin
{
	@Shadow public abstract boolean onGround();

	@Shadow public abstract Vec3 getDeltaMovement();

	@Unique
	public boolean isHandlingPortalMove = false;

	@Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
	public void noSuffocation(CallbackInfoReturnable<Boolean> cir)
	{
		Entity entity = (Entity) (Object) this;

		Pair<UUID, Boolean> pair = PortalUtilities.getClosestPortal(entity);
		UUID id = pair.getFirst();
		boolean isPrimary = pair.getSecond();
		if(id == null)
			return;

		Vec3 portalPos = PortalUtilities.getPortalPos(entity.level(), id, isPrimary);
		Vec2 portalRot = PortalUtilities.getPortalRotation(entity.level(), id, isPrimary);

		AABB box = PortalUtilities.getPortalBoundingBox(portalPos, portalRot.x, portalRot.y);
		if(box.intersects(entity.getBoundingBox()))
			cir.setReturnValue(false);
	}

//	@ModifyExpressionValue(method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
//			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBlockSpeedFactor()F"))
//	private float modifyFrictionValue(float original)
//	{
//		Entity entity = (Entity) (Object) this;
//		if(!entity.onGround())
//			return 0.98f;
//
//		return original;
//	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void aperture$applyNonLivingPhysics(CallbackInfo ci)
	{
		Entity entity = (Entity) (Object) this;
		if (entity.level().isClientSide() || entity instanceof LivingEntity)
			return;

		if (entity.getType().equals(EntityInit.CUBE.get()))
		{
			if (!this.onGround())
			{
				Vec3 motion = this.getDeltaMovement();
				double airDragFactor = 0.91;
				entity.setDeltaMovement(new Vec3(motion.x() / airDragFactor, motion.y() / airDragFactor, motion.z() / airDragFactor));
			}
		}
	}

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void tryingBetteR(MoverType type, Vec3 movement, CallbackInfo ci)
	{
		Entity entity = (Entity) (Object) this;
		if (entity.level().isClientSide())
			return;

		if (isHandlingPortalMove)
			return;

		ServerLevel serverLevel = (ServerLevel) entity.level();
		ApertureCapability aperture = entity.getData(AttachmentTypeInit.APERTURE);
		if (aperture == null)
			return;

		if (aperture.ignorePortalsTime > 0)
		{
			aperture.ignorePortalsTime--;
			return;
		}

		PortalLink closestLink = null;
		boolean isPrimaryPortal = true;
		double earliestT = 1.0;

		for (PortalLink link : PortalUtilities.getPortalLinks(serverLevel).values())
		{
			if (!link.isOpen())
				continue;

			double tPrimary = link.getPortalIntersectionFraction(entity, aperture, movement, true);
			if (tPrimary >= 0.0 && tPrimary < earliestT)
			{
				earliestT = tPrimary;
				closestLink = link;
				isPrimaryPortal = true;
			}

			double tSecondary = link.getPortalIntersectionFraction(entity, aperture, movement, false);
			if (tSecondary >= 0.0 && tSecondary < earliestT)
			{
				earliestT = tSecondary;
				closestLink = link;
				isPrimaryPortal = false;
			}
		}

		if (closestLink != null)
		{
			isHandlingPortalMove = true;
			try
			{
				Vec3 firstPartMovement = movement.scale(earliestT);
				if (firstPartMovement.lengthSqr() > 1.0E-7)
					entity.move(type, firstPartMovement);

				if (closestLink.executeSubTickTeleport(entity, serverLevel, isPrimaryPortal, earliestT, movement, type))
					ci.cancel();
			} finally {
				isHandlingPortalMove = false;
			}
		}
	}

	private double firstLengthSqr(Vec3 vec) {
		return vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
	}
}
