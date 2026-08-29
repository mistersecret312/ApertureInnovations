package net.mistersecret312.aperture_innovations.multitool;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigurationProperty<T>
{
	private final String name;
	private final String category;
	private final String translatable;

	private final ConfigurationType<T> type;
	private final InteractionType interaction;

	private final Consumer<T> setter;
	private final Supplier<T> getter;

	public ConfigurationProperty(String name, String category, String translatable, ConfigurationType<T> type,
			InteractionType interaction, Consumer<T> setter, Supplier<T> getter)
	{
		this.name = name;
		this.category = category;
		this.translatable = translatable;

		this.type = type;
		this.interaction = interaction;

		this.setter = setter;
		this.getter = getter;
	}

	public ConfigurationType<T> getType()
	{
		return type;
	}

	public InteractionType getInteraction()
	{
		return interaction;
	}

	public String getName()
	{
		return name;
	}

	public String getCategory()
	{
		return category;
	}

	public String getTranslatable()
	{
		return translatable;
	}

	public T get()
	{
		return getter.get();
	}

	public void set(T value)
	{
		this.setter.accept(value);
	}

	@SuppressWarnings("unchecked")
	public void setUnsafe(Object value)
	{
		this.set(((T) value));
	}
}
