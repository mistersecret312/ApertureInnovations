package net.mistersecret312.aperture_innovations.multitool;

import net.minecraft.core.RegistryAccess;

import java.util.List;

public interface IHaveConfiguration
{
	List<ConfigurationProperty<?>> getConfigurationProperties(RegistryAccess registryAccess);
}
