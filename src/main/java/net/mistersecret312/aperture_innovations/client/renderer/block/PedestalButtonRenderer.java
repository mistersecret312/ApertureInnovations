package net.mistersecret312.aperture_innovations.client.renderer.block;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.LargeButtonBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.PedestalButtonBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.PedestalButtonBlock;
import net.mistersecret312.aperture_innovations.blocks.multiblock.OrientedMasterBlock;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.client.model.PedestalButtonModel;
import net.mistersecret312.aperture_innovations.client.renderer.ColoredGlowingLayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.specialty.DynamicGeoBlockRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.awt.*;
import java.util.List;

import static net.mistersecret312.aperture_innovations.client.renderer.item.PortalGunRenderer.GLOWING_RENDER_TYPE;

public class PedestalButtonRenderer extends DynamicGeoBlockRenderer<PedestalButtonBlockEntity>
{
	public PedestalButtonRenderer(BlockEntityRendererProvider.Context context)
	{
		super(new PedestalButtonModel());

		this.addRenderLayer(new ColoredGlowingLayer<>(this,
				(button, bone) -> getButtonTexture(bone, button),
				(button, bone) -> getButtonColor(bone, button),
				(button, bone) -> getButtonRenderType(bone, button)
		));

		this.addRenderLayer(new ColoredGlowingLayer<>(this,
				(button, bone) -> getLinesTexture(bone, button),
				(button, bone) -> getLinesColor(bone, button),
				(button, bone) -> getLinesRenderType(bone, button)
		));

		this.addRenderLayer(new ColoredGlowingLayer<>(this,
				(button, bone) -> getHullTexture(bone, button),
				(button, bone) -> getHullColor(bone, button),
				(button, bone) -> getHullRenderType(bone, button)
		));
	}

	@Override
	public void actuallyRender(PoseStack poseStack, PedestalButtonBlockEntity animatable, BakedGeoModel model,
							   @Nullable RenderType renderType, MultiBufferSource bufferSource,
							   @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
							   int packedOverlay, int colour)
	{
		super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
				packedLight, packedOverlay, colour);
	}

	@Override
	public void preRender(PoseStack poseStack, PedestalButtonBlockEntity animatable, BakedGeoModel model,
						  @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
						  float partialTick, int packedLight, int packedOverlay, int colour)
	{
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight,
				packedOverlay, colour);

		Direction facing = animatable.getBlockState().getValue(PedestalButtonBlock.FACING);
		Direction normal = animatable.getBlockState().getValue(PedestalButtonBlock.NORMAL);
		boolean positive = normal.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);

		if(normal.getAxis().isVertical())
		{
			if(!positive)
			{
				poseStack.translate(0f, 1f, 0f);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180));
				if(facing.getAxis().equals(Direction.Axis.X))
					poseStack.mulPose(Axis.YP.rotationDegrees(180));
			}
		}
		if(normal.getAxis().isHorizontal())
		{
			if(normal.getAxis().equals(Direction.Axis.X))
			{
				poseStack.translate(positive ? -0.5f : 0.5f, 0.5f, 0f);

				poseStack.mulPose(Axis.ZP.rotationDegrees(normal.toYRot()));
				poseStack.mulPose(Axis.YP.rotationDegrees(positive ? 180 : 0));
			}
			if(normal.getAxis().equals(Direction.Axis.Z))
			{
				poseStack.translate(0f, 0.5f, positive ? -0.5f : 0.5f);
				poseStack.mulPose(Axis.ZP.rotationDegrees(90));
				poseStack.mulPose(Axis.XP.rotationDegrees(positive ? 90 : -90));
			}

			if(facing.getAxis().isVertical())
			{
				boolean posFace = facing.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
				poseStack.mulPose(Axis.YP.rotationDegrees(posFace ? 0 :180));
			}
		}

		poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getBlockState().getValue(PedestalButtonBlock.FACING).toYRot()));
		if(facing.getAxis().equals(Direction.Axis.X))
			poseStack.mulPose(Axis.YP.rotationDegrees(180));
	}

	public ResourceLocation getHullTexture(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		return ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
				"textures/block/pedestal_button/pedestal_button.png");
	}

	public ResourceLocation getButtonTexture(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		if(!bone.getName().contains("Button"))
			return null;

		int color = animatable.getButtonColor().packagedInt();
		if(color != 0)
			return animatable.getClientVariant().genericButtonTexture().orElse(null);

		return animatable.getClientVariant().buttonTexture().orElse(null);
	}

	public ResourceLocation getLinesTexture(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		if(!bone.getName().contains("ColoredLines"))
			return null;

		int color = animatable.getLinesColor().packagedInt();
		if(color != 0)
			return animatable.getClientVariant().genericLinesTexture().orElse(null);

		return animatable.getClientVariant().linesTexture().orElse(null);
	}

	public int getHullColor(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		int color = animatable.getHullColor().packagedInt();
		if(color != 0)
			return new Color(color, false).getRGB();

		return -1;
	}

	public int getButtonColor(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		int color = animatable.getButtonColor().packagedInt();
		if(color != 0)
			return new Color(color, false).getRGB();

		return -1;
	}

	public int getLinesColor(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		int color = animatable.getLinesColor().packagedInt();
		if(color != 0)
			return new Color(color, false).getRGB();

		return -1;
	}

	public RenderType getHullRenderType(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		return RenderType.entityTranslucent(getHullTexture(bone, animatable));
	}

	public RenderType getButtonRenderType(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		return GLOWING_RENDER_TYPE.apply(getButtonTexture(bone, animatable), false);
	}

	public RenderType getLinesRenderType(GeoBone bone, PedestalButtonBlockEntity animatable)
	{
		return PortalRenderTypes.APERTURE_GLOW.apply(getLinesTexture(bone, animatable),
				RenderStateShard.TRANSLUCENT_TRANSPARENCY);
	}

	@Override
	public AABB getRenderBoundingBox(PedestalButtonBlockEntity blockEntity)
	{
		if(blockEntity.getLevel() != null && blockEntity.getBlockState().getBlock() instanceof OrientedMasterBlock master)
			return master.getFullShape(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState()).bounds().move(blockEntity.getBlockPos());

		return new AABB(0, 0, 0, 0, 0, 0);
	}

	@Override
	protected void rotateBlock(Direction facing, PoseStack poseStack)
	{

	}
}
