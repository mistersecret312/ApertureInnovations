package net.mistersecret312.aperture_innovations.client.model;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariants;
import net.mistersecret312.aperture_innovations.entities.CubeEntity;
import net.mistersecret312.aperture_innovations.items.CubeItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class CubeItemModel extends GeoModel<CubeItem>
{
	@Override
	public ResourceLocation getModelResource(CubeItem animatable)
	{
		return ClientCubeVariant.DEFAULT_VARIANT.modelPath();
	}

	@Override
	public ResourceLocation getModelResource(CubeItem animatable, @Nullable GeoRenderer<CubeItem> renderer)
	{
		if(renderer instanceof GeoItemRenderer<CubeItem> itemRenderer)
			return animatable.getCubeVariant(itemRenderer.getCurrentItemStack()).modelPath();

		return super.getModelResource(animatable, renderer);
	}

	@Override
	public ResourceLocation getTextureResource(CubeItem animatable)
	{
		return ClientCubeVariant.DEFAULT_VARIANT.hullTexture();
	}

	@Override
	public ResourceLocation getAnimationResource(CubeItem animatable)
	{
		return null;
	}
}
