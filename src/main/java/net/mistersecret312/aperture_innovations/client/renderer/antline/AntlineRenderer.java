package net.mistersecret312.aperture_innovations.client.renderer.antline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.AntlineBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.AntlineBlock;
import net.mistersecret312.aperture_innovations.blocks.enums.ConnectionState;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.utilities.ClientAntlineUtilities;
import net.neoforged.neoforge.client.model.data.ModelData;

public class AntlineRenderer implements BlockEntityRenderer<AntlineBlockEntity>
{
	public AntlineRenderer(BlockEntityRendererProvider.Context context)
	{

	}

	@Override
	public void render(AntlineBlockEntity blockEntity, float partialTick, PoseStack poseStack,
						   MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{
		if(blockEntity.getFakeState() != null && blockEntity.getLevel() != null)
		{
			poseStack.pushPose();
			BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockEntity.getFakeState());
			for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(blockEntity.getFakeState(), RandomSource.create(42), ModelData.EMPTY))
			{
				Minecraft.getInstance().getBlockRenderer().renderBatched(blockEntity.getFakeState(),
						blockEntity.getBlockPos(), blockEntity.getLevel(), poseStack, bufferSource.getBuffer(rt),
						true, blockEntity.getLevel().random, ModelData.EMPTY, rt);
			}

			poseStack.popPose();

			if(Minecraft.getInstance().player != null && blockEntity.getBlockState().getDestroyProgress(Minecraft.getInstance().player, blockEntity.getLevel(), blockEntity.getBlockPos()) > 0.2)
				return;
		}

		String activity = blockEntity.active ? "active" : "inactive";
		int color = ClientAntlineUtilities.isActive(blockEntity.getNetworkID()) ? blockEntity.activeColor : blockEntity.color;

