package net.mistersecret312.aperture_innovations.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;

public class ColoredTextButton extends PlainTextButton
{
	public MultiToolScreen screen;
	public ColoredTextButton(int x, int y, int width, int height, Component message,
							 OnPress onPress, Font font, MultiToolScreen screen)
	{
		super(x, y, width, height, message, onPress, font);
		this.screen = screen;
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		if(this.isHoveredOrFocused())
		{
			Component msg = ComponentUtils.mergeStyles(this.getMessage().copy(), Style.EMPTY.withUnderlined(true));
			guiGraphics.drawString(Minecraft.getInstance().font, msg, this.getX(), this.getY(),
					(screen.glowColor == -1 || screen.glowColor == 16777215) ? 0x000000 : screen.glowColor, false);
		}
		else guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX(), this.getY(),
				(screen.glowColor == -1 || screen.glowColor == 16777215) ? 0x000000 : screen.glowColor, false);
	}
}
