package net.mistersecret312.aperture_innovations.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.client.ColorUtil;
import net.mistersecret312.aperture_innovations.client.PortalRenderTypes;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.utilities.ClientPortalUtilities;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.UUID;


public class PortalRenderer
{
	public static HashMap<UUID, ClientPortalLink> LINKS = new HashMap<>();

	public static void primaryRender(ClientPortalLink link, Vec3 pos, MultiBufferSource.BufferSource buffer, PoseStack poseStack, Camera camera, float scale) {
		if (link.getPrimaryPortal().isInWorld())
		{
			poseStack.pushPose();
			if(link.isOpen())
			{
				renderPortalNonSee(buffer, pos, poseStack, camera, link, true, scale);
			}

			float xRot = link.getPrimaryPortal().getXRotation();
			float yRot = link.getPrimaryPortal().getYRotation();

			Direction direction = Direction.fromYRot(yRot);
			if(xRot == -90)
				direction = Direction.UP;
			if(xRot == 90)
				direction = Direction.DOWN;

			poseStack.mulPose(Axis.YP.rotationDegrees(yRot + ((direction.getAxis().equals(
					Direction.Axis.X) && direction.getAxis().isHorizontal()) ? 180 : 0)));
			poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

			poseStack.translate(0f, 0f, 0f);

			poseStack.translate(0f, 0f, 0.001f);
			poseStack.scale(2f, 2f, 2f);

			poseStack.scale(scale, scale, scale);
			poseStack.translate(0.25f, 0f, 0f);

			renderPortalFrame(ClientPortalUtilities.getPortalClosedTexture(link, true),
					ClientPortalUtilities.getPortalColor(link, true), buffer, poseStack);

			poseStack.popPose();
		}
	}

	public static void secondaryRender(ClientPortalLink link, Vec3 pos, MultiBufferSource.BufferSource buffer, PoseStack poseStack, Camera camera, float scale) {
		if (link.getSecondaryPortal().isInWorld())
		{
			poseStack.pushPose();
			if(link.isOpen())
			{
				renderPortalNonSee(buffer, pos, poseStack, camera, link, false, scale);
			}

			float xRot = link.getSecondaryPortal().getXRotation();
			float yRot = link.getSecondaryPortal().getYRotation();

			Direction direction = Direction.fromYRot(yRot);
			if(xRot == -90)
				direction = Direction.UP;
			if(xRot == 90)
				direction = Direction.DOWN;

			poseStack.mulPose(Axis.YP.rotationDegrees(yRot + ((direction.getAxis().equals(
					Direction.Axis.X) && direction.getAxis().isHorizontal()) ? 180 : 0)));
			poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

			poseStack.translate(0f, 0f, 0.001f);
			poseStack.scale(2f, 2f, 2f);

			poseStack.scale(scale, scale, scale);
			poseStack.translate(0.25f, 0f, 0f);

			renderPortalFrame(ClientPortalUtilities.getPortalClosedTexture(link, false), ClientPortalUtilities.getPortalColor(link, false),
					buffer, poseStack);

			poseStack.popPose();
		}
	}

	public static void renderPortalNonSee(MultiBufferSource buffer, Vec3 pos, PoseStack poseStack, Camera camera, ClientPortalLink link, boolean isPrimary, float scale)
	{
		poseStack.pushPose();

		float xRot = isPrimary ? link.getPrimaryPortal().getXRotation() : link.getSecondaryPortal().getXRotation();
		float yRot = isPrimary ? link.getPrimaryPortal().getYRotation() : link.getSecondaryPortal().getYRotation();

		Direction direction = Direction.fromYRot(yRot);
		if(direction.getAxis().equals(Direction.Axis.X))
			yRot += 180;

		poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
		poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

		poseStack.scale(1f, 2f, 1f);

		poseStack.translate(0,0f,0.01f);
		poseStack.scale(scale, scale, scale);

		if(ApertureInnovations.isIrisLoaded())
		{
			VertexConsumer consumerB = buffer.getBuffer(PortalRenderTypes.portalEndMask());
			consumerB.addVertex(poseStack.last().pose(), -0.5f, -0.5f, 0);
			consumerB.addVertex(poseStack.last().pose(), 0.5f, -0.5f, 0);
			consumerB.addVertex(poseStack.last().pose(), 0.5f, 0.5f, 0);
			consumerB.addVertex(poseStack.last().pose(), -0.5f, 0.5f, 0);
		}

		VertexConsumer consumerA = buffer.getBuffer(PortalRenderTypes.portal(isPrimary ?
												 link.getVariant().primaryPortal().getMaskTexture() :
												 link.getVariant().secondaryPortal().getMaskTexture()));

		consumerA.addVertex(poseStack.last().pose(), -0.5f, -0.5f, 0)
				 .setUv(0, 1)
				 .setColor(FastColor.ABGR32.color(255, 255, 255, 255));

		consumerA.addVertex(poseStack.last().pose(), 0.5f, -0.5f, 0)
				 .setUv(1, 1)
				 .setColor(FastColor.ABGR32.color(255, 255, 255, 255));

		consumerA.addVertex(poseStack.last().pose(), 0.5f, 0.5f, 0)
				 .setUv(1, 0)
				 .setColor(FastColor.ABGR32.color(255, 255, 255, 255));

		consumerA.addVertex(poseStack.last().pose(), -0.5f, 0.5f, 0)
				 .setUv(0, 0)
				 .setColor(FastColor.ABGR32.color(255, 255, 255, 255));

		if(!ApertureInnovations.isIrisLoaded())
		{
			VertexConsumer consumerB = buffer.getBuffer(PortalRenderTypes.portalEndMask());
			consumerB.addVertex(poseStack.last().pose(), -0.5f, -0.5f, 0);
			consumerB.addVertex(poseStack.last().pose(), 0.5f, -0.5f, 0);
			consumerB.addVertex(poseStack.last().pose(), 0.5f, 0.5f, 0);
			consumerB.addVertex(poseStack.last().pose(), -0.5f, 0.5f, 0);
		}
		poseStack.popPose();
	}

