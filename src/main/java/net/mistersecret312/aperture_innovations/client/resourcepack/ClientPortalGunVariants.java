package net.mistersecret312.aperture_innovations.client.resourcepack;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.HashMap;

public class ClientPortalGunVariants {
    private static final HashMap<ResourceLocation, ClientPortalGunVariant> GUN_VARIANTS = new HashMap<>();

    public static void clear()
    {
        GUN_VARIANTS.clear();
    }

    public static boolean hasPortalGunVariant(ResourceLocation location)
    {
        return GUN_VARIANTS.containsKey(location);
    }

    public static ClientPortalGunVariant getPortalGunVariant(ResourceLocation location)
    {
        if (hasPortalGunVariant(location))
            return GUN_VARIANTS.get(location);

        return ClientPortalGunVariant.DEFAULT_VARIANT;
    }

    public static void addPortalGunVariant(ResourceLocation location, ClientPortalGunVariant portalGunVariant)
    {
        if (!hasPortalGunVariant(location))
            GUN_VARIANTS.put(location, portalGunVariant);
        else
            ApertureInnovations.LOGGER.error("Portal Gun Variant " + location.toString() + " already exists");
    }
}
