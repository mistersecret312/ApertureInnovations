package net.mistersecret312.aperture_innovations.client.renderer.item;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.client.ColorUtil;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.client.model.PortalGunItemModel;
import net.mistersecret312.aperture_innovations.client.renderer.ColoredGlowingLayer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariants;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.utilities.ClientPortalUtilities;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.specialty.DynamicGeoItemRenderer;

import java.awt.*;
import java.util.List;
import java.util.function.BiFunction;

public class PortalGunRenderer extends DynamicGeoItemRenderer<PortalGunItem>
{
    public PortalGunRenderer()
    {
        super(new PortalGunItemModel());
        //Hull
        this.addRenderLayer(new ColoredGlowingLayer<>(this,
                this::getHullTexture, this::getHullColor, this::getHullRenderType));

        //Core Glow
        this.addRenderLayer(new ColoredGlowingLayer<>(this,
                this::getCoreTexture, this::getCoreColor, this::getCoreRenderType));

        //Zap
        this.addRenderLayer(new ColoredGlowingLayer<>(this,
                this::getZapTexture, this::getZapColor, this::getZapRenderType));
    }

    @Override
    protected boolean boneRenderOverride(PoseStack poseStack, GeoBone bone, MultiBufferSource bufferSource,
                                         VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay,
                                         int colour)
    {
        if(bone.getName().equals("StripePrimary") || bone.getName().equals("StripeSecondary"))
        {
            boolean isPrimary = bone.getName().equals("StripePrimary");
            int stripeColor = isPrimary ? this.getAnimatable()
                                              .getPrimaryStripeColor(this.currentItemStack) : this.getAnimatable()
                                                                                                  .getSecondaryStripeColor(
                                                                                                          this.currentItemStack);
            Color rgbStripeColor = new Color(stripeColor, false);
            if(rgbStripeColor.getRGB() == -1 || rgbStripeColor.getRGB() == 16777215)
            {
                ClientPortalLink link = PortalUtilities.getPortalLinks()
                                                       .get(this.getAnimatable().getUUID(this.currentItemStack, false));
                ClientPortalGunVariant variant = ClientPortalGunVariant.DEFAULT_VARIANT;
                if(link != null && link.getVariant() != null)
                    variant = link.getVariant();

                if(animatable.getVariant(currentItemStack) != null)
                    variant = ClientPortalGunVariants.getPortalGunVariant(animatable.getVariant(currentItemStack));

                ColorUtil.RGBA color = isPrimary ? variant.primaryStripeColor() : variant.secondaryStripeColor();

//                if(color.red() == 1F && color.green() == 1F && color.blue() == 1F && color.alpha() == 1F)
//                    return true;

                Color originalColor = new Color(colour, true);

                float red = (originalColor.getRed() * color.red()) / 255F;
                float green = (originalColor.getGreen() * color.green()) / 255F;
                float blue = (originalColor.getBlue() * color.blue()) / 255F;
                float alpha = (originalColor.getAlpha() * color.alpha()) / 255F;

                Color resultColor = new Color(red, green, blue, alpha);

                renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, resultColor.getRGB());
            } else
            {
                Color color = new Color(stripeColor, false);
                if(color.getRGB() == -1)
                    return true;

                Color originalColor = new Color(colour, true);

                float red = (originalColor.getRed() * color.getRed()) / 255F;
                float green = (originalColor.getGreen() * color.getGreen()) / 255F;
                float blue = (originalColor.getBlue() * color.getBlue()) / 255F;
                float alpha = (originalColor.getAlpha() * color.getAlpha()) / 255F;

                Color resultColor = new Color(red / 255F, green / 255F, blue / 255F, alpha / 255F);

                renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, resultColor.getRGB());
            }
            return true;
        }

        return true;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, PortalGunItem animatable, BakedGeoModel model,
                               @Nullable RenderType renderType, MultiBufferSource bufferSource,
                               @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                               int packedOverlay, int colour)
    {
        if(this.model instanceof PortalGunItemModel itemModel)
            itemModel.stack = this.getCurrentItemStack();

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, colour);
    }

    @Override
    public RenderType getRenderType(PortalGunItem animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entityTranslucent(texture);
    }

    public ResourceLocation getHullTexture(PortalGunItem animatable, GeoBone bone)
    {
        List<String> stripeBones = Lists.newArrayList("StripePrimary", "StripeSecondary");
        List<String> gunCore = Lists.newArrayList("CoreOuter", "CoreInner", "PortalLight", "Muzzle");;
        if(gunCore.contains(bone.getName()) || bone.getName().equals("Zap") ||
                   stripeBones.contains(bone.getName()))
            return null;

        ClientPortalGunVariant variant = animatable.getGunVariant(this.currentItemStack);
        if(variant == null)
            variant = ClientPortalGunVariant.DEFAULT_VARIANT;
        return variant.texture();
    }

    public ResourceLocation getCoreTexture(PortalGunItem animatable, GeoBone bone)
    {
        List<String> gunCore = Lists.newArrayList("CoreOuter", "CoreInner", "PortalLight", "Muzzle");
        ClientPortalGunVariant variant = animatable.getGunVariant(this.currentItemStack);
        if(variant == null)
            variant = ClientPortalGunVariant.DEFAULT_VARIANT;
        int lastPortal = animatable.getLastShotPortal(this.currentItemStack);
        if(!gunCore.contains(bone.getName()) || bone.getName().equals("Zap"))
            return null;

        if(lastPortal == -1)
            return variant.idleCoreTexture();
        int portalColor = lastPortal == 0 ?
                                  animatable.getPrimaryPortalColor(this.currentItemStack) : animatable.getSecondaryPortalColor(this.currentItemStack);
        Color color = new Color(portalColor, false);
        if(color.getRGB() != -1)
            return variant.genericPortal().getCoreTexture();

        if(lastPortal == 0)
            return variant.primaryPortal().getCoreTexture();
        if(lastPortal == 1)
            return variant.secondaryPortal().getCoreTexture();

        return variant.idleCoreTexture();
    }

    public ResourceLocation getZapTexture(PortalGunItem animatable, GeoBone bone)
    {
        if(animatable.getZapTick(this.currentItemStack) == -1)
            return null;

        return ApertureInnovations.of(
                "textures/portal_gun/zap/portal_gun_zap_" + animatable.getZapTick(this.currentItemStack) + ".png");
    }

    public int getHullColor(PortalGunItem animatable, GeoBone bone)
    {
        Color color = new Color(animatable.getHullColor(this.currentItemStack), false);
        return color.getRGB();
    }

    public int getCoreColor(PortalGunItem animatable, GeoBone bone)
    {
        int lastPortal = animatable.getLastShotPortal(this.currentItemStack);
        int portalColor = lastPortal == 0 ? animatable.getPrimaryPortalColor(this.currentItemStack) : animatable.getSecondaryPortalColor(this.currentItemStack);
        Color color = new Color(portalColor, false);
        if(lastPortal == -1)
            return -1;

        return color.getRGB();
    }

    public int getZapColor(PortalGunItem animatable, GeoBone bone)
    {
        return -1;
    }

    public RenderType getHullRenderType(PortalGunItem animatable, GeoBone bone)
    {
        return RenderType.entityTranslucent(getHullTexture(animatable, bone));
    }

    public RenderType getCoreRenderType(PortalGunItem animatable, GeoBone bone)
    {
        return PortalRenderTypes.APERTURE_GLOW.apply(getCoreTexture(animatable, bone), RenderStateShard.TRANSLUCENT_TRANSPARENCY);
    }

    public RenderType getZapRenderType(PortalGunItem animatable, GeoBone bone)
    {
        return RenderType.EYES.apply(getZapTexture(animatable, bone), RenderStateShard.TRANSLUCENT_TRANSPARENCY);
    }

    private static final RenderStateShard.ShaderStateShard SHADER_STATE = new RenderStateShard.ShaderStateShard(
            GameRenderer::getRendertypeEntityTranslucentEmissiveShader);
    private static final RenderStateShard.TransparencyStateShard TRANSPARENCY_STATE = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    private static final RenderStateShard.WriteMaskStateShard WRITE_MASK = new RenderStateShard.WriteMaskStateShard(true, true);
    public static final BiFunction<ResourceLocation, Boolean, RenderType> GLOWING_RENDER_TYPE = Util.memoize((texture, isGlowing) -> {
        RenderStateShard.TextureStateShard textureState = new RenderStateShard.TextureStateShard(texture, false, false);

        return RenderType.create("geo_glowing_layer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(SHADER_STATE)
                        .setTextureState(textureState)
                        .setTransparencyState(TRANSPARENCY_STATE)
                        .setOverlayState(new RenderStateShard.OverlayStateShard(true))
                        .setWriteMaskState(WRITE_MASK).createCompositeState(isGlowing));
    });
}
