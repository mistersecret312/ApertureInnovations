package net.mistersecret312.aperture_innovations.multitool;

import java.util.List;

public abstract class InteractionType
{
	public static class Toggle extends InteractionType
	{
		public Toggle() {}
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
