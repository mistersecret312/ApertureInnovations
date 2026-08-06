package net.mistersecret312.aperture_innovations.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.client.ColorUtil;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariants;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.utilities.ClientPortalUtilities;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.specialty.DynamicGeoItemRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.awt.*;
import java.util.List;
import java.util.function.BiFunction;

public class PortalGunRenderer extends DynamicGeoItemRenderer<PortalGunItem> {
    public PortalGunRenderer() {
        super(new DefaultedItemGeoModel<>(ApertureInnovations.of( "portal_gun")));
    }

    @Override
    protected boolean boneRenderOverride(PoseStack poseStack, GeoBone bone, MultiBufferSource bufferSource,
                                         VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay,
                                         int colour) {
        List<String> gunCore = Lists.newArrayList("CoreOuter", "CoreInner", "PortalLight", "Muzzle");
        if (gunCore.contains(bone.getName())) {
            int portal = this.getAnimatable().getLastShotPortal(this.currentItemStack);
            ClientPortalLink link = PortalUtilities.getPortalLinks().get(this.getAnimatable().getUUID(this.currentItemStack, false));

            if (portal == -1 || link == null)
                return super.boneRenderOverride(poseStack, bone, bufferSource, buffer,
                        partialTick, packedLight, packedOverlay, colour);

            ColorUtil.RGBA color = ClientPortalUtilities.getPortalColor(link, portal == 0);
            Color originalColor = new Color(colour, true);

            float red = (originalColor.getRed() * color.red()) / 255F;
            float green = (originalColor.getGreen() * color.green()) / 255F;
            float blue = (originalColor.getBlue() * color.blue()) / 255F;
            float alpha = (originalColor.getAlpha() * color.alpha()) / 255F;

            Color resultColor = new Color(red, green, blue, alpha);

            renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, resultColor.getRGB());
            return true;
        }

        if (bone.getName().equals("StripePrimary") || bone.getName().equals("StripeSecondary")) {
            boolean isPrimary = bone.getName().equals("StripePrimary");
            int stripeColor = isPrimary ? this.getAnimatable().getPrimaryStripeColor(this.currentItemStack) :
                    this.getAnimatable().getSecondaryStripeColor(this.currentItemStack);
            if (stripeColor == -1) {
                ClientPortalLink link = PortalUtilities.getPortalLinks().get(this.getAnimatable().getUUID(this.currentItemStack, false));
                ClientPortalGunVariant variant = ClientPortalGunVariant.DEFAULT_VARIANT;

                if(animatable.getVariant(currentItemStack) != null)
                    variant = ClientPortalGunVariants.getPortalGunVariant(animatable.getVariant(currentItemStack));

                if (link != null)
                    variant = link.getVariant();

                if (variant == null) {
                    return true;
                }

                ColorUtil.RGBA color = isPrimary ? variant.primaryStripeColor() : variant.secondaryStripeColor();

                if (color.red() == 1F && color.green() == 1F && color.blue() == 1F && color.alpha() == 1F)
                    return true;

                Color originalColor = new Color(colour, true);

                float red = (originalColor.getRed() * color.red()) / 255F;
                float green = (originalColor.getGreen() * color.green()) / 255F;
                float blue = (originalColor.getBlue() * color.blue()) / 255F;
                float alpha = (originalColor.getAlpha() * color.alpha()) / 255F;

                Color resultColor = new Color(red, green, blue, alpha);

                renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, resultColor.getRGB());
            } else {
                Color color = new Color(stripeColor, false);
                if (color.getRGB() == -1)
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

        return super.boneRenderOverride(poseStack, bone, bufferSource, buffer, partialTick, packedLight, packedOverlay,
                colour);
    }

    @Override
    protected @Nullable RenderType getRenderTypeOverrideForBone(GeoBone bone, PortalGunItem animatable,
                                                                ResourceLocation texturePath,
                                                                MultiBufferSource bufferSource, float partialTick) {
        List<String> gunCore = Lists.newArrayList("CoreOuter", "CoreInner", "PortalLight", "Muzzle");
        if(bone.getName().equals("Zap"))
            return RenderType.EYES.apply(getTextureOverrideForBone(bone, animatable, partialTick), RenderStateShard.TRANSLUCENT_TRANSPARENCY);
        if (gunCore.contains(bone.getName())) {
            return PortalRenderTypes.APERTURE_GLOW.apply(getTextureOverrideForBone(bone, animatable, partialTick), RenderStateShard.TRANSLUCENT_TRANSPARENCY);
        }
        return super.getRenderTypeOverrideForBone(bone, animatable, texturePath, bufferSource, partialTick);
    }

    @Override
    protected @Nullable ResourceLocation getTextureOverrideForBone(GeoBone bone, PortalGunItem animatable,
                                                                   float partialTick) {
        int portal = this.getAnimatable().getLastShotPortal(this.currentItemStack);
        ClientPortalLink link = PortalUtilities.getPortalLinks().get(this.getAnimatable().getUUID(this.currentItemStack, false));

        if(bone.getName().equals("Zap") && animatable.getZapTick(currentItemStack) != -1)
        {
			return ApertureInnovations.of("textures/portal_gun/zap/portal_gun_zap_" + animatable.getZapTick(this.currentItemStack) + ".png");
        }

        if (link != null) {
            List<String> gunCore = Lists.newArrayList("CoreOuter", "CoreInner", "PortalLight", "Muzzle");
            if (gunCore.contains(bone.getName()))
                return ClientPortalUtilities.getPortalGunCoreTexture(link, portal);
            else return ClientPortalUtilities.getPortalGunTexture(link);
        }

        return ApertureInnovations.of( "textures/item/portal_gun.png");
    }

    @Override
    public RenderType getRenderType(PortalGunItem animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, PortalGunItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
        RenderUtil.translateMatrixToBone(poseStack, bone);
        RenderUtil.translateToPivotPoint(poseStack, bone);
        RenderUtil.rotateMatrixAroundBone(poseStack, bone);
        RenderUtil.scaleMatrixForBone(poseStack, bone);

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());

            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.itemRenderTranslations));
        }

        RenderUtil.translateAwayFromPivotPoint(poseStack, bone);

        this.textureOverride = getTextureOverrideForBone(bone, this.animatable, partialTick);
        ResourceLocation texture = this.textureOverride == null ? getTextureLocation(this.animatable) : this.textureOverride;
        RenderType renderTypeOverride = getRenderTypeOverrideForBone(bone, this.animatable, texture, bufferSource, partialTick);

        if (texture != null && renderTypeOverride == null)
            renderTypeOverride = getRenderType(this.animatable, texture, bufferSource, partialTick);

        if (renderTypeOverride != null)
            buffer = bufferSource.getBuffer(renderTypeOverride);

        if (!boneRenderOverride(poseStack, bone, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour))
            super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);

        if (renderTypeOverride != null)
            buffer = bufferSource.getBuffer(renderTypeOverride);

        if (!isReRender)
            applyRenderLayersForBone(poseStack, animatable, bone, renderTypeOverride, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        buffer = checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderTypeOverride);

        super.renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        poseStack.popPose();
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
