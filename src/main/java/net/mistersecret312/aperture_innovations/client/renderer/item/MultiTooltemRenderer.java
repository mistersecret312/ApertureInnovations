package net.mistersecret312.aperture_innovations.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.client.model.MultiToolItemModel;
import net.mistersecret312.aperture_innovations.client.model.MultiToolItemModel;
import net.mistersecret312.aperture_innovations.client.renderer.ColoredGlowingLayer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariants;
import net.mistersecret312.aperture_innovations.items.MultiToolItem;
import net.mistersecret312.aperture_innovations.items.MultiToolItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.specialty.DynamicGeoItemRenderer;

import java.awt.*;

public class MultiTooltemRenderer extends DynamicGeoItemRenderer<MultiToolItem>
{

    public MultiTooltemRenderer()
    {
        super(new MultiToolItemModel());

        this.addRenderLayer(new ColoredGlowingLayer<>(this,
                (item, bone) -> this.getTexture(bone, item),
                (item, bone) -> this.getColor(bone, item),
                (item, bone) -> this.getRenderType(bone, item)));
        this.addRenderLayer(new ColoredGlowingLayer<>(this,
                (item, bone) -> getHullTexture(bone, item),
                (item, bone) -> getHullColor(bone, item),
                (item, bone) -> RenderType.entityTranslucent(getHullTexture(bone, item))
        ));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, MultiToolItem animatable, BakedGeoModel model,
                               @Nullable RenderType renderType, MultiBufferSource bufferSource,
                               @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                               int packedOverlay, int colour)
    {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, colour);
    }

    public ResourceLocation getTexture(GeoBone bone, MultiToolItem animatable)
    {
        int color = animatable.getGlowColor(getCurrentItemStack());
        ClientMultiToolVariant variant = ClientMultiToolVariants.getMultiToolVariant(animatable.getVariantKey(getCurrentItemStack()));
        if(!bone.getName().contains("Colored"))
            return null;

        ResourceLocation texture = variant.glowTexture().orElse(null);
        if(color != -1 && color != 16777215)
            texture = variant.genericGlowTexture().orElse(null);

        return texture;
    }

    public ResourceLocation getHullTexture(GeoBone bone, MultiToolItem animatable)
    {

        ClientMultiToolVariant variant = ClientMultiToolVariants.getMultiToolVariant(animatable.getVariantKey(getCurrentItemStack()));
        return variant.hullTexture();
    }

    public int getColor(GeoBone bone, MultiToolItem animatable)
    {
        int color = animatable.getGlowColor(getCurrentItemStack());
        if(color != 0)
            return new Color(color, false).getRGB();

        return -1;
    }

    public int getHullColor(GeoBone bone, MultiToolItem animatable)
    {
        int color = animatable.getHullColor(getCurrentItemStack());
        if(color != 0)
            return new Color(color, false).getRGB();

        return -1;
    }

    public RenderType getRenderType(GeoBone bone, MultiToolItem animatable)
    {
        return PortalRenderTypes.APERTURE_GLOW.apply(getTexture(bone, animatable), RenderStateShard.TRANSLUCENT_TRANSPARENCY);
    }
}
