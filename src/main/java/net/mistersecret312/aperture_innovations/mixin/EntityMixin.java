package net.mistersecret312.aperture_innovations.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.init.AttachmentTypeInit;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public class EntityMixin
{
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

	@ModifyExpressionValue(method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;getBlockSpeedFactor()F"
			)
	)
	private float modifyFrictionValue(float original)
		{
			Entity entity = (Entity) (Object) this;
			float frictionlessTime = entity.getData(AttachmentTypeInit.APERTURE.get()).frictionlessTime;
			if(frictionlessTime != 0 && !entity.onGround())
				return 0.98f;

			return original;
		}
}
