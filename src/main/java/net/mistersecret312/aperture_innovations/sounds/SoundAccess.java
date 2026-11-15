package net.mistersecret312.aperture_innovations.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariant;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.utilities.ClientPortalUtilities;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;

import java.util.UUID;

public class SoundAccess
{
	protected static Minecraft minecraft = Minecraft.getInstance();

	public static void playPortalAmbient(UUID uuid, boolean isPrimary, boolean stop)
	{
		ClientPortalLink link = PortalUtilities.getPortalLinks().get(uuid);
		if(minecraft.level.dimension() != PortalUtilities.getPortalDimension(minecraft.level, uuid, isPrimary))
			return;

		PortalSoundWrapper.PortalAmbient ambientSound = ClientPortalUtilities.getAmbientSound(uuid, isPrimary);

		if(link.isOpen() && PortalUtilities.getPortalPos(minecraft.level, uuid, isPrimary) != null)
		{
			if(ambientSound == null)
			{
				ambientSound = new PortalSoundWrapper.PortalAmbient(link, isPrimary);
				ClientPortalUtilities.setAmbientSound(ambientSound, uuid, isPrimary);
			}

			Vec3 portalPos = isPrimary ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();
			if(!portalPos.equals(ambientSound.sound.portalPos))
			{
				ambientSound.stopSound();
				ambientSound = new PortalSoundWrapper.PortalAmbient(link, isPrimary);
				ClientPortalUtilities.setAmbientSound(ambientSound, uuid, isPrimary);
			}

			if(stop)
				ambientSound.stopSound();
			else if(!ambientSound.isPlaying())
			{
				ambientSound.playSound();
			}
		}
	}

	public static void playGunZap(UUID playerID, boolean stop)
	{
		GunSoundWrapper.ZapSound ambientSound = ClientPortalUtilities.getPlayerZapSound(playerID);
		Level level = Minecraft.getInstance().level;
		if(level == null)
			return;

		Player player = level.getPlayerByUUID(playerID);
		if(ambientSound == null)
		{
			ambientSound = new GunSoundWrapper.ZapSound(player);
			ClientPortalUtilities.setPlayerZapSound(ambientSound, playerID);
		}

		if(stop)
		{
			ambientSound.stopSound();
			ClientPortalUtilities.setPlayerZapSound(null, playerID);
			return;
		}

		if(!ambientSound.isPlaying())
			ambientSound.playSound();
	}

	public static void playOpenPortalSound(UUID linkID, boolean isPrimary)
	{
		if(minecraft.level.dimension() != PortalUtilities.getPortalDimension(minecraft.level, linkID, isPrimary))
			return;
		GenericPortalSound sound = new GenericPortalSound(PortalUtilities.getPortalLinks().get(linkID), isPrimary,
				getPortalOpenSound(linkID, isPrimary), 5, 10, 0.35F);
		ClientPortalUtilities.setPortalOpeningAnimationProgress(0F, linkID, isPrimary);
		minecraft.getSoundManager().play(sound);
	}

	public static void playEnterPortalSound(UUID linkID, boolean isPrimary)
	{
		if(minecraft.level.dimension() != PortalUtilities.getPortalDimension(minecraft.level, linkID, isPrimary))
			return;
		GenericPortalSound sound = new GenericPortalSound(PortalUtilities.getPortalLinks().get(linkID), isPrimary,
				getEnterPortalSound(linkID, isPrimary), 5, 10, 0.35F);
		minecraft.getSoundManager().play(sound);
	}

	public static void playFizzlePortalSound(UUID linkID, boolean isPrimary)
	{
		if(minecraft.level.dimension() != PortalUtilities.getPortalDimension(minecraft.level, linkID, isPrimary))
			return;
		GenericPortalSound sound = new GenericPortalSound(PortalUtilities.getPortalLinks().get(linkID), isPrimary,
				getFizzlePortalSound(linkID, isPrimary), 5, 10, 0.35F);
		ClientPortalUtilities.setPortalOpeningAnimationProgress(0F, linkID, isPrimary);
		minecraft.getSoundManager().play(sound);
		playPortalAmbient(linkID, isPrimary, true);
	}

	public static void playInvalidSurfaceSound(UUID linkID, BlockPos pos, boolean isPrimary)
	{
		GenericGunSound sound = new GenericGunSound(PortalUtilities.getPortalLinks().get(linkID), pos, isPrimary,
				getInvalidSurfaceSound(linkID, isPrimary), 5, 10, 0.35F);
		minecraft.getSoundManager().play(sound);
	}

