package net.mistersecret312.aperture_innovations.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.client.model.CubeItemModel;
import net.mistersecret312.aperture_innovations.client.renderer.ColoredGlowingLayer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariant;
import net.mistersecret312.aperture_innovations.items.CubeItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.specialty.DynamicGeoItemRenderer;

import java.awt.*;

public class CubeItemRenderer extends DynamicGeoItemRenderer<CubeItem>
{

    public CubeItemRenderer()
    {
        super(new CubeItemModel());

        this.addRenderLayer(new ColoredGlowingLayer<>(this,
                (cubeItem, bone) -> this.getTexture(bone, cubeItem),
                (cubeItem, bone) -> this.getColor(bone, cubeItem),
                (cubeItem, bone) -> this.getRenderType(bone, cubeItem)));
        this.addRenderLayer(new ColoredGlowingLayer<>(this,
                (cubeItem, bone) -> getHullTexture(bone, cubeItem),
                (cubeItem, bone) -> getHullColor(bone, cubeItem),
                (cubeItem, bone) -> RenderType.entityTranslucent(getHullTexture(bone, cubeItem))
        ));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, CubeItem animatable, BakedGeoModel model,
                               @Nullable RenderType renderType, MultiBufferSource bufferSource,
                               @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                               int packedOverlay, int colour)
    {

    }

    public ResourceLocation getTexture(GeoBone bone, CubeItem animatable)
    {
        int color = animatable.getColor(getCurrentItemStack());
        ClientCubeVariant cubeVariant = animatable.getCubeVariant(getCurrentItemStack());
        if(!bone.getName().contains("ColoredCircle"))
            return null;

        ResourceLocation texture = cubeVariant.idleTexture().orElse(null);
        if(color != 0)
            texture = cubeVariant.genericTexture().orElse(null);

        return texture;
    }

    public ResourceLocation getHullTexture(GeoBone bone, CubeItem animatable)
    {
        return animatable.getCubeVariant(getCurrentItemStack()).hullTexture();
    }

    public int getColor(GeoBone bone, CubeItem animatable)
    {
        int color = animatable.getColor(getCurrentItemStack());
        if(color != 0)
            return new Color(color, false).getRGB();

        return -1;
    }

    public int getHullColor(GeoBone bone, CubeItem animatable)
    {
        int color = animatable.getHullColor(getCurrentItemStack());
        if(color != 0)
            return new Color(color, false).getRGB();

        return -1;
    }

    public RenderType getRenderType(GeoBone bone, CubeItem animatable)
    {
        return PortalRenderTypes.APERTURE_GLOW.apply(getTexture(bone, animatable), RenderStateShard.TRANSLUCENT_TRANSPARENCY);
    }
}
