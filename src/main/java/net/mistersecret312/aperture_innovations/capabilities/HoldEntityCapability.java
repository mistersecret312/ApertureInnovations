package net.mistersecret312.aperture_innovations.capabilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.mistersecret312.aperture_innovations.network.ClientboundEntityHeldUpdatePacket;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class HoldEntityCapability implements INBTSerializable<CompoundTag>
{

	public boolean isHeld = false;

	public void tick(Level level, Entity entity)
	{
		if(!isHeld)
			return;

		Player player = findHoldingPlayer(level, entity);
		if(player == null)
		{
			this.setHeld(entity, false);
			entity.setNoGravity(false);
			return;
		}

		entity.setNoGravity(true);
		entity.resetFallDistance();

		Vec3 eyePos = player.getEyePosition();
		Vec3 lookVec = player.getViewVector(1.0F);
		Vec3 targetCenterPos = eyePos.add(lookVec.scale(3.0));

		Vec3 currentCenterPos = entity.getBoundingBox().getCenter();

		Vec3 pullVector = targetCenterPos.subtract(currentCenterPos);
		Vec3 desiredVel = pullVector.scale(0.4);

		double maxSpeed = 1.5;
		if (desiredVel.lengthSqr() > maxSpeed * maxSpeed)
			desiredVel = desiredVel.normalize().scale(maxSpeed);

		entity.setDeltaMovement(desiredVel);
		entity.hasImpulse = true;

		float targetYRot = -player.getYRot();
		entity.setYRot(Mth.approachDegrees(entity.getYRot(), targetYRot, 25.0F));
	}

	public Player findHoldingPlayer(Level level, Entity entity)
	{
		AABB box = new AABB(entity.blockPosition()).inflate(4);
		List<Player> players = new ArrayList<>();
		for(Player player : level.players())
		{
			if(box.contains(player.position()))
				players.add(player);
		}

		Player holdingPlayer = null;
		for(Player player : players)
		{
			ItemStack main = player.getMainHandItem();
			ItemStack off = player.getOffhandItem();
			boolean hasPortalGun = main.is(ItemInit.PORTAL_GUN.get()) || off.is(ItemInit.PORTAL_GUN.get());
			if(!hasPortalGun)
				continue;

			ItemStack gunStack = main.is(ItemInit.PORTAL_GUN.get()) ? main : off;
			PortalGunItem portalGun = (PortalGunItem) gunStack.getItem();

			Integer id = portalGun.getHeldEntity(gunStack);
			if(id == null)
				continue;

			if(id.equals(entity.getId()))
			{
				holdingPlayer = player;
				break;
			}
		}

		return holdingPlayer;
	}

	public void setHeld(Entity entity, boolean held)
	{
		if(!entity.level().isClientSide())
		{
			this.isHeld = held;
			PacketDistributor.sendToAllPlayers(new ClientboundEntityHeldUpdatePacket(entity.getId(), held));
		}
		else this.isHeld = held;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		CompoundTag tag = new CompoundTag();

		tag.putBoolean("isHeld", this.isHeld);
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
	{
		this.isHeld = nbt.getBoolean("isHeld");
	}
}
