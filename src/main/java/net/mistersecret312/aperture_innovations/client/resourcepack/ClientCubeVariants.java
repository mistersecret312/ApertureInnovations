package net.mistersecret312.aperture_innovations.client.resourcepack;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.HashMap;

public class ClientCubeVariants
{
    private static final HashMap<ResourceLocation, ClientCubeVariant> CUBE_VARIANTS = new HashMap<>();

    public static void clear()
    {
        CUBE_VARIANTS.clear();
    }

    public static boolean hasCubeVariant(ResourceLocation location)
    {
        return CUBE_VARIANTS.containsKey(location);
    }

    public static ClientCubeVariant getCubeVariant(ResourceLocation location)
    {
        if (hasCubeVariant(location))
            return CUBE_VARIANTS.get(location);

        return ClientCubeVariant.DEFAULT_VARIANT;
    }

    public static void addCubeVariant(ResourceLocation location, ClientCubeVariant cubeVariant)
    {
        if (!hasCubeVariant(location))
            CUBE_VARIANTS.put(location, cubeVariant);
        else
            ApertureInnovations.LOGGER.error("Cube Variant " + location.toString() + " already exists");
    }
}
