package net.mistersecret312.aperture_innovations.capabilities;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.init.NetworkInit;
import net.mistersecret312.aperture_innovations.network.ClientboundApertureCapabilityPacket;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.UUID;

public class ApertureCapability implements INBTSerializable<CompoundTag>
{
	public static final EntityCapability<ApertureCapability, Void> APERTURE_CAPABILITY = EntityCapability.createVoid(
		ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "aperture"), ApertureCapability.class);

	public Vector3d distanceVec = new Vector3d();
	public Pair<UUID, Boolean> portal;

	public double horizontalDistance = 0;
	public double verticalDistance = 0;

	public int frictionlessTime = 0;
	public int ignorePortalsTime = 0;

	public void tick(Level level, Entity entity)
	{
		if(level.isClientSide())
			return;
		//TODO  do better than frictionlesstime.
		frictionlessTime = 0;

		if(frictionlessTime > 0)
			entity.setOnGround(false);

		if(entity instanceof LivingEntity living)
		{
			if(frictionlessTime != 0 && !entity.onGround())
			{
				living.setDiscardFriction(true);
			}
			else
			{
				living.setDiscardFriction(false);
			}
		}

		if(entity instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new ClientboundApertureCapabilityPacket(frictionlessTime));

		if(frictionlessTime > 0)
			frictionlessTime--;
		if(ignorePortalsTime > 0)
			ignorePortalsTime--;

		if(shouldReset(level, entity))
			reset();

		else if(portal != null)
		{
			Vec3 speed = entity.getDeltaMovement();
			Vector3f movementVector = new Vector3f(speed.x > 0 ? (float) speed.x : (float) -speed.x,
					speed.y > 0 ? (float) speed.y : (float) -speed.y, speed.z > 0 ? (float) speed.z : (float) -speed.z);
			this.distanceVec.add(movementVector);
		}
	}

	public boolean shouldReset(Level level, Entity entity)
	{
		if(portal != null)
		{
			Vec3 pos = PortalUtilities.getPortalPos(level, portal.getFirst(), portal.getSecond());
			if(pos != null && pos.distanceTo(entity.position()) < 2)
				return false;
		}
		if(entity.onGround())
			return true;
		if(entity instanceof LivingEntity && ((LivingEntity) entity).isFallFlying())
		{
			return true;
		}
		if(entity instanceof ServerPlayer && ((ServerPlayer) entity).getAbilities().flying)
		{
			return true;
		}

		return true;
	}

	public void reset()
	{
		portal = null;
		this.distanceVec = new Vector3d();
		this.horizontalDistance = 0;
		this.verticalDistance = 0;
		this.frictionlessTime = 0;
		this.ignorePortalsTime = 0;
	}

	public void updateDistance()
	{
		if(portal == null || this.distanceVec == null)
			return;

		if(distanceVec.lengthSquared() > 0)
		{
			horizontalDistance += Mth.sqrt((float) (Math.pow(distanceVec.x, 2) + Math.pow(distanceVec.z, 2)));
			verticalDistance += Math.sqrt(Math.pow(distanceVec.y, 2));
		}

		distanceVec = new Vector3d();
	}

	public void setFrictionlessTime(int frictionlessTime)
	{
		if(this.frictionlessTime > frictionlessTime)
			return;

		this.frictionlessTime = frictionlessTime;
	}

	public void setIgnorePortalsTime(int ignorePortalsTime)
	{
		this.ignorePortalsTime = ignorePortalsTime;
	}

	public void setPortal(Pair<UUID, Boolean> portal)
	{
		this.portal = portal;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		CompoundTag tag = new CompoundTag();

		CompoundTag distanceTag = new CompoundTag();
		distanceTag.putDouble("x", distanceVec.x);
		distanceTag.putDouble("y", distanceVec.y);
		distanceTag.putDouble("z", distanceVec.z);

		tag.put("distance", distanceTag);

		tag.putDouble("horizontal", horizontalDistance);
		tag.putDouble("vertical", verticalDistance);

		tag.putInt("frictionlessTime", frictionlessTime);
		tag.putInt("ignorePortalsTime", ignorePortalsTime);

		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
	{
		Vector3d distanceVec = new Vector3d();

		CompoundTag distanceTag = nbt.getCompound("distance");
		distanceVec.x = distanceTag.getDouble("x");
		distanceVec.y = distanceTag.getDouble("y");
		distanceVec.z = distanceTag.getDouble("z");

		this.distanceVec = distanceVec;

		this.horizontalDistance = nbt.getDouble("horizontal");
		this.verticalDistance = nbt.getDouble("vertical");

		this.frictionlessTime = nbt.getInt("frictionlessTime");
		this.ignorePortalsTime = nbt.getInt("ignorePortalsTime");
	}
}
