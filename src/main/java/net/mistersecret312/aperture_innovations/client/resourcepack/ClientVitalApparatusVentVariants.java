package net.mistersecret312.aperture_innovations.client.resourcepack;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.HashMap;

public class ClientVitalApparatusVentVariants
{
    private static final HashMap<ResourceLocation, ClientVitalApparatusVentVariant> VITAL_APPARATUS_VENT_VARIANTS = new HashMap<>();

    public static void clear()
    {
        VITAL_APPARATUS_VENT_VARIANTS.clear();
    }

    public static boolean hasVitalApparatusVentVariant(ResourceLocation location)
    {
        return VITAL_APPARATUS_VENT_VARIANTS.containsKey(location);
    }

    public static ClientVitalApparatusVentVariant getVitalApparatusVentVariant(ResourceLocation location)
    {
        if (hasVitalApparatusVentVariant(location))
            return VITAL_APPARATUS_VENT_VARIANTS.get(location);

        return ClientVitalApparatusVentVariant.DEFAULT_VARIANT;
    }

    public static void addVitalApparatusVentVariant(ResourceLocation location, ClientVitalApparatusVentVariant multiToolVariant)
    {
        if (!hasVitalApparatusVentVariant(location))
            VITAL_APPARATUS_VENT_VARIANTS.put(location, multiToolVariant);
        else
            ApertureInnovations.LOGGER.error("Vital Apparatus Vent Variant " + location.toString() + " already exists");
    }
}
