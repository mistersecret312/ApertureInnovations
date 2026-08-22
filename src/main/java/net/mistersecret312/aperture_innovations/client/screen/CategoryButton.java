package net.mistersecret312.aperture_innovations.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.mistersecret312.aperture_innovations.multitool.Color;

public class CategoryButton extends PlainTextButton
{
	public MultiToolScreen screen;
	public CategoryButton(int x, int y, int width, int height, Component message, OnPress onPress, Font font,
						  MultiToolScreen screen)
	{
		super(x, y, width, height, message, onPress, font);
		this.screen = screen;
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		PoseStack poseStack = guiGraphics.pose();
		if(screen.categoryWidth != 0 && screen.categoryHeight != 0)
		{
			int width = screen.categoryWidth;
			int height = screen.categoryHeight;
			int x = screen.categoryBoxX;
			int y = screen.categoryBoxY;

			poseStack.pushPose();
			Color color = Color.fromInt(screen.mainColor);
			guiGraphics.setColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
			screen.renderStretchedButton(guiGraphics, screen.getMenuTexture(), x, y,
					Math.max(19, width + 7), Math.max(31, height), 0, 196, 19, 31,
					21, 9, 9, 9);
			guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

			color = Color.fromInt(screen.glowColor);
			guiGraphics.setColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
			screen.renderStretchedButton(guiGraphics, screen.getMenuInsideTexture(), x, y,
					Math.max(19, width + 7), Math.max(31, height), 0, 196, 19, 31,
					21, 9, 9, 9);
			guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			poseStack.popPose();
		}
		else
		{
			poseStack.pushPose();
			Color color = Color.fromInt(screen.mainColor);
			guiGraphics.setColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
			screen.renderStretchedButton(guiGraphics, screen.getMenuTexture(), this.getX() - 4, this.getY() - 4,
					Math.max(19, width + 7), Math.max(22, height), 19, 196, 19, 22, 11, 10, 9, 9);
			guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

			poseStack.popPose();
		}

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
