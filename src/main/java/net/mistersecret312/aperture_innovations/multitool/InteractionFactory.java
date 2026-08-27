package net.mistersecret312.aperture_innovations.multitool;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.mistersecret312.aperture_innovations.client.screen.MultiToolScreen;
import net.mistersecret312.aperture_innovations.client.screen.inputs.DropdownWidget;
import net.mistersecret312.aperture_innovations.client.screen.inputs.SteppedSliderWidget;

import java.text.NumberFormat;

public class InteractionFactory
{
	public static int makeWidget(InteractionType.Toggle toggle, ConfigurationProperty<?> property, int x, int y, MultiToolScreen screen)
	{
		String name = property.getName();
		MutableComponent component = Component.translatable(property.getTranslatable());
		PlainTextButton button = new PlainTextButton(x, y, Minecraft.getInstance().font.width(component), Minecraft.getInstance().font.lineHeight,
				component,
				press ->
					{
						Object object = screen.properties.get(name);
						if(object instanceof Boolean bool)
						{
							screen.properties.put(property.getName(), !bool);
						}
					},
				Minecraft.getInstance().font);

		screen.addCategoryWidget(button, screen.categories.get(property.getCategory()));
		return 24;
	}

	public static int makeWidget(InteractionType.NumberField field, ConfigurationProperty<?> property, int x, int y, MultiToolScreen screen)
	{
		return 24;
	}

	public static int makeWidget(InteractionType.RGBColorPicker colorPicker, ConfigurationProperty<?> property, int x, int y, MultiToolScreen screen)
	{
		if(screen.colorSliderMode)
		{
			makeSliderBox(colorPicker, property, x, y, screen);
		}
		else
		{
			makeTextBox(colorPicker, property, x, y, screen);
		}
		return 40;
	}

