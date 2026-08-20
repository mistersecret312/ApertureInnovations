package net.mistersecret312.aperture_innovations.client.resourcepack;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.HashMap;

public class ClientPedestalButtonVariants
{
    private static final HashMap<ResourceLocation, ClientPedestalButtonVariant> BUTTON_VARIANTS = new HashMap<>();

    public static void clear()
    {
        BUTTON_VARIANTS.clear();
    }

    public static boolean hasButtonVariant(ResourceLocation location)
    {
        return BUTTON_VARIANTS.containsKey(location);
    }

    public static ClientPedestalButtonVariant getButtonVariant(ResourceLocation location)
    {
        if (hasButtonVariant(location))
            return BUTTON_VARIANTS.get(location);

        return ClientPedestalButtonVariant.DEFAULT_VARIANT;
    }

    public static void addButtonVariant(ResourceLocation location, ClientPedestalButtonVariant buttonVariant)
    {
        if (!hasButtonVariant(location))
            BUTTON_VARIANTS.put(location, buttonVariant);
        else
            ApertureInnovations.LOGGER.error("Pedestal Button Variant " + location.toString() + " already exists");
    }
}
