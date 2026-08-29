package net.mistersecret312.aperture_innovations.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ColoredGlowingLayer<T extends GeoAnimatable> extends AutoGlowingGeoLayer<T>
{
	private final BiFunction<T, GeoBone, ResourceLocation> textureFunction;
	private final BiFunction<T, GeoBone, Integer> colorFunction;
	private final BiFunction<T, GeoBone, RenderType> typeFunction;
	public ColoredGlowingLayer(GeoRenderer<T> renderer,
							   BiFunction<T, GeoBone, ResourceLocation> texture,
							   BiFunction<T, GeoBone, Integer> color,
							   BiFunction<T, GeoBone, RenderType> type)
	{
		super(renderer);
		this.textureFunction = texture;
		this.colorFunction = color;
		this.typeFunction = type;
	}

	@Override
	public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType,
					   MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick,
					   int packedLight, int packedOverlay)
	{

		for(GeoBone bone : bakedModel.topLevelBones())
			renderColoredCube(animatable, poseStack, bone, bufferSource, packedLight, packedOverlay);

	}

	public void renderColoredCube(T animatable, PoseStack poseStack,
								  GeoBone bone, MultiBufferSource bufferSource,
								  int packedLight, int packedOverlay)
	{
		poseStack.pushPose();

		RenderUtil.prepMatrixForBone(poseStack, bone);

		if(textureFunction.apply(animatable, bone) != null)
			getRenderer().renderCubesOfBone(poseStack, bone,
					bufferSource.getBuffer(typeFunction.apply(animatable, bone)), packedLight,
					packedOverlay, colorFunction.apply(animatable, bone));

		for(GeoBone childBone : bone.getChildBones())
		{
			renderColoredCube(animatable, poseStack, childBone, bufferSource,
					packedLight, packedOverlay);
		}

		poseStack.popPose();
	}
}
