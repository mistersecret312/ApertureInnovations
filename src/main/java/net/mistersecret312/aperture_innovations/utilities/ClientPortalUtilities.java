package net.mistersecret312.aperture_innovations.utilities;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.client.ColorUtil;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariant;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.sounds.GunSoundWrapper;
import net.mistersecret312.aperture_innovations.sounds.GunZapSound;
import net.mistersecret312.aperture_innovations.sounds.PortalSoundWrapper;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class ClientPortalUtilities
{
	public static HashMap<UUID, Pair<PortalSoundWrapper.PortalAmbient, PortalSoundWrapper.PortalAmbient>> AMBIENTS = new HashMap<>();
	public static HashMap<UUID, GunSoundWrapper.ZapSound> PLAYERS = new HashMap<>();

	public static HashMap<UUID, Pair<Float, Float>> OPENING_ANIMATIONS = new HashMap<>();

	public static ColorUtil.RGBA getPortalColor(ClientPortalLink link, boolean isPrimary)
	{
		ClientPortalGunVariant variant = link.getVariant();
		int gunColor = isPrimary ? link.getPrimaryPortal().getColor() : link.getSecondaryPortal().getColor();
		Color rgbaColor = new Color(gunColor, false);

		ColorUtil.RGBA variantColor = isPrimary ? variant.primaryPortal().getColor() : variant.secondaryPortal().getColor();
		if(rgbaColor.getRGB() == -1)
			return variantColor;
		else
		{
			return new ColorUtil.RGBA(rgbaColor.getRed()/255f, rgbaColor.getGreen()/255f, rgbaColor.getBlue()/255f, 1F);
		}
	}

	public static ResourceLocation getCrosshairTexture(ClientPortalLink link, boolean isPrimary)
	{
		ClientPortalGunVariant variant = link.getVariant();
		int gunColor = isPrimary ? link.getPrimaryPortal().getColor() : link.getSecondaryPortal().getColor();
		Color rgbaColor = new Color(gunColor, false);
		if(rgbaColor.getRGB() == -1)
			return variant.crosshairTexture();
		else
		{
			return isPrimary
						   ? variant.primaryPortal().isGenericColoring()
									 ? variant.genericPortal().getCrosshairTexture() :
									 variant.crosshairTexture()
						   : variant.secondaryPortal().isGenericColoring() ?
									 variant.genericPortal().getCrosshairTexture() :
									 variant.crosshairTexture();
		}
	}

	public static ResourceLocation getPortalHighlightTexture(ClientPortalLink link, boolean isPrimary)
	{
		ClientPortalGunVariant variant = link.getVariant();
		int gunColor = isPrimary ? link.getPrimaryPortal().getColor() : link.getSecondaryPortal().getColor();
		Color rgbaColor = new Color(gunColor, false);
		if(rgbaColor.getRGB() == -1)
			return isPrimary ? variant.primaryPortal().getHighlightTexture() : variant.secondaryPortal().getHighlightTexture();
		else
		{
			return isPrimary ? variant.primaryPortal().isGenericColoring() ?
									   variant.genericPortal().getHighlightTexture() :
									   variant.primaryPortal().getHighlightTexture()
						   : variant.secondaryPortal().isGenericColoring() ?
									 variant.genericPortal().getHighlightTexture() :
									 variant.secondaryPortal().getHighlightTexture();
		}

	}

	public static ResourceLocation getPortalGunCoreTexture(ClientPortalLink link, int lastPortal)
	{
		ClientPortalGunVariant variant = link.getVariant();

		ResourceLocation genericCore = variant.genericPortal().getCoreTexture();
		ResourceLocation idleCore = variant.idleCoreTexture();
		ResourceLocation primaryCore = variant.primaryPortal().getCoreTexture();
		ResourceLocation secondaryCore = variant.secondaryPortal().getCoreTexture();

		if(lastPortal == -1)
			return idleCore;
		boolean isPrimary = lastPortal == 0;

		int gunColor = isPrimary ? link.getPrimaryPortal().getColor() : link.getSecondaryPortal().getColor();
		Color rgbaColor = new Color(gunColor, false);
		if(rgbaColor.getRGB() == -1)
			return isPrimary ? primaryCore : secondaryCore;
		else
		{
			return isPrimary ? variant.primaryPortal().isGenericColoring() ?
									   genericCore : primaryCore
						     : variant.secondaryPortal().isGenericColoring() ?
									   genericCore : secondaryCore;
		}
	}

	public static ResourceLocation getPortalGunTexture(ClientPortalLink link)
	{
		ClientPortalGunVariant variant = link.getVariant();
		return variant.texture();
	}

	public static ResourceLocation getPortalClosedTexture(ClientPortalLink link, boolean isPrimary)
	{
		ClientPortalGunVariant variant = link.getVariant();
		int gunColor = isPrimary ? link.getPrimaryPortal().getColor() : link.getSecondaryPortal().getColor();
		Color rgbaColor = new Color(gunColor, false);
		if(rgbaColor.getRGB() == -1)
			return isPrimary ? variant.primaryPortal().getClosedTexture() : variant.secondaryPortal().getClosedTexture();
		else
		{
			return isPrimary ? variant.primaryPortal().isGenericColoring() ?
									   variant.genericPortal().getClosedTexture() :
									   variant.primaryPortal().getClosedTexture()
						   : variant.secondaryPortal().isGenericColoring() ?
									 variant.genericPortal().getClosedTexture() :
									 variant.secondaryPortal().getClosedTexture();
		}
	}

	public static ResourceLocation getPortalVortexTexture(ClientPortalLink link, boolean isPrimary)
	{
		ClientPortalGunVariant variant = link.getVariant();
		int gunColor = isPrimary ? link.getPrimaryPortal().getColor() : link.getSecondaryPortal().getColor();
		Color rgbaColor = new Color(gunColor, false);
		if(rgbaColor.getRGB() == -1)
			return isPrimary ? variant.primaryPortal().getVortexTexture() : variant.secondaryPortal().getVortexTexture();
		else
		{
			return isPrimary ? variant.primaryPortal().isGenericColoring() ?
									   variant.genericPortal().getVortexTexture() :
									   variant.primaryPortal().getVortexTexture()
						   : variant.secondaryPortal().isGenericColoring() ?
									 variant.genericPortal().getVortexTexture() :
									 variant.secondaryPortal().getVortexTexture();
		}
	}

	public static PortalSoundWrapper.PortalAmbient getAmbientSound(UUID uuid, boolean isPrimary)
	{
		Pair<PortalSoundWrapper.PortalAmbient, PortalSoundWrapper.PortalAmbient> pair = AMBIENTS.getOrDefault(uuid, new Pair<>(null, null));
		if(isPrimary)
			return pair.getFirst();
		else return pair.getSecond();
	}

	public static void setAmbientSound(PortalSoundWrapper.PortalAmbient ambient, UUID uuid, boolean isPrimary)
	{
		Pair<PortalSoundWrapper.PortalAmbient, PortalSoundWrapper.PortalAmbient> pair = AMBIENTS.getOrDefault(uuid, new Pair<>(null, null));
		if(isPrimary)
			pair = new Pair<>(ambient, pair.getSecond());
		else pair = new Pair<>(pair.getFirst(), ambient);

		AMBIENTS.put(uuid, pair);
	}

	public static GunSoundWrapper.ZapSound getPlayerZapSound(UUID uuid)
	{
		return PLAYERS.getOrDefault(uuid, null);
	}

	public static void setPlayerZapSound(GunSoundWrapper.ZapSound ambient, UUID uuid)
	{
		PLAYERS.put(uuid, ambient);
	}

	public static float getPortalOpeningAnimationProgress(UUID uuid, boolean isPrimary)
	{
		Pair<Float, Float> pair = OPENING_ANIMATIONS.getOrDefault(uuid, new Pair<>(0F, 0F));
		if(isPrimary)
			return pair.getFirst();
		else return pair.getSecond();
	}

	public static void setPortalOpeningAnimationProgress(float progress, UUID uuid, boolean isPrimary)
	{
		Pair<Float, Float> pair = OPENING_ANIMATIONS.getOrDefault(uuid, new Pair<>(0F, 0F));
		if(isPrimary)
			pair = new Pair<>(progress, pair.getSecond());
		else pair = new Pair<>(pair.getFirst(), progress);

		OPENING_ANIMATIONS.put(uuid, pair);
	}
}
