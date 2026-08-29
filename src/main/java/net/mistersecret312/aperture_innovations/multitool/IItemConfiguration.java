package net.mistersecret312.aperture_innovations.multitool;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IItemConfiguration extends IHaveConfiguration
{
	List<ConfigurationProperty<?>> getConfigurationProperties(ItemStack stack, RegistryAccess registryAccess);

	@Override
	default List<ConfigurationProperty<?>> getConfigurationProperties(RegistryAccess registryAccess)
	{
		return List.of();
	}
}