	public static void playGunActivateSound(UUID linkID, BlockPos pos, boolean isPrimary)
	{
		GenericGunSound sound = new GenericGunSound(PortalUtilities.getPortalLinks().get(linkID), pos, isPrimary,
				getGunActivateSound(linkID), 5, 10, 0.35F);
		minecraft.getSoundManager().play(sound);
	}

	public static void playShootPortalSound(UUID linkID, BlockPos pos, boolean isPrimary)
	{
		GenericGunSound sound = new GenericGunSound(PortalUtilities.getPortalLinks().get(linkID), pos, isPrimary,
				getShotPortalSound(linkID, isPrimary), 5, 10, 0.35F);
		minecraft.getSoundManager().play(sound);
	}

	public static void playResetPortalSound(UUID linkID, BlockPos pos, boolean isPrimary)
	{
		GenericGunSound sound = new GenericGunSound(PortalUtilities.getPortalLinks().get(linkID), pos, isPrimary,
				getGunResetSound(linkID), 5, 10, 0.35F);
		minecraft.getSoundManager().play(sound);
	}

	public static SoundEvent getPortalAmbient(UUID uuid, boolean isPrimary)
	{
		ClientPortalGunVariant variant = PortalUtilities.getPortalLinks().get(uuid).getVariant();
		if(isPrimary)
			return SoundEvent.createVariableRangeEvent(variant.primaryPortal().getAmbientSound());
		else return SoundEvent.createVariableRangeEvent(variant.secondaryPortal().getAmbientSound());
	}

	public static SoundEvent getPortalOpenSound(UUID uuid, boolean isPrimary)
	{
		ClientPortalGunVariant variant = PortalUtilities.getPortalLinks().get(uuid).getVariant();
		if(isPrimary)
			return SoundEvent.createVariableRangeEvent(variant.primaryPortal().getOpeningSound());
		else return SoundEvent.createVariableRangeEvent(variant.secondaryPortal().getOpeningSound());
	}

	public static SoundEvent getEnterPortalSound(UUID uuid, boolean isPrimary)
	{
		ClientPortalGunVariant variant = PortalUtilities.getPortalLinks().get(uuid).getVariant();
		if(isPrimary)
			return SoundEvent.createVariableRangeEvent(variant.primaryPortal().getEnterSound());
		else return SoundEvent.createVariableRangeEvent(variant.secondaryPortal().getEnterSound());
	}

	public static SoundEvent getFizzlePortalSound(UUID uuid, boolean isPrimary)
	{
		ClientPortalGunVariant variant = PortalUtilities.getPortalLinks().get(uuid).getVariant();
		if(isPrimary)
			return SoundEvent.createVariableRangeEvent(variant.primaryPortal().getFizzleSound());
		else return SoundEvent.createVariableRangeEvent(variant.secondaryPortal().getFizzleSound());
	}

	public static SoundEvent getInvalidSurfaceSound(UUID uuid, boolean isPrimary)
	{
		ClientPortalGunVariant variant = PortalUtilities.getPortalLinks().get(uuid).getVariant();
		if(isPrimary)
			return SoundEvent.createVariableRangeEvent(variant.primaryPortal().getInvalidSurfaceSound());
		else return SoundEvent.createVariableRangeEvent(variant.secondaryPortal().getInvalidSurfaceSound());
	}

	public static SoundEvent getGunActivateSound(UUID uuid)
	{
		ClientPortalGunVariant variant = PortalUtilities.getPortalLinks().get(uuid).getVariant();
		return SoundEvent.createVariableRangeEvent(variant.activationSound());
	}

	public static SoundEvent getShotPortalSound(UUID uuid, boolean isPrimary)
	{
		ClientPortalGunVariant variant = PortalUtilities.getPortalLinks().get(uuid).getVariant();
		if(isPrimary)
			return SoundEvent.createVariableRangeEvent(variant.primaryPortal().getShotSound());
		else return SoundEvent.createVariableRangeEvent(variant.secondaryPortal().getShotSound());
	}

	public static SoundEvent getGunResetSound(UUID uuid)
	{
		ClientPortalGunVariant variant = PortalUtilities.getPortalLinks().get(uuid).getVariant();
		return SoundEvent.createVariableRangeEvent(variant.resetSound());
	}

}

