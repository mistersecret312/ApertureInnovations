package net.mistersecret312.aperture_innovations.client.screen.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

public interface PreviewRenderer
{
	void render(GuiGraphics graphics, PoseStack poseStack, int mouseX, int mouseY, float partialTick);

	void applyFakeState(HashMap<String, Object> map);
}
