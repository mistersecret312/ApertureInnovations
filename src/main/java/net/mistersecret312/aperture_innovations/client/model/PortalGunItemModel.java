package net.mistersecret312.aperture_innovations.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariant;
import net.mistersecret312.aperture_innovations.items.CubeItem;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class PortalGunItemModel extends GeoModel<PortalGunItem>
{
	public ItemStack stack = ItemStack.EMPTY;

	@Override
	public ResourceLocation getModelResource(PortalGunItem animatable)
	{
		return ClientPortalGunVariant.DEFAULT_VARIANT.model();
	}

	@Override
	public ResourceLocation getTextureResource(PortalGunItem animatable)
	{
		return ClientPortalGunVariant.DEFAULT_VARIANT.texture();
	}

	@Override
	public ResourceLocation getModelResource(PortalGunItem animatable, @Nullable GeoRenderer<PortalGunItem> renderer)
	{
		if(renderer instanceof GeoItemRenderer<PortalGunItem> itemRenderer)
			return animatable.getGunVariant(itemRenderer.getCurrentItemStack()).model();

		return super.getModelResource(animatable, renderer);
	}

	@Override
	public ResourceLocation getTextureResource(PortalGunItem animatable, @Nullable GeoRenderer<PortalGunItem> renderer)
	{
		if(renderer instanceof GeoItemRenderer<PortalGunItem> itemRenderer)
			return animatable.getGunVariant(itemRenderer.getCurrentItemStack()).texture();

		return super.getTextureResource(animatable, renderer);
	}

	@Override
	public ResourceLocation getAnimationResource(PortalGunItem animatable)
	{
		if(!stack.isEmpty() && stack.getItem() instanceof PortalGunItem)
			return animatable.getGunVariant(stack).animation();
		return ClientPortalGunVariant.DEFAULT_VARIANT.animation();
	}
}
