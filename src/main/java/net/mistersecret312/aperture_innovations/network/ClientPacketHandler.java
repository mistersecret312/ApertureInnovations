package net.mistersecret312.aperture_innovations.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.block_entities.AntlineBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.AntlineOutputBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.VitalApparatusVentBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.multiblock.MasterBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.multiblock.DummyBlock;
import net.mistersecret312.aperture_innovations.capabilities.ApertureCapability;
import net.mistersecret312.aperture_innovations.client.renderer.PortalRenderer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariants;
import net.mistersecret312.aperture_innovations.client.screen.MultiToolScreen;
import net.mistersecret312.aperture_innovations.client.screen.renderers.BlockEntityPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.EntityPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.ItemPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.PreviewRenderer;
import net.mistersecret312.aperture_innovations.init.AttachmentTypeInit;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.items.MultiToolItem;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;
import net.mistersecret312.aperture_innovations.multitool.IItemConfiguration;
import net.mistersecret312.aperture_innovations.utilities.ClientAntlineUtilities;
import net.mistersecret312.aperture_innovations.utilities.ClientPortalUtilities;
import net.mistersecret312.aperture_innovations.data.portal.Portal;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;

import java.util.UUID;

public class ClientPacketHandler
{
	public static void syncPortalData(UUID linkID, boolean isPrimary, Portal portal, ResourceLocation variant)
	{
		ClientPortalLink link = PortalRenderer.LINKS.getOrDefault(linkID, new ClientPortalLink());
		link.variantKey = variant;
		link.linkID = linkID;
		if(isPrimary)
		{
			if(portal.getPosition() != null && !portal.getPosition().equals(link.getPrimaryPortal().getPosition()))
			{
				portal.setReplaceShapes(
						PortalUtilities.calculatePortalVoxels(Minecraft.getInstance().level, portal.getPosition(),
								portal.getXRotation(), portal.getYRotation()));
				ClientPortalUtilities.setPortalOpeningAnimationProgress(0F, linkID, true);
			}
			link.setPrimaryPortal(portal);
		} else
		{
			if(portal.getPosition() != null && !portal.getPosition().equals(link.getPrimaryPortal().getPosition()))
			{
				portal.setReplaceShapes(
						PortalUtilities.calculatePortalVoxels(Minecraft.getInstance().level, portal.getPosition(),
								portal.getXRotation(), portal.getYRotation()));
				ClientPortalUtilities.setPortalOpeningAnimationProgress(0F, linkID, false);
			}
			link.setSecondaryPortal(portal);
		}
		PortalRenderer.LINKS.put(linkID, link);
	}

	public static void handleTeleportMomentumPacket(Vector3f speed)
	{
		Player player = Minecraft.getInstance().player;
		if(player != null)
		{
			player.setDeltaMovement(new Vec3(speed));
		}
	}

	public static void handleAntlineUpdate(BlockPos pos, boolean active, int color, int activeColor)
	{
		BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
		if(blockEntity != null)
		{
			if(blockEntity instanceof AntlineBlockEntity antlineBlockEntity)
			{
				antlineBlockEntity.updateConnections();
				antlineBlockEntity.trimConnections();
				antlineBlockEntity.active = active;

				antlineBlockEntity.color = color;
				antlineBlockEntity.activeColor = activeColor;

				ClientAntlineUtilities.setActive(antlineBlockEntity.getNetworkID(), active);

				antlineBlockEntity.setChanged();
			}
		}
	}

	public static void handleAntlineOutputUpdate(BlockPos pos, int color, int activeColor)
	{
		BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
		if(blockEntity != null)
		{
			if(blockEntity instanceof AntlineOutputBlockEntity output)
			{
				output.color = color;
				output.activeColor = activeColor;

				output.setChanged();
			}
		}
	}

	public static void handleEntityPortalLerp(int id, Vector3f pos, Vector3f delta, float xRot, float yRot)
	{
		Entity entity = Minecraft.getInstance().level.getEntity(id);
		if(entity != null)
		{
			entity.setPos(pos.x, pos.y, pos.z);

			entity.xOld = pos.x;
			entity.xo = pos.x;
			entity.xRotO = xRot;

			entity.yOld = pos.y;
			entity.yo = pos.y;
			entity.yRotO = yRot;

			entity.zOld = pos.z;
			entity.zo = pos.z;

			entity.setOldPosAndRot();
			entity.lerpTo(pos.x, pos.y, pos.z, yRot, xRot, 0);
			entity.setDeltaMovement(new Vec3(delta));
		}
	}

	public static void handlePlayerCapabilityPacket(int frictionlessTime)
	{
		Player player = Minecraft.getInstance().player;
		if(player == null) return;

		ApertureCapability aperture = player.getData(AttachmentTypeInit.APERTURE);
		aperture.frictionlessTime = frictionlessTime;
		player.setData(AttachmentTypeInit.APERTURE, aperture);

		if(frictionlessTime != 0 && !player.onGround())
		{
			player.setDiscardFriction(true);
		} else
		{
			player.setDiscardFriction(false);
		}
	}