	public static void makeSliderBox(InteractionType.RGBColorPicker colorPicker, ConfigurationProperty<?> property,
									 int x, int y, MultiToolScreen screen)
	{
		String name = property.getName();
		y -= 22;
		x -= 22;
		for(int i = 0; i < 3; i++)
		{
			SteppedSliderWidget box = new SteppedSliderWidget(x + i * 44, y,
					40, 16, Component.empty(), 0, screen.hsbMode ? 1 : 255, screen.hsbMode ? 0.05 : 1, 0, null);

			String text = screen.hsbMode ? "H" : "R";

			int red = ((Color) screen.properties.get(name)).red();
			int green = ((Color) screen.properties.get(name)).green();
			int blue = ((Color) screen.properties.get(name)).blue();
			float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);

			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMaximumFractionDigits(3);

			hsb[0] = Float.parseFloat(format.format(hsb[0]));
			hsb[1] = Float.parseFloat(format.format(hsb[1]));
			hsb[2] = Float.parseFloat(format.format(hsb[2]));

			box.setValue(screen.hsbMode ? hsb[0] : red);
			if(i == 1)
			{
				text = screen.hsbMode ? "S" : "G";
				box.setValue(screen.hsbMode ? hsb[1] : green);
			}
			if(i == 2)
			{
				text = "B";
				box.setValue(screen.hsbMode ? hsb[2] : blue);
			}

			box.prefix = text;

			int finalI = i;
			box.setOnValueChanged(value ->
				{
					Object object = screen.properties.get(name);
					if(object instanceof Color(int r, int g, int b))
					{
						float[] HSB;
						if(finalI == 0)
						{
							double H = Mth.wrapDegrees(value);
							HSB = java.awt.Color.RGBtoHSB(r, g, b, null);

							java.awt.Color color = java.awt.Color.getHSBColor((float) H, HSB[1], HSB[2]);
							if(screen.hsbMode)
								screen.properties.put(name, new Color(color.getRed(), color.getGreen(), color.getBlue()));
							else screen.properties.put(name, new Color((int) value.doubleValue(), g, b));
						}
						if(finalI == 1)
						{
							double S = Mth.clamp(value, 0, 1);
							HSB = java.awt.Color.RGBtoHSB(r, g, b, null);

							java.awt.Color color = java.awt.Color.getHSBColor(HSB[0], (float) S, HSB[2]);
							if(screen.hsbMode)
								screen.properties.put(name, new Color(color.getRed(), color.getGreen(), color.getBlue()));
							else screen.properties.put(name, new Color(r, (int) value.doubleValue(), b));
						}
						if(finalI == 2)
						{
							double B = Mth.clamp(value, 0, 1);
							HSB = java.awt.Color.RGBtoHSB(r, g, b, null);

							java.awt.Color color = java.awt.Color.getHSBColor(HSB[0], HSB[1], (float) B);
							if(screen.hsbMode)
								screen.properties.put(name, new Color(color.getRed(), color.getGreen(), color.getBlue()));
							else screen.properties.put(name, new Color(r, g, (int) value.doubleValue()));
						}
					}
					screen.renderer.applyFakeState(screen.properties);
				});
			screen.addCategoryWidget(box, screen.categories.get(property.getCategory()));

			MutableComponent component = screen.hsbMode ? Component.literal("Hue") : Component.literal("Red");
			if (i == 1) component = screen.hsbMode ? Component.literal("Saturation") : Component.literal("Green");
			if (i == 2) component = screen.hsbMode ? Component.literal("Brightness") : Component.literal("Blue");

			int textW = Minecraft.getInstance().font.width(component);
			int labelX = x + (i * 44) + (20 - textW / 2);
			int labelY = y + 18;

			MutableComponent finalComponent = component;
			screen.addCategoryWidget(new AbstractStringWidget(labelX, labelY, textW, Minecraft.getInstance().font.lineHeight,
					finalComponent, Minecraft.getInstance().font) {
				@Override
				protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
					graphics.pose().pushPose();
					graphics.pose().translate(this.getX(), this.getY(), 0);
					graphics.pose().scale(0.75f, 0.75f, 1f);
					graphics.drawString(Minecraft.getInstance().font, finalComponent, 0, 0, 16777215);
					graphics.pose().popPose();
				}
			}, screen.categories.get(property.getCategory()));
		}

		MutableComponent component = Component.translatable("category.aperture_innovations."+property.getCategory()+"."+name);
		int titleW = Minecraft.getInstance().font.width(component);

		screen.addCategoryWidget(new AbstractStringWidget(x+64-titleW/2, y - 10, Minecraft.getInstance().font.width(component),
				Minecraft.getInstance().font.lineHeight, component, Minecraft.getInstance().font)
		{
			@Override
			protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
			{
				int color = ((Color) screen.properties.get(name)).packagedInt();
				if (color == 0)
					color = 16777215;
				graphics.drawString(Minecraft.getInstance().font, component, this.getX(), this.getY(), color);
			}
		}, screen.categories.get(property.getCategory()));
	}

	public static void makeTextBox(InteractionType.RGBColorPicker colorPicker, ConfigurationProperty<?> property, int x,
								   int y, MultiToolScreen screen)
	{
		String name = property.getName();
		y -= 22;
		x -= 22;
		for(int i = 0; i < 3; i++)
		{
			EditBox box = new EditBox(Minecraft.getInstance().font, x + i * 44, y, 40, 16, Component.empty());
			String text = screen.hsbMode ? "H" : "R";
			int textColor = screen.hsbMode ? 0xFFFFFF : 0xFF0000;

			int red = ((Color) screen.properties.get(name)).red();
			int green = ((Color) screen.properties.get(name)).green();
			int blue = ((Color) screen.properties.get(name)).blue();
			float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);

			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMaximumFractionDigits(3);

			hsb[0] = Float.parseFloat(format.format(hsb[0]));
			hsb[1] = Float.parseFloat(format.format(hsb[1]));
			hsb[2] = Float.parseFloat(format.format(hsb[2]));

			box.setValue(String.valueOf(screen.hsbMode ? hsb[0] : red));
			if(i == 1)
			{
				text = screen.hsbMode ? "S" : "G";
				textColor = screen.hsbMode ? 0xFFFFFF : 0x00FF00;

				box.setValue(String.valueOf(screen.hsbMode ? hsb[1] : green));
			}
			if(i == 2)
			{
				text = "B";
				textColor = screen.hsbMode ? 0xFFFFFF : 0x0000FF;
				box.setValue(String.valueOf(screen.hsbMode ? hsb[2] : blue));
			}

			int finalI = i;
			box.setTextColor(textColor);
			box.setResponder(string ->
				{
					if(string.isBlank()) return;

					double value = 0;
					try
					{
						if(!screen.hsbMode)
							value = Integer.parseInt(string);
						else value = Double.parseDouble(string);
					}
					catch(NumberFormatException ignored)
					{
						box.setValue("");
					}

					Object object = screen.properties.get(name);
					if(object instanceof Color(int r, int g, int b))
					{
						float[] HSB = null;
						if(finalI == 0)
						{
							double H = Mth.wrapDegrees(value);
							HSB = java.awt.Color.RGBtoHSB(r, g, b, null);

							java.awt.Color color = java.awt.Color.getHSBColor((float) H, HSB[1], HSB[2]);
							if(screen.hsbMode)
								screen.properties.put(name, new Color(color.getRed(), color.getGreen(), color.getBlue()));
							else screen.properties.put(name, new Color((int) value, g, b));
						}
						if(finalI == 1)
						{
							double S = Mth.clamp(value, 0, 1);
							HSB = java.awt.Color.RGBtoHSB(r, g, b, null);

							java.awt.Color color = java.awt.Color.getHSBColor(HSB[0], (float) S, HSB[2]);
							if(screen.hsbMode)
								screen.properties.put(name, new Color(color.getRed(), color.getGreen(), color.getBlue()));
							else screen.properties.put(name, new Color(r, (int) value, b));
						}
						if(finalI == 2)
						{
							double B = Mth.clamp(value, 0, 1);
							HSB = java.awt.Color.RGBtoHSB(r, g, b, null);

							java.awt.Color color = java.awt.Color.getHSBColor(HSB[0], HSB[1], (float) B);
							if(screen.hsbMode)
								screen.properties.put(name, new Color(color.getRed(), color.getGreen(), color.getBlue()));
							else screen.properties.put(name, new Color(r, g, (int) value));
						}
					}
					screen.renderer.applyFakeState(screen.properties);
				});
			box.setHint(Component.literal(text));
			screen.addCategoryWidget(box, screen.categories.get(property.getCategory()));

			MutableComponent component = screen.hsbMode ? Component.literal("Hue") : Component.literal("Red");
			if (i == 1) component = screen.hsbMode ? Component.literal("Saturation") : Component.literal("Green");
			if (i == 2) component = screen.hsbMode ? Component.literal("Brightness") : Component.literal("Blue");

			int textW = Minecraft.getInstance().font.width(component);
			int labelX = x + (i * 44) + (20 - textW / 2);
			int labelY = y + 18;

			MutableComponent finalComponent = component;
			screen.addCategoryWidget(new AbstractStringWidget(labelX, labelY, textW, Minecraft.getInstance().font.lineHeight,
					finalComponent, Minecraft.getInstance().font) {
				@Override
				protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
					graphics.pose().pushPose();
					graphics.pose().translate(this.getX(), this.getY(), 0);
					graphics.pose().scale(0.75f, 0.75f, 1f);
					graphics.drawString(Minecraft.getInstance().font, finalComponent, 0, 0, 16777215);
					graphics.pose().popPose();
				}
			}, screen.categories.get(property.getCategory()));
		}

		MutableComponent component = Component.translatable("category.aperture_innovations."+property.getCategory()+"."+name);
		int titleW = Minecraft.getInstance().font.width(component);

		screen.addCategoryWidget(new AbstractStringWidget(x+64-titleW/2, y - 10, titleW, Minecraft.getInstance().font.lineHeight, component, Minecraft.getInstance().font) {
			@Override
			protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
				int color = ((Color) screen.properties.get(name)).packagedInt();
				if (color == 0)
					color = 16777215;
				graphics.drawString(Minecraft.getInstance().font, component, this.getX(), this.getY(), color);
			}
		}, screen.categories.get(property.getCategory()));
	}

	public static int makeWidget(InteractionType.TextField field, ConfigurationProperty<?> property, int x, int y, MultiToolScreen screen)
	{
		return 24;
	}

	public static int makeWidget(InteractionType.Slider sliderType, ConfigurationProperty<?> property, int x, int y, MultiToolScreen screen)
	{
		String name = property.getName();
		SteppedSliderWidget slider = new SteppedSliderWidget(x, y, 40, 12,
				Component.literal("R"), sliderType.min, sliderType.max, sliderType.step, 0.0, value ->
			{
				screen.renderer.applyFakeState(screen.properties);
			});

		screen.addCategoryWidget(slider, screen.categories.get(property.getCategory()));
		return 20;
	}

	public static int makeWidget(InteractionType.ListChoice picker, ConfigurationProperty<?> property, int x, int y, MultiToolScreen screen)
	{
		String name = property.getName();
		Component component = Component.translatable("multi_tool."+property.get().toString().replace(':', '.'));

		int width = Minecraft.getInstance().font.width(component) + 15;
		int centerLineX = x + 90;

		int targetX = centerLineX - (width / 2);
		if (targetX + width > centerLineX)
			targetX = centerLineX - width+14;

		DropdownWidget widget = new DropdownWidget(targetX, y-4,
				width,
				Minecraft.getInstance().font.lineHeight * 2,
				picker.allowedValues, property.get().toString(),
				(selected) ->
					{
						screen.properties.put(name, ResourceLocation.parse(selected));
						screen.renderer.applyFakeState(screen.properties);
					});

		Component message = Component.translatable("multi_tool."+screen.properties.get(name).toString().replace(':', '.'));
		widget.setMessage(message);
		screen.addCategoryWidget(widget, screen.categories.get(property.getCategory()));

		return 28;
	}

	public static int makeWidget(InteractionType interaction, ConfigurationProperty<?> configurationProperty, int x, int currentY, MultiToolScreen multiToolScreen)
	{
		return switch(interaction)
		{
			case InteractionType.Toggle toggle ->
					makeWidget(toggle, configurationProperty, x, currentY, multiToolScreen);
			case InteractionType.NumberField field ->
					makeWidget(field, configurationProperty, x, currentY, multiToolScreen);
			case InteractionType.TextField field ->
					makeWidget(field, configurationProperty, x, currentY, multiToolScreen);
			case InteractionType.RGBColorPicker picker ->
					makeWidget(picker, configurationProperty, x, currentY, multiToolScreen);
			case InteractionType.ListChoice choice ->
					makeWidget(choice, configurationProperty, x, currentY, multiToolScreen);
			case InteractionType.Slider slider ->
					makeWidget(slider, configurationProperty, x, currentY, multiToolScreen);
			case null, default -> 0;
		};
	}
}
