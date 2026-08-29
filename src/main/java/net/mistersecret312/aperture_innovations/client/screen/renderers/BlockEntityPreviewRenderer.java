package net.mistersecret312.aperture_innovations.client.screen.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.block_entities.VitalApparatusVentBlockEntity;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.HashMap;

public class BlockEntityPreviewRenderer implements PreviewRenderer
{
	public final BlockState state;
	public BlockEntity blockEntity = null;

	public BlockEntityPreviewRenderer(BlockState state, BlockEntity blockEntity,
									  HolderLookup.Provider provider)
	{
		this.state = state;
		if(blockEntity != null)
		{
			CompoundTag entireTag = blockEntity.saveWithoutMetadata(provider);
			this.blockEntity = blockEntity.getType().create(blockEntity.getBlockPos(), state);
			if(this.blockEntity != null)
			{
				this.blockEntity.loadWithComponents(entireTag, provider);
				this.blockEntity.setLevel(Minecraft.getInstance().level);
			}
		}
	}

	@Override
	public void render(GuiGraphics graphics, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		poseStack.pushPose();
		poseStack.scale(30, -30, 30);
		float rotation = Minecraft.getInstance().levelRenderer.getTicks();
		poseStack.mulPose(Axis.XP.rotationDegrees(30));
		poseStack.mulPose(Axis.YP.rotationDegrees(rotation % 360));

		AABB bounds = new AABB(0, 0, 0, 1, 1, 1);
		if (Minecraft.getInstance().level != null)
		{
			VoxelShape shape = state.getShape(Minecraft.getInstance().level, BlockPos.ZERO);
			if (!shape.isEmpty())
				bounds = shape.bounds();
		}

		double centerX = bounds.minX + (bounds.maxX - bounds.minX) / 2.0;
		double centerY = bounds.minY + (bounds.maxY - bounds.minY) / 2.0;
		double centerZ = bounds.minZ + (bounds.maxZ - bounds.minZ) / 2.0;

		poseStack.translate(-centerX, -centerY, -centerZ);

		if(!state.getRenderShape().equals(RenderShape.INVISIBLE))
		{
			Minecraft.getInstance().getBlockRenderer()
					 .renderSingleBlock(state, poseStack, graphics.bufferSource(), LightTexture.FULL_BRIGHT,
							 OverlayTexture.NO_OVERLAY, net.neoforged.neoforge.client.model.data.ModelData.EMPTY, null);

			if(this.blockEntity != null)
				Minecraft.getInstance().getBlockEntityRenderDispatcher().render(this.blockEntity, partialTick, poseStack, graphics.bufferSource());
		}

		graphics.bufferSource().endBatch();
		poseStack.popPose();
	}

	@Override
	public void applyFakeState(HashMap<String, Object> map)
	{
		if(this.blockEntity == null)
			return;
		if(!(this.blockEntity instanceof IHaveConfiguration configurable))
			return;

		for(ConfigurationProperty<?> property : configurable.getConfigurationProperties(Minecraft.getInstance().level.registryAccess()))
		{
			Object value = map.get(property.getName());
			if(value != null)
			{
				property.setUnsafe(value);
			}
		}

		this.blockEntity.getBlockState();
	}
}