	public static void handleEntityHeldUpdate(int id, boolean held)
	{
		Entity entity = Minecraft.getInstance().level.getEntity(id);
		if(entity != null)
		{
			entity.getData(AttachmentTypeInit.HOLD_ENTITY.get()).setHeld(entity, held);
		}
	}

	public static void fizzleParticles(int id)
	{
		Level level = Minecraft.getInstance().level;
		Entity entity = level.getEntity(id);
		if(entity != null)
		{
			AABB box = entity.getBoundingBox();
			double width = box.getXsize();
			double height = box.getYsize();
			double length = box.getZsize();

			double volume = width * height * length;
			for (int i = 0; i < Math.min(Math.max(Math.round(volume*5), 1), 1000); i++)
			{
				double xOffset = entity.getRandom().nextGaussian() * (width / 3);
				double yOffset = .2 + (entity.getRandom().nextGaussian() * (height / 3));
				double zOffset = entity.getRandom().nextGaussian() * (length / 3);
				double velocityX = entity.getRandom().nextGaussian();
				double velocityY = entity.getRandom().nextGaussian();
				double velocityZ = entity.getRandom().nextGaussian();
				level.addParticle(ParticleTypes.ELECTRIC_SPARK, entity.getX() + xOffset, entity.getY() + yOffset, entity.getZ() + zOffset, velocityX, velocityY, velocityZ);
			}

			for (int i = 0; i < 3; i++)
			{
				double xOffset = entity.getRandom().nextGaussian() * (width / 3.8);
				double yOffset = .2 + (entity.getRandom().nextGaussian() * (height / 3.8));
				double zOffset = entity.getRandom().nextGaussian() * (length / 3.8);
				level.addParticle(ParticleTypes.SMOKE, entity.getX() + xOffset, entity.getY() + yOffset, entity.getZ() + zOffset, 0, -0.3, 0);
			}
		}
	}

	public static void openMultiToolItemScreen(boolean toolMain, boolean targetMain)
	{
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		InteractionHand toolHand = toolMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		InteractionHand targetHand = targetMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

		ItemStack toolStack = player.getItemInHand(toolHand);
		ItemStack targetStack = player.getItemInHand(targetHand);

		if (toolStack.getItem() instanceof MultiToolItem item) {
			ClientMultiToolVariant variant = ClientMultiToolVariants.getMultiToolVariant(item.getVariantKey(toolStack));
			PreviewRenderer renderer = new ItemPreviewRenderer(targetStack, targetHand);
			MultiToolScreen screen = new MultiToolScreen(targetStack.getHoverName(),
					targetStack.getItem() instanceof IItemConfiguration config ? config : null,
					renderer, variant, item.getHullColor(toolStack), item.getGlowColor(toolStack));
			mc.setScreen(screen);
		}
	}

	public static void openMultiToolBlockScreen(BlockPos pos, boolean toolMain)
	{
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		Level level = mc.level;
		if (player == null || level == null)
			return;

		BlockState state = level.getBlockState(pos);
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if(state.getBlock() instanceof DummyBlock dummyBlock)
		{
			MasterBlockEntity master = dummyBlock.getMaster(level, pos);
			if(master != null)
			{
				blockEntity = master;
				state = master.getBlockState();
			}
		}

		ItemStack toolStack = player.getItemInHand(toolMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
		if (toolStack.getItem() instanceof MultiToolItem item)
		{
			ClientMultiToolVariant variant = ClientMultiToolVariants.getMultiToolVariant(item.getVariantKey(toolStack));
			PreviewRenderer renderer = new BlockEntityPreviewRenderer(state, blockEntity, level.registryAccess());
			MultiToolScreen screen = new MultiToolScreen(state.getBlock().getName(),
					blockEntity instanceof IHaveConfiguration configuration ? configuration : null,
					renderer, variant, item.getHullColor(toolStack), item.getGlowColor(toolStack));
			mc.setScreen(screen);
		}
	}

	public static void openMultiToolEntityScreen(int id, boolean toolMain)
	{
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		Level level = mc.level;
		if (player == null || level == null)
			return;

		Entity entity = level.getEntity(id);
		if (entity == null) return;

		ItemStack toolStack = player.getItemInHand(toolMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
		if (toolStack.getItem() instanceof MultiToolItem item)
		{
			ClientMultiToolVariant variant = ClientMultiToolVariants.getMultiToolVariant(item.getVariantKey(toolStack));
			PreviewRenderer renderer = new EntityPreviewRenderer(level, entity);
			MultiToolScreen screen = new MultiToolScreen(entity.getName(),
					entity instanceof IHaveConfiguration config ? config : null, renderer,
					variant, item.getHullColor(toolStack), item.getGlowColor(toolStack));
			mc.setScreen(screen);
		}
	}
}
