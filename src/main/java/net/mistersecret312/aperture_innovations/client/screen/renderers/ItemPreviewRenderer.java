package net.mistersecret312.aperture_innovations.client.screen.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.IItemConfiguration;

import java.util.HashMap;

public class ItemPreviewRenderer implements PreviewRenderer
{
	public ItemStack stack;
	public InteractionHand hand;
	public ItemPreviewRenderer(ItemStack stack, InteractionHand hand)
	{
		this.stack = stack.copy();
		this.hand = hand;
	}

	@Override
	public void render(GuiGraphics graphics, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
	{
		poseStack.pushPose();

		poseStack.scale(5, 5, 5);
		poseStack.translate(-8, -14, 0);

		graphics.renderItem(this.stack, 0, 0);
		poseStack.popPose();
	}

	@Override
	public void applyFakeState(HashMap<String, Object> map)
	{
		if(stack == null || !(stack.getItem() instanceof IItemConfiguration configuration))
			return;

		for(ConfigurationProperty<?> property : configuration.getConfigurationProperties(stack, Minecraft.getInstance().level.registryAccess()))
		{
			Object value = map.get(property.getName());
			if(value != null)
				property.setUnsafe(value);
		}
	}
}
