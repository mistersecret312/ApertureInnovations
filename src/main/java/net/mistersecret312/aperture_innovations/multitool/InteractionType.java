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
import java.util.List;

public abstract class InteractionType
{
	public static class Toggle extends InteractionType
	{
		public Toggle() {}

		public int makeWidget(ConfigurationProperty<?> property, int x, int y, MultiToolScreen screen)
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
	}

	public static class NumberField extends InteractionType
	{
		public NumberField() {}
	}

	public static class RGBColorPicker extends InteractionType
	{
		public RGBColorPicker() {}
	}

	public static class TextField extends InteractionType
	{
		public final int maxSymbols;
		public TextField(int maxSymbols)
		{
			this.maxSymbols = maxSymbols;
		}
	}

	public static class Slider extends InteractionType
	{
		public final double min;
		public final double max;
		public final double step;
		public Slider(double min, double max, double step)
		{
			this.min = min;
			this.max = max;
			this.step = step;
		}
	}

	public static class ListChoice extends InteractionType
	{
		public final List<String> allowedValues;
		public final Object current;
		public ListChoice(List<String> allowedValues, String current)
		{
			this.allowedValues = allowedValues;
			this.current = current;
		}
	}
}
