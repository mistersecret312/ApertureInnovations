package net.mistersecret312.aperture_innovations.client.model;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.entities.CubeEntity;
import software.bernie.geckolib.model.GeoModel;

public class CubeModel extends GeoModel<CubeEntity>
{
	@Override
	public ResourceLocation getModelResource(CubeEntity animatable)
	{
		return animatable.getClientVariant().modelPath();
	}

	@Override
	public ResourceLocation getTextureResource(CubeEntity animatable)
	{
		return animatable.getClientVariant().hullTexture();
	}

	@Override
	public ResourceLocation getAnimationResource(CubeEntity animatable)
	{
		return null;
	}
}
