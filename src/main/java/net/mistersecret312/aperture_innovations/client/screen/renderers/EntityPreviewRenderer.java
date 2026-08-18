package net.mistersecret312.aperture_innovations.client.screen.renderers;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.mistersecret312.aperture_innovations.entities.CubeEntity;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;

import java.util.HashMap;

public class EntityPreviewRenderer implements PreviewRenderer
{
	public final Entity entity;
	public EntityPreviewRenderer(Level level, Entity original)
	{
		this.entity = original.getType().create(level);
		CompoundTag tag = original.saveWithoutId(new CompoundTag());
		if(this.entity != null)
		{
			this.entity.load(tag);
			this.entity.setUUID(original.getUUID());
		}
	}

	@Override
	public void render(GuiGraphics graphics, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		if(this.entity == null)
			return;

		poseStack.pushPose();
		poseStack.translate(0.0, 0.0, 50.0);
		poseStack.scale(30, -30, 30);

		float rotation = Minecraft.getInstance().levelRenderer.getTicks();
		poseStack.mulPose(Axis.YP.rotationDegrees(rotation % 360));

		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		entityrenderdispatcher.setRenderShadow(false);

		RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, graphics.bufferSource(), 15728880));
		graphics.flush();
		entityrenderdispatcher.setRenderShadow(true);
		poseStack.popPose();
		Lighting.setupFor3DItems();
	}

	@Override
	public void applyFakeState(HashMap<String, Object> map)
	{
		if(this.entity == null)
			return;
		if(!(this.entity instanceof IHaveConfiguration configurable))
			return;

		for(ConfigurationProperty<?> property : configurable.getConfigurationProperties(Minecraft.getInstance().level.registryAccess()))
		{
			Object value = map.get(property.getName());
			if(value != null)
				property.setUnsafe(value);
		}
	}
}
