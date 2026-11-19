package net.mistersecret312.aperture_innovations.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalTransitHandler;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin
{
	@Inject(method = "suffocatesAt(Lnet/minecraft/core/BlockPos;)Z",
	at = @At("RETURN"), cancellable = true)
	public void dontSuffocate(BlockPos pos, CallbackInfoReturnable<Boolean> cir)
	{
		LocalPlayer player = (LocalPlayer) (Object) this;
		Level level = Minecraft.getInstance().level;
		if(level == null)
		{
			cir.cancel();
			return;
		}

		Pair<UUID, Boolean> portal = PortalUtilities.getClosestPortal(player);

		AABB playerBox = player.getBoundingBox();

		UUID uuid = portal.getFirst();
		boolean isPrimary = portal.getSecond();
		if(uuid == null)
		{
			cir.cancel();
			return;
		}

		boolean isOpen = PortalUtilities.isPortalOpen(level, uuid);

		Vec3 portalPos = PortalUtilities.getPortalPos(level, uuid, isPrimary);

		Vec2 rotation = PortalUtilities.getPortalRotation(level, uuid, isPrimary);

		List<VoxelShape> reAddVoxels = PortalUtilities.getPortalVoxels(level, uuid, isPrimary,
				portalPos, rotation.x, rotation.y);

		AABB portalBox = PortalUtilities.getPortalBoundingBox(portalPos, rotation.x, rotation.y);
		AABB teleportBox = PortalUtilities.getPortalTeleportBox(portalPos, rotation.x, rotation.y);
		AABB floorBox = PortalUtilities.getPortalFloorBox(portalPos, rotation.x, rotation.y).inflate(0d, 0.01d, 0d);

		Direction direction = PortalUtilities.getPortalDirection(level, uuid, isPrimary);

		Vec3 logicPos = teleportBox.getCenter();
		logicPos = logicPos.add(direction.getOpposite().getStepX() * player.getBbWidth() / 2f,
				direction.getOpposite().getStepY() * player.getBbHeight() / 1.25f,
				direction.getOpposite().getStepZ() * player.getBbWidth() / 2f);

		Vec3 currentPos = player.position().add(0, player.getBbHeight() / 2f, 0);
		Vec3 offsetFromPortal = currentPos.subtract(logicPos);

		double dotProduct = offsetFromPortal.dot(new Vec3(direction.step()));
		if(floorBox.intersects(playerBox) && isOpen)
		{
			cir.setReturnValue(false);
		}

		if(portalBox.intersects(playerBox) && dotProduct > 0)
		{
			cir.setReturnValue(false);
			return;
		}

		for(VoxelShape voxel : reAddVoxels)
		{
			if(voxel.isEmpty() || !isOpen)
				continue;

			for(AABB aabb : voxel.toAabbs())
			{
				if(aabb.intersects(playerBox))
				{
					cir.setReturnValue(false);
					return;
				}
			}
		}
	}
}
