package net.mistersecret312.aperture_innovations.client.model;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.PedestalButtonBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.VitalApparatusVentBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class VitalApparatusVentModel extends GeoModel<VitalApparatusVentBlockEntity>
{

	@Override
	public ResourceLocation getModelResource(VitalApparatusVentBlockEntity animatable)
	{
		return animatable.getClientVariant().modelPath();
	}

	@Override
	public ResourceLocation getTextureResource(VitalApparatusVentBlockEntity animatable)
	{
		return animatable.getClientVariant().hullTexture();
	}

	@Override
	public ResourceLocation getAnimationResource(VitalApparatusVentBlockEntity animatable)
	{
		return animatable.getClientVariant().animationPath();
	}
}