		ResourceLocation relayTexture;
		ResourceLocation connectionTexture;
		if(color == -1)
		{
			relayTexture = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"textures/antline/antline_relay_" + activity + ".png");
			connectionTexture = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"textures/antline/antline_connection_" + activity + ".png");
		}
		else
		{
			relayTexture = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"textures/antline/antline_relay_generic.png");
			connectionTexture = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"textures/antline/antline_connection_generic.png");
		}


		poseStack.pushPose();

		poseStack.mulPose(Axis.XN.rotationDegrees(90));
		poseStack.translate(0.5f, -0.5f, 0.01f);
		poseStack.scale(0.25f, 0.25f, 0.25f);

		Direction normal = blockEntity.getNormal();
		Direction.Axis axis = normal.getAxis();
		boolean positive = normal.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
		if(axis.equals(Direction.Axis.X))
		{
			poseStack.mulPose(Axis.YP.rotationDegrees(positive ? 90 : -90));
			poseStack.translate(positive ? -2f : 2f, 0f, -1.95f);
		}

		if(axis.equals(Direction.Axis.Y))
		{
			poseStack.mulPose(Axis.XP.rotationDegrees(positive ? 0 : 180));
			poseStack.translate(0f, 0f, positive ? 0 : -3.95f);
		}

		if(axis.equals(Direction.Axis.Z))
		{
			poseStack.mulPose(Axis.XN.rotationDegrees(positive ? -90 : 90));
			poseStack.translate(0f, positive ? 2f : -2f, -1.95f);
		}

		ResourceLocation texture = relayTexture;

		int axises = blockEntity.getConnectedSides().stream().map(Direction::getAxis).distinct().toList().size();
		if(axises > 1)
			texture = connectionTexture;

		renderDot(bufferSource, poseStack, texture, color);

		for(Direction direction : Direction.values())
		{
			if(direction.getAxis().equals(blockEntity.getNormal().getAxis()))
				continue;

			if(blockEntity.getState(direction).equals(ConnectionState.NONE))
				continue;

			poseStack.pushPose();

			poseStack.translate(axis.equals(Direction.Axis.X) ? (positive ? -1f : 1f) * 1.33f*direction.getNormal().getY() : 0F,
					axis.equals(Direction.Axis.Z) ? (positive ? 1f : -1f) * 1.33f*direction.getNormal().getY() : 0F, 0F);

			if(axis.equals(Direction.Axis.X) && !positive)
				poseStack.mulPose(Axis.ZP.rotationDegrees(180));

			poseStack.translate(1.33f*direction.getNormal().getX(), (positive ? -1f : 1f) * 1.33f*direction.getNormal().getZ(), 0);
			renderDot(bufferSource, poseStack, blockEntity.getState(direction).equals(ConnectionState.LINK) ? connectionTexture : relayTexture, color);

			if(blockEntity.getState(direction) == ConnectionState.UP
					   || blockEntity.getState(direction) == ConnectionState.SIDE_UP)
			{
				Level level = blockEntity.getLevel();
				if(level != null)
				{
					BlockState state = level.getBlockState(blockEntity.getBlockPos().relative(normal));
					if(blockEntity.getState(direction) == ConnectionState.SIDE_UP)
						state = level.getBlockState(blockEntity.getBlockPos().relative(direction).relative(normal));

					if(!(state.getBlock() instanceof AntlineBlock))
					{
						poseStack.popPose();
						continue;
					}
				}

				if(direction.getAxis().isVertical())
					poseStack.translate(3*1.33f, 0f, 0f);

				poseStack.translate(3*1.33f*direction.getNormal().getX(), 3*(positive ? -1f : 1f) * 1.33f*direction.getNormal().getZ(), 0);

				for(int i = 0; i < 3; i++)
				{
					poseStack.translate(1.33f*direction.getNormal().getX(), (positive ? -1f : 1f) * 1.33f*direction.getNormal().getZ(), 0);

					poseStack.pushPose();
					if(direction.getAxis().isVertical())
					{
						boolean dirPos = normal.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
						if(normal.getAxis().equals(Direction.Axis.X))
						{
							if(direction.equals(Direction.UP))
							{
								poseStack.translate(-3.95f, 0f, 4f);
								poseStack.mulPose(Axis.YP.rotationDegrees(180));
							}
							if(direction.equals(Direction.DOWN))
							{
								poseStack.translate(-4f, 0f, 0f);
							}

							poseStack.translate(0.66f, 0f, 0.66f + i * 1.33f);
							poseStack.mulPose(Axis.YP.rotationDegrees(-90));
						}
						if(normal.getAxis().equals(Direction.Axis.Z))
						{
							if(direction.equals(Direction.UP))
							{
								poseStack.translate(0f, dirPos ? -4.05f : 4.05f, 4f);
								poseStack.mulPose(Axis.XP.rotationDegrees(180));
							}
							if(direction.equals(Direction.DOWN))
							{
								poseStack.translate(0f, dirPos ? 4f : -4f, 0f);
							}
							poseStack.translate(-4f, dirPos ? -4.66f : 4.66f, 0f);

							poseStack.translate(0f, 0f, 0.66f + i * 1.33f);
							poseStack.mulPose(Axis.XP.rotationDegrees(positive ? -90 : 90));
						}
					}

					if(direction.getAxis().isHorizontal())
					{
						boolean dirPos = direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
						poseStack.translate(-1.33f*direction.getNormal().getX(), -(positive ? -1f : 1f) * 1.33f*direction.getNormal().getZ(), 0);

						if(direction.getAxis().equals(Direction.Axis.X))
						{
							poseStack.translate(dirPos ? -4f : 4f, 0f, 1.33f);
							poseStack.mulPose(Axis.YP.rotationDegrees(dirPos ? -90 : 90));
							poseStack.translate(i * (dirPos ? 1.33f : -1.33f) + (dirPos ? -0.66f : 0.66f), 0f,
									i * 1.33f - 0.66f);
						}
						if(direction.getAxis().equals(Direction.Axis.Z))
						{
							poseStack.translate(0f, dirPos ? 4f : -4f, 1.33f);
							poseStack.mulPose(Axis.XP.rotationDegrees(dirPos ? -90 : 90));
							poseStack.translate(0f, i * (dirPos ? -1.33f : 1.33f) + (dirPos ? 0.66f : -0.66f),
									i * 1.33f - 0.66f);
						}

						if(normal.equals(Direction.WEST))
						{
							poseStack.translate(0f, 0f, -2*i*1.33f-6.7f);
							poseStack.mulPose(Axis.XP.rotationDegrees(180));
						}
					}

					renderDot(bufferSource, poseStack, relayTexture, color);
					poseStack.popPose();
				}
			}

			poseStack.popPose();
		}

		poseStack.popPose();
	}

	public void renderDot(MultiBufferSource bufferSource, PoseStack poseStack, ResourceLocation texture, int color)
	{
		VertexConsumer consumer = bufferSource.getBuffer(PortalRenderTypes.antline(texture));
		color = FastColor.ARGB32.color(255, color);
		consumer.addVertex(poseStack.last().pose(), -0.5f, -0.5f, 0).setUv(0, 1).setColor(color);
		consumer.addVertex(poseStack.last().pose(), 0.5f, -0.5f, 0).setUv(1, 1).setColor(color);
		consumer.addVertex(poseStack.last().pose(), 0.5f, 0.5f, 0).setUv(1, 0).setColor(color);
		consumer.addVertex(poseStack.last().pose(), -0.5f, 0.5f, 0).setUv(0, 0).setColor(color);
	}
}
