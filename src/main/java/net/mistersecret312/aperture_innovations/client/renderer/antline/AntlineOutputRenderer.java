package net.mistersecret312.aperture_innovations.client.renderer.antline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.AntlineOutputBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.AntlineOutputBlock;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;

public class AntlineOutputRenderer implements BlockEntityRenderer<AntlineOutputBlockEntity>
{
	public AntlineOutputRenderer(BlockEntityRendererProvider.Context context)
	{

	}

	@Override
	public void render(AntlineOutputBlockEntity blockEntity, float partialTick, PoseStack poseStack,
						   MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{
		String activity = blockEntity.getBlockState().getValue(AntlineOutputBlock.ACTIVE) ? "on" : "off";
		int color = blockEntity.getBlockState().getValue(AntlineOutputBlock.ACTIVE) ? blockEntity.activeColor : blockEntity.color;

		ResourceLocation backTexture;
		ResourceLocation activityTexture = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
				"textures/antline/checkmark/tick_" + activity + ".png");
		if(color == -1)
		{
			backTexture = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"textures/antline/checkmark/tick_" + activity + "_background.png");
		}
		else
		{
			backTexture = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"textures/antline/checkmark/tick_" + activity + "_background_generic.png");
		}

		poseStack.pushPose();

		poseStack.mulPose(Axis.XN.rotationDegrees(90));
		poseStack.translate(0.5f, -0.5f, 0.01f);
		poseStack.scale(0.5f, 0.5f, 0.5f);

		Direction normal = blockEntity.getNormal();
		Direction.Axis axis = normal.getAxis();
		boolean normalPos = normal.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);

		Direction facing = blockEntity.getFacing();
		Direction.Axis facingAxis = facing.getAxis();
		boolean facingPos = facing.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);

		if(axis.equals(Direction.Axis.X))
		{
			poseStack.mulPose(Axis.YP.rotationDegrees(normalPos ? 90 : -90));
		}

		if(axis.equals(Direction.Axis.Y))
		{
			poseStack.mulPose(Axis.XP.rotationDegrees(normalPos ? 0 : 180));
		}

		if(axis.equals(Direction.Axis.Z))
		{
			poseStack.mulPose(Axis.XN.rotationDegrees(normalPos ? -90 : 90));
		}

		poseStack.mulPose(Axis.ZP.rotationDegrees(facing.toYRot()));
		if(facingAxis.isHorizontal() && axis.isVertical())
		{
			if(facingAxis.equals(Direction.Axis.Z) && normalPos)
				poseStack.mulPose(Axis.ZP.rotationDegrees(facingPos ? 180 : -180));
		}
		if(axis.isHorizontal())
		{
			poseStack.mulPose(Axis.ZP.rotationDegrees(90));
			if(axis.equals(Direction.Axis.X))
				poseStack.mulPose(Axis.ZP.rotationDegrees(normalPos ? 90 : -90));
			if(axis.equals(Direction.Axis.Z))
				poseStack.mulPose(Axis.ZP.rotationDegrees(normalPos ? 0 : 180));
		}
		poseStack.translate(0f, axis.isVertical() ? 0f : 1f,
				axis.isVertical() ?
						normalPos ? 0f : -1.95f
						: -0.95f);

		renderTexture(bufferSource, poseStack, backTexture, color);
		poseStack.translate(0f, 0f, 0.01f);
		renderTexture(bufferSource, poseStack, activityTexture, -1);

		poseStack.popPose();
	}

	public void renderTexture(MultiBufferSource bufferSource, PoseStack poseStack, ResourceLocation texture, int color)
	{
		VertexConsumer consumer = bufferSource.getBuffer(PortalRenderTypes.antline(texture));
		color = FastColor.ARGB32.color(255, color);
		consumer.addVertex(poseStack.last().pose(), -0.5f, -0.5f, 0).setUv(0, 1).setColor(color);
		consumer.addVertex(poseStack.last().pose(), 0.5f, -0.5f, 0).setUv(1, 1).setColor(color);
		consumer.addVertex(poseStack.last().pose(), 0.5f, 0.5f, 0).setUv(1, 0).setColor(color);
		consumer.addVertex(poseStack.last().pose(), -0.5f, 0.5f, 0).setUv(0, 0).setColor(color);
	}
}
