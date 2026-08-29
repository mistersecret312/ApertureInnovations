package net.mistersecret312.aperture_innovations.client.model;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.PedestalButtonBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class PedestalButtonModel extends GeoModel<PedestalButtonBlockEntity>
{

	@Override
	public ResourceLocation getModelResource(PedestalButtonBlockEntity animatable)
	{
		return animatable.getClientVariant().modelPath();
	}

	@Override
	public ResourceLocation getTextureResource(PedestalButtonBlockEntity animatable)
	{
		return animatable.getClientVariant().hullTexture();
	}

	@Override
	public ResourceLocation getAnimationResource(PedestalButtonBlockEntity animatable)
	{
		return animatable.getClientVariant().animationPath();
	}
}
