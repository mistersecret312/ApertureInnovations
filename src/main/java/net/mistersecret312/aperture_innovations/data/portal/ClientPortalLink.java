package net.mistersecret312.aperture_innovations.data.portal;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariants;
import net.mistersecret312.aperture_innovations.datapack.PortalGunVariant;

import java.util.UUID;


public class ClientPortalLink
{
	public UUID linkID;

	private Portal primaryPortal = new Portal();
	private Portal secondaryPortal = new Portal();
	
	public ResourceLocation variantKey;

	public ClientPortalLink()
	{}

	public ClientPortalLink(UUID linkID,
					 ResourceLocation variantKey)
	{
		this.linkID = linkID;
		this.variantKey = variantKey;
	}

	public boolean isOpen()
	{
		return primaryPortal.isOpen() && secondaryPortal.isOpen();
	}

	public ClientPortalGunVariant getVariant()
	{
		Registry<PortalGunVariant> registry =
				Minecraft.getInstance().level.registryAccess().registryOrThrow(PortalGunVariant.REGISTRY_KEY);
		PortalGunVariant dataVariant = registry.get(variantKey);
		if(dataVariant == null)
			return ClientPortalGunVariant.DEFAULT_VARIANT;

		return ClientPortalGunVariants.getPortalGunVariant(dataVariant.getClientVariant());
	}
	
	public UUID linkID()
	{
		return linkID;
	}

	public ResourceLocation variantKey()
	{
		return variantKey;
	}

	public Portal getPrimaryPortal()
	{
		return primaryPortal;
	}

	public Portal getSecondaryPortal()
	{
		return secondaryPortal;
	}

	public void setPrimaryPortal(Portal portal)
	{
		this.primaryPortal = portal;
	}

	public void setSecondaryPortal(Portal portal)
	{
		this.secondaryPortal = portal;
	}
}
