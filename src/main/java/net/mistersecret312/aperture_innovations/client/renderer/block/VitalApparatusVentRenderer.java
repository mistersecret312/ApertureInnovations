package net.mistersecret312.aperture_innovations.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.VitalApparatusVentBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.multiblock.MasterBlock;
import net.mistersecret312.aperture_innovations.blocks.multiblock.OrientedMasterBlock;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.client.model.VitalApparatusVentModel;
import net.mistersecret312.aperture_innovations.client.renderer.ColoredGlowingLayer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.specialty.DynamicGeoBlockRenderer;

import java.awt.*;

public class VitalApparatusVentRenderer extends DynamicGeoBlockRenderer<VitalApparatusVentBlockEntity>
{
	public VitalApparatusVentRenderer(BlockEntityRendererProvider.Context context)
	{
		super(new VitalApparatusVentModel());
		this.addRenderLayer(new ColoredGlowingLayer<>(this,
				(vent, bone) -> getGlowTexture(bone, vent),
				(vent, bone) -> getGlowColor(bone, vent),
				(vent, bone) -> getGlowRenderType(bone, vent)
		));
		this.addRenderLayer(new ColoredGlowingLayer<>(this,
				(vent, bone) -> getHullTexture(bone, vent),
				(vent, bone) -> getHullColor(bone, vent),
				(vent, bone) -> RenderType.entityTranslucent(getHullTexture(bone, vent))
		));
		this.addRenderLayer(new ColoredGlowingLayer<>(this,
				(vent, bone) -> ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/vital_apparatus_vent/glass.png"),
				(vent, bone) -> getHullColor(bone, vent),
				(vent, bone) -> RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/entity/vital_apparatus_vent/glass.png"))
		));
	}

	@Override
	public void actuallyRender(PoseStack poseStack, VitalApparatusVentBlockEntity animatable, BakedGeoModel model,
							   @Nullable RenderType renderType, MultiBufferSource bufferSource,
							   @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
							   int packedOverlay, int colour)
	{
		super.actuallyRender(poseStack, animatable, model, renderType, bufferSource,
				buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
	}

	@Override
	public @Nullable RenderType getRenderType(VitalApparatusVentBlockEntity animatable, ResourceLocation texture,
											  @Nullable MultiBufferSource bufferSource, float partialTick)
	{
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void preRender(PoseStack poseStack, VitalApparatusVentBlockEntity vent, BakedGeoModel model,
						  @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
						  float partialTick, int packedLight, int packedOverlay, int colour)
	{
		super.preRender(poseStack, vent, model, bufferSource, buffer, isReRender, partialTick, packedLight,
				packedOverlay, colour);
		Direction down = vent.getBlockState().getValue(OrientedMasterBlock.NORMAL).getOpposite();
		poseStack.mulPose(down.getRotation());

		if(down.equals(Direction.UP))
			poseStack.translate(0, -1.125, 0);
		else if(down.equals(Direction.DOWN))
			poseStack.translate(0, -2.125, 0);
		else
			poseStack.translate(0, -1.625, -0.5);

		Minecraft mc = Minecraft.getInstance();
		Level level = vent.getLevel();
		if(level == null)
			return;

		Entity entity = vent.getTrackingType().create(level);
		if(entity == null)
			return;

		if(!vent.getTrackingData().isEmpty())
			entity.load(vent.getTrackingData());

		if(bufferSource != null)
		{
			poseStack.pushPose();

			float y = Mth.lerp(vent.getEmptyTime() / 16f, 1f, -0.25f);

			poseStack.translate(0, y, 0);

			mc.getEntityRenderDispatcher()
			  .render(entity, 0, 0, 0, 0, partialTick, poseStack, bufferSource, packedLight);

			poseStack.popPose();
		}
	}

	@Override
	protected void rotateBlock(Direction facing, PoseStack poseStack)
	{

	}

	public ResourceLocation getGlowTexture(GeoBone bone, VitalApparatusVentBlockEntity animatable)
	{
		if(!bone.getName().equals("GlowingStuff"))
			return null;

		if(animatable.getIdleColor().packagedInt() != 0)
		{
			return animatable.getClientVariant().glowGeneric();
		}
		return animatable.isOpen() ? animatable.getClientVariant().activeTexture().orElse(null) : animatable.getClientVariant().inactiveTexture().orElse(null);
	}

	public ResourceLocation getHullTexture(GeoBone bone, VitalApparatusVentBlockEntity animatable)
	{
		if(animatable.getHullColor().packagedInt() != 0)
		{
			return animatable.getClientVariant().hullGeneric();
		}

		return animatable.getClientVariant().hullTexture();
	}

	public int getGlowColor(GeoBone bone, VitalApparatusVentBlockEntity animatable)
	{
		if(animatable.getIdleColor().packagedInt() != 0 && !animatable.isOpen())
			return new Color(animatable.getIdleColor().packagedInt(), false).getRGB();
		if(animatable.getActiveColor().packagedInt() != 0 && animatable.isOpen())
			return new Color(animatable.getActiveColor().packagedInt(), false).getRGB();

		return -1;
	}

	public int getHullColor(GeoBone bone, VitalApparatusVentBlockEntity animatable)
	{
		if(animatable.getHullColor().packagedInt() != 0)
			return new Color(animatable.getHullColor().packagedInt(), false).getRGB();

		return -1;
	}

	public RenderType getGlowRenderType(GeoBone bone, VitalApparatusVentBlockEntity animatable)
	{
		return PortalRenderTypes.APERTURE_GLOW.apply(getGlowTexture(bone, animatable), RenderStateShard.TRANSLUCENT_TRANSPARENCY);
	}

	@Override
	public AABB getRenderBoundingBox(VitalApparatusVentBlockEntity blockEntity)
	{
		if(blockEntity.getBlockState().getBlock() instanceof MasterBlock masterBlock)
			return masterBlock.getFullShape(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState()).bounds().move(blockEntity.getBlockPos());

		return super.getRenderBoundingBox(blockEntity);
	}
}
