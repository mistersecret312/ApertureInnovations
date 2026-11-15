package net.mistersecret312.aperture_innovations.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import net.mistersecret312.aperture_innovations.client.Layers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class LongFallBootsModel extends HumanoidModel<LivingEntity>
{
	public static LongFallBootsModel INSTANCE;

	public LongFallBootsModel(ModelPart root)
	{
		super(root);
	}

	public static LayerDefinition createBodyLayer()
	{
		CubeDeformation cubeDeformation = CubeDeformation.NONE;
		float yOffset = 0.0F;

		MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, yOffset);
		PartDefinition root = meshdefinition.getRoot();
		PartDefinition right_leg = root.getChild("right_leg");
		PartDefinition left_leg = root.getChild("left_leg");

		right_leg.addOrReplaceChild("right_boot", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.0F, -10.5F, -1.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
																.texOffs(16, 0).mirror().addBox(-1.0F, -9.5F, 3.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)).mirror(false)
																.texOffs(22, 0).mirror().addBox(-1.0F, -6.5F, 3.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
																.texOffs(0, 12).mirror().addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.251F)).mirror(false), PartPose.offset(0F, 12.0F, -1.0F));

		left_leg.addOrReplaceChild("left_boot", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -10.5F, -1.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F))
															  .texOffs(16, 0).addBox(-1.0F, -9.5F, 3.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.2F))
															  .texOffs(22, 0).addBox(-1.0F, -6.5F, 3.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
															  .texOffs(0, 12).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.251F)), PartPose.offset(0F, 12.0F, -1.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	protected Iterable<ModelPart> bodyParts()
	{
		return ImmutableList.of(this.leftLeg, this.rightLeg);
	}

	@SubscribeEvent
	public static void bakeModelLayers(EntityRenderersEvent.AddLayers event)
	{
		EntityModelSet entityModelSet = event.getEntityModels();
		INSTANCE = new LongFallBootsModel(entityModelSet.bakeLayer(Layers.LONG_FALL_BOOTS));
	}
}
