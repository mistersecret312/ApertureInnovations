package net.mistersecret312.aperture_innovations.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.impl.SableCompanionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.blocks.multiblock.MasterBlock;
import net.mistersecret312.aperture_innovations.client.ColorUtil;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.items.MultiBlockItem;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.mistersecret312.aperture_innovations.network.ServerboundOpenPortalPacket;
import net.mistersecret312.aperture_innovations.network.ServerboundPickUpEntityPacket;
import net.mistersecret312.aperture_innovations.network.ServerboundResetPortalLinkPacket;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.utilities.ClientPortalUtilities;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import net.mistersecret312.aperture_innovations.sounds.PortalSoundWrapper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.*;

import java.util.List;
import java.awt.*;
import java.util.Map;
import java.util.UUID;

import static net.mistersecret312.aperture_innovations.client.renderer.PortalRenderer.*;

@EventBusSubscriber(modid = ApertureInnovations.MODID, value = Dist.CLIENT)
public class ClientEvents
{
	@SubscribeEvent
	public static void renderPortals(RenderLevelStageEvent event) {
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		Camera camera = event.getCamera();
		PoseStack poseStack = event.getPoseStack();

		if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES)
		{
			for(Map.Entry<UUID, ClientPortalLink> linkEntry : LINKS.entrySet())
			{
				ClientPortalLink link = linkEntry.getValue();

				poseStack.pushPose();

				ResourceLocation texturePrimary = ClientPortalUtilities.getPortalVortexTexture(link, true);
				ResourceLocation textureSecondary = ClientPortalUtilities.getPortalVortexTexture(link, false);

				final TextureAtlasSprite primary = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
															.apply(texturePrimary);
				final TextureAtlasSprite secondary = Minecraft.getInstance()
															  .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
															  .apply(textureSecondary);

				for(int i = 0; i < 2; i++)
				{
					boolean isPrimary = i == 0;
					ResourceKey<Level> dimension = isPrimary ? link.getPrimaryPortal().getDimension() : link.getSecondaryPortal().getDimension();
					if(Minecraft.getInstance().level.dimension() != dimension)
						continue;
					if(Minecraft.getInstance().level.dimension() != dimension)
						continue;

					poseStack.pushPose();

					float scale = ClientPortalUtilities.getPortalOpeningAnimationProgress(link.linkID(), isPrimary);

					Vec3 portalPos = isPrimary ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();

					if(portalPos != null)
					{
						ClientSubLevelAccess access = SableCompanion.INSTANCE.getContainingClient(portalPos);
						if(access == null)
							poseStack.translate(-camera.getPosition().x + portalPos.x,
									-camera.getPosition().y + portalPos.y,
									-camera.getPosition().z + portalPos.z);

						if(access != null)
						{
							portalPos = access.renderPose().transformPosition(portalPos);
							poseStack.translate(-camera.getPosition().x + portalPos.x,
									-camera.getPosition().y + portalPos.y,
									-camera.getPosition().z + portalPos.z);

							Quaternionf rotation = access.renderPose().orientation().get(new Quaternionf());
							Vector3f angles = rotation.getEulerAnglesYXZ(new Vector3f());
							poseStack.mulPose(Axis.YP.rotation(angles.y));
							poseStack.mulPose(Axis.XP.rotation(angles.x));
							poseStack.mulPose(Axis.ZP.rotation(angles.z));
						}
					}

					if(portalPos != null
							   && Minecraft.getInstance().level.isLoaded(BlockPos.containing(portalPos))
							   && event.getLevelRenderer().getFrustum().isVisible(new AABB(portalPos, portalPos).inflate(1)))
					{
						if(isPrimary)
							primaryRender(link, portalPos, buffer, poseStack, camera, scale);
						else
							secondaryRender(link, portalPos, buffer, poseStack, camera, scale);
					}

					if(isPrimary && link.getPrimaryPortal().isInWorld())
						renderPortalVortex(link, portalPos, camera, primary, buffer, poseStack, true);
					else if(link.getSecondaryPortal().isInWorld())
						renderPortalVortex(link, portalPos, camera, secondary, buffer, poseStack, false);

					poseStack.popPose();
				}

				poseStack.popPose();
			}
		}
	}

	@SubscribeEvent
	public static void renderBlockOverlay(RenderBlockScreenEffectEvent event)
	{
		Player player = event.getPlayer();
		Level level = player.level();

		if(event.getOverlayType().equals(RenderBlockScreenEffectEvent.OverlayType.BLOCK))
		{
			Pair<UUID, Boolean> portal = PortalUtilities.getClosestPortal(player);
			UUID uuid = portal.getFirst();
			boolean isPrimary = portal.getSecond();
			if(uuid == null)
				return;

			Vec3 portalPos = PortalUtilities.getPortalPos(level, uuid, isPrimary);
			Vec2 rotation = PortalUtilities.getPortalRotation(level, uuid, isPrimary);

			AABB portalBox = PortalUtilities.getPortalBoundingBox(portalPos, rotation.x, rotation.y);
			if(portalBox.contains(player.getEyePosition()))
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void renderMultiBlockBox(RenderHighlightEvent.Block event)
	{
		Level level = Minecraft.getInstance().level;
		Player player = Minecraft.getInstance().player;
		PoseStack poseStack = event.getPoseStack();
		MultiBufferSource bufferSource = event.getMultiBufferSource();
		Camera camera = event.getCamera();

		if(player == null || level == null)
			return;

		ItemStack mainHandStack = player.getMainHandItem();
		ItemStack offHandStack = player.getOffhandItem();

		ItemStack itemStack = ItemStack.EMPTY;
		InteractionHand hand = InteractionHand.MAIN_HAND;
		if(offHandStack.getItem() instanceof MultiBlockItem)
			itemStack = offHandStack;

		if(mainHandStack.getItem() instanceof MultiBlockItem)
		{
			itemStack = mainHandStack;
			hand = InteractionHand.OFF_HAND;
		}
		if(!(itemStack.getItem() instanceof MultiBlockItem item))
			return;

		if(!(item.getBlock() instanceof MasterBlock master))
			return;

		BlockState state = master.getStateForPlacement(new BlockPlaceContext(player, hand, itemStack, event.getTarget()));
		VoxelShape shape = master.getFullShape(level, event.getTarget().getBlockPos(), state);
		AABB box = shape.bounds();
		BlockPos pos = event.getTarget().getBlockPos().relative(event.getTarget().getDirection());
		poseStack.pushPose();
		poseStack.translate(-camera.getPosition().x,
				-camera.getPosition().y, -camera.getPosition().z);
		poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

		LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), box,
				0f, 200f/255f, 1f, 1f);

		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(-camera.getPosition().x,
				-camera.getPosition().y, -camera.getPosition().z);

		BlockPos.betweenClosedStream(box.move(pos.getX(), pos.getY(), pos.getZ())).filter(position ->
			{
				BlockState otherState = level.getBlockState(position);
				if(otherState.isAir())
					return false;

				return !otherState.canBeReplaced();
			}).forEach(position ->
			{
				poseStack.pushPose();
				poseStack.translate(position.getX(), position.getY(), position.getZ());
				LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()),
						new AABB(BlockPos.ZERO), 1f, 0f, 0f, 1f);
				poseStack.popPose();
			});

		poseStack.popPose();
	}

	@SubscribeEvent
	public static void mouseClicks(InputEvent.MouseButton.Pre event)
	{
		Level level = Minecraft.getInstance().level;
		Player player = Minecraft.getInstance().player;
		if(level == null || player == null || Minecraft.getInstance().screen != null)
			return;

		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		boolean hasPortalGun = main.is(ItemInit.PORTAL_GUN.get()) || off.is(ItemInit.PORTAL_GUN.get());
		if (!hasPortalGun)
			return;

		ItemStack gunItemStack = main.is(ItemInit.PORTAL_GUN.get()) ? main : off;
		PortalGunItem gunItem = (PortalGunItem) gunItemStack.getItem();

		int dualityState = gunItem.getDualityState(gunItemStack);
		UUID uuid = gunItem.getUUID(gunItemStack, false);

		if(!player.isShiftKeyDown() && event.getButton() == 0 &&
				   event.getAction() == 1 && (dualityState == 2 || dualityState == 0))
		{
			PacketDistributor.sendToServer(new ServerboundOpenPortalPacket(true));
			event.setCanceled(true);
		}
		else if(!player.isShiftKeyDown() && event.getButton() == 1 &&
						event.getAction() == 1 && (dualityState == 2 || dualityState == 1))
		{
			PacketDistributor.sendToServer(new ServerboundOpenPortalPacket(false));
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent.Pre event)
	{
		Minecraft mc = Minecraft.getInstance();
		if(mc.level != null && mc.player != null)
		{
			LINKS.forEach((linkID, link) ->
				{
					for(int i = 0; i < 2; i++)
					{
						boolean isPrimary = i == 0;
						Vec3 portalPos = isPrimary ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();

						if(portalPos != null)
						{
							float progress = ClientPortalUtilities.getPortalOpeningAnimationProgress(linkID, isPrimary);
							if(progress < 1F)
							{
								progress += 0.25f;
								ClientPortalUtilities.setPortalOpeningAnimationProgress(progress, linkID, isPrimary);
							}
						}

						if(!link.isOpen())
						{
							PortalSoundWrapper.PortalAmbient ambient = ClientPortalUtilities.getAmbientSound(linkID,
									isPrimary);
							if(ambient != null) ambient.stopSound();
						}
					}
				});
		}
		if(mc.player == null)
			return;

		while(ApertureInnovations.ClientModEvents.RESET_PORTAL_GUN.get().consumeClick())
			PacketDistributor.sendToServer(new ServerboundResetPortalLinkPacket());

		while(ApertureInnovations.ClientModEvents.PICK_UP.get().consumeClick())
			PacketDistributor.sendToServer(new ServerboundPickUpEntityPacket());

		while(ApertureInnovations.ClientModEvents.PRIMARY_FIRE.get().consumeClick() && !mc.player.isShiftKeyDown())
			PacketDistributor.sendToServer(new ServerboundOpenPortalPacket(true));

		while(ApertureInnovations.ClientModEvents.SECONDARY_FIRE.get().consumeClick() && !mc.player.isShiftKeyDown())
			PacketDistributor.sendToServer(new ServerboundOpenPortalPacket(false));
	}

}
