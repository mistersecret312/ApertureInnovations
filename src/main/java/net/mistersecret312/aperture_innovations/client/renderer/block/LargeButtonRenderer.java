package net.mistersecret312.aperture_innovations.client.renderer.block;

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
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.LargeButtonBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.LargeButtonBlock;
import net.mistersecret312.aperture_innovations.blocks.multiblock.OrientedMasterBlock;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.client.model.LargeButtonModel;
import net.mistersecret312.aperture_innovations.client.renderer.ColoredGlowingLayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.specialty.DynamicGeoBlockRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.awt.*;

import static net.mistersecret312.aperture_innovations.client.renderer.item.PortalGunRenderer.GLOWING_RENDER_TYPE;

public class LargeButtonRenderer extends DynamicGeoBlockRenderer<LargeButtonBlockEntity>
{
	public LargeButtonRenderer(BlockEntityRendererProvider.Context context)
	{
		super(new LargeButtonModel());
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
	public void actuallyRender(PoseStack poseStack, LargeButtonBlockEntity animatable, BakedGeoModel model,
							   @Nullable RenderType renderType, MultiBufferSource bufferSource,
							   @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
							   int packedOverlay, int colour)
	{
		super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer,
				isReRender, partialTick, packedLight, packedOverlay, colour);
	}

	public ResourceLocation getButtonTexture(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		int color = animatable.getButtonColor().packagedInt();
		if(color != 0)
			return animatable.getClientVariant().genericButtonTexture().orElse(null);

		return animatable.getClientVariant().buttonTexture().orElse(null);
	}

	public ResourceLocation getLinesTexture(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		int color = animatable.getColor().packagedInt();
		if(color != 0)
			return animatable.getClientVariant().genericLinesTexture();

		boolean active = animatable.getBlockState().getValue(LargeButtonBlock.PRESSED);
		if(active)
			return animatable.getClientVariant().activeLinesTexture();

		return animatable.getClientVariant().inactiveLinesTexture();
	}

	public ResourceLocation getHullTexture(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		return animatable.getClientVariant().hullTexture();
	}

	public int getButtonColor(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		int color = animatable.getButtonColor().packagedInt();
		if(color != 0)
			return new Color(color, false).getRGB();

		return -1;
	}

	public int getLinesColor(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		boolean active = animatable.getBlockState().getValue(LargeButtonBlock.PRESSED);
		int color = active ? animatable.getActiveColor().packagedInt() : animatable.getColor().packagedInt();

		if(color != 0)
			return new Color(color, false).getRGB();

		return -1;
	}

	public int getHullColor(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		int color = animatable.getHullColor().packagedInt();
		if(color != 0)
			return new Color(color, false).getRGB();

		return -1;
	}

	public RenderType getButtonRenderType(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		return GLOWING_RENDER_TYPE.apply(getButtonTexture(bone, animatable), false);
	}

	public RenderType getLinesRenderType(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		return PortalRenderTypes.APERTURE_GLOW.apply(getLinesTexture(bone, animatable),
				RenderStateShard.TRANSLUCENT_TRANSPARENCY);
	}

	public RenderType getHullRenderType(GeoBone bone, LargeButtonBlockEntity animatable)
	{
		return RenderType.entityTranslucent(getHullTexture(bone, animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, LargeButtonBlockEntity animatable, BakedGeoModel model,
						  @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
						  float partialTick, int packedLight, int packedOverlay, int colour)
	{
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight,
				packedOverlay, colour);

		Direction facing = animatable.getBlockState().getValue(LargeButtonBlock.FACING);
		Direction normal = animatable.getBlockState().getValue(LargeButtonBlock.NORMAL);

		poseStack.mulPose(normal.getRotation());

		if(normal.equals(Direction.UP))
		{
			if(facing.equals(Direction.NORTH))
				poseStack.translate(1, 0, -1);
			if(facing.equals(Direction.EAST))
				poseStack.translate(1, 0, 0);
			if(facing.equals(Direction.WEST))
				poseStack.translate(0, 0, -1);
		}
		else poseStack.translate(0, -0.5, 0);

		if(normal.equals(Direction.DOWN))
			poseStack.translate(1, -1, -1);
		if(normal.equals(Direction.NORTH))
			poseStack.translate(0, 0, -1.5f);
		if(normal.equals(Direction.WEST))
			poseStack.translate(1f, 0, -1.5f);
		if(normal.equals(Direction.SOUTH))
			poseStack.translate(1f, 0f, -1.5f);
		if(normal.equals(Direction.EAST))
			poseStack.translate(0, 0, -1.5f);

	}

	@Override
	public AABB getRenderBoundingBox(LargeButtonBlockEntity blockEntity)
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