	public static void renderPortalFrame(ResourceLocation texture, ColorUtil.RGBA color, MultiBufferSource buffer, PoseStack poseStack) {
		poseStack.pushPose();
		VertexConsumer consumerA = buffer.getBuffer(PortalRenderTypes.portalFrame(texture));
		consumerA.addVertex(poseStack.last().pose(), -0.5f, -0.5f, 0)
				 .setUv(0, 1)
				 .setColor(color.red(), color.green(), color.blue(), color.alpha());
		consumerA.addVertex(poseStack.last().pose(), 0.5f, -0.5f, 0)
				 .setUv(1, 1)
				 .setColor(color.red(), color.green(), color.blue(), color.alpha());
		consumerA.addVertex(poseStack.last().pose(), 0.5f, 0.5f, 0)
				 .setUv(1, 0)
				 .setColor(color.red(), color.green(), color.blue(), color.alpha());
		consumerA.addVertex(poseStack.last().pose(), -0.5f, 0.5f, 0)
				 .setUv(0, 0)
				 .setColor(color.red(), color.green(), color.blue(), color.alpha());

		poseStack.popPose();
	}

	public static void renderPortalHighlight(MultiBufferSource buffer, PoseStack poseStack,
											 ResourceLocation texture, ColorUtil.RGBA color, boolean isPrimary) {
		poseStack.pushPose();
		Tesselator tesselator = Tesselator.getInstance();
		Matrix4f matrix = poseStack.last().pose();

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_GREATER);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), color.alpha());

		BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		builder.addVertex(matrix, -0.5f, -0.5f, 0).setUv(0.0f, 1.0f);
		builder.addVertex(matrix,  0.5f, -0.5f, 0).setUv(1f, 1.0f);
		builder.addVertex(matrix,  0.5f,  0.5f, 0).setUv(1f, 0.0f);
		builder.addVertex(matrix, -0.5f,  0.5f, 0).setUv(0.0f, 0.0f);

		BufferUploader.drawWithShader(builder.buildOrThrow());
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

		RenderSystem.disableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);

		poseStack.popPose();
	}

	public static void renderPortalVortex(ClientPortalLink link, Vec3 pos, Camera camera,
										  TextureAtlasSprite sprite, MultiBufferSource buffer,
										  PoseStack poseStack, boolean isPrimary) {
		poseStack.pushPose();

		float xRot = isPrimary ? link.getPrimaryPortal().getXRotation() : link.getSecondaryPortal().getXRotation();
		float yRot = isPrimary ? link.getPrimaryPortal().getYRotation() : link.getSecondaryPortal().getYRotation();

		Direction direction = Direction.fromYRot(yRot);
		if(direction.getAxis().equals(Direction.Axis.X))
			yRot += 180;

		poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
		poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

		float scale = ClientPortalUtilities.getPortalOpeningAnimationProgress(link.linkID(), isPrimary);
		poseStack.scale(2f, 2f, 2f);
		poseStack.scale(scale, scale, scale);
		poseStack.translate(0, 0f, 0.0125);

		ColorUtil.RGBA color = ClientPortalUtilities.getPortalColor(link, isPrimary);

		VertexConsumer consumerA = buffer.getBuffer(PortalRenderTypes.portalVortex(sprite.atlasLocation()));
		consumerA.addVertex(poseStack.last().pose(), -0.5f, -0.5f, 0)
				 .setUv(sprite.getU0(), sprite.getV1())
				 .setColor(color.red(), color.green(), color.blue(), color.alpha());
		consumerA.addVertex(poseStack.last().pose(), 0.5f, -0.5f, 0)
				 .setUv(sprite.getU1(), sprite.getV1())
				 .setColor(color.red(), color.green(), color.blue(), color.alpha());
		consumerA.addVertex(poseStack.last().pose(), 0.5f, 0.5f, 0)
				 .setUv(sprite.getU1(), sprite.getV0())
				 .setColor(color.red(), color.green(), color.blue(), color.alpha());
		consumerA.addVertex(poseStack.last().pose(), -0.5f, 0.5f, 0)
				 .setUv(sprite.getU0(), sprite.getV0())
				 .setColor(color.red(), color.green(), color.blue(), color.alpha());

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null)
			return;

		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		boolean hasPortalGun = main.is(ItemInit.PORTAL_GUN.get()) || off.is(ItemInit.PORTAL_GUN.get());
		if (hasPortalGun)
		{
			ItemStack gunStack = main.is(ItemInit.PORTAL_GUN.get()) ? main : off;
			PortalGunItem portalGun = (PortalGunItem) gunStack.getItem();

			UUID linkID = portalGun.getUUID(gunStack, false);
			if(linkID != null && linkID.equals(link.linkID()))
			{
				ResourceLocation texture = ClientPortalUtilities.getPortalHighlightTexture(link, isPrimary);

				poseStack.translate(0.09375f, 0f, 0f);

				poseStack.pushPose();
				poseStack.translate(0.16f, -0.01f, 0.01f);
				renderPortalHighlight(buffer, poseStack, texture, color, isPrimary);
				poseStack.popPose();

				poseStack.pushPose();
				poseStack.translate(-0.32f, -0.001f, 0.01f);
				poseStack.mulPose(Axis.YP.rotationDegrees(180));
				renderPortalHighlight(buffer, poseStack, texture, color, isPrimary);
				poseStack.popPose();
			}
		}

		poseStack.popPose();
	}

}
