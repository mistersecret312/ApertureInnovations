package net.mistersecret312.aperture_innovations.client.model;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.LargeButtonBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.PedestalButtonBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class LargeButtonModel extends GeoModel<LargeButtonBlockEntity>
{

	@Override
	public ResourceLocation getModelResource(LargeButtonBlockEntity animatable)
	{
		return animatable.getClientVariant().modelPath();
	}

	@Override
	public ResourceLocation getTextureResource(LargeButtonBlockEntity animatable)
	{
		return animatable.getClientVariant().hullTexture();
	}

	@Override
	public ResourceLocation getAnimationResource(LargeButtonBlockEntity animatable)
	{
		return animatable.getClientVariant().animationPath();
	}
}
