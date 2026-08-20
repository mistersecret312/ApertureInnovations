package net.mistersecret312.aperture_innovations.client.resourcepack;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.datapack.MultiToolVariant;

import java.util.HashMap;

public class ClientMultiToolVariants
{
    private static final HashMap<ResourceLocation, ClientMultiToolVariant> MULTI_TOOL_VARIANTS = new HashMap<>();

    public static void clear()
    {
        MULTI_TOOL_VARIANTS.clear();
    }

    public static boolean hasMultiToolVariant(ResourceLocation location)
    {
        return MULTI_TOOL_VARIANTS.containsKey(location);
    }

    public static ClientMultiToolVariant getMultiToolVariant(ResourceLocation location)
    {
        if (hasMultiToolVariant(location))
            return MULTI_TOOL_VARIANTS.get(location);

        return ClientMultiToolVariant.DEFAULT_VARIANT;
    }

    public static void addMultiToolVariant(ResourceLocation location, ClientMultiToolVariant multiToolVariant)
    {
        if (!hasMultiToolVariant(location))
            MULTI_TOOL_VARIANTS.put(location, multiToolVariant);
        else
            ApertureInnovations.LOGGER.error("Multi Tool Variant " + location.toString() + " already exists");
    }
}
