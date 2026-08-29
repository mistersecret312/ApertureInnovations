package net.mistersecret312.aperture_innovations.client.model;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariants;
import net.mistersecret312.aperture_innovations.items.CubeItem;
import net.mistersecret312.aperture_innovations.items.MultiToolItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class MultiToolItemModel extends GeoModel<MultiToolItem>
{
	@Override
	public ResourceLocation getModelResource(MultiToolItem animatable)
	{
		return ClientMultiToolVariant.DEFAULT_VARIANT.modelPath();
	}

	@Override
	public ResourceLocation getModelResource(MultiToolItem animatable, @Nullable GeoRenderer<MultiToolItem> renderer)
	{
		if(renderer instanceof GeoItemRenderer<MultiToolItem> itemRenderer)
		{
			ClientMultiToolVariant variant = ClientMultiToolVariants.getMultiToolVariant(animatable.getVariantKey(itemRenderer.getCurrentItemStack()));
			return variant.modelPath();
		}
		return super.getModelResource(animatable, renderer);
	}

	@Override
	public ResourceLocation getTextureResource(MultiToolItem animatable)
	{
		return ClientMultiToolVariant.DEFAULT_VARIANT.hullTexture();
	}

	@Override
	public ResourceLocation getAnimationResource(MultiToolItem animatable)
	{
		return null;
	}
}
