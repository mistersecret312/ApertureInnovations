package net.mistersecret312.aperture_innovations.client.resourcepack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.client.ColorUtil.RGBA;

public record ClientPortalGunVariant(ResourceLocation texture,
                                     ResourceLocation model, ResourceLocation animation,
                                     ResourceLocation idleCoreTexture, ResourceLocation crosshairTexture,
                                     GenericPortal genericPortal, Portal primaryPortal,
                                     Portal secondaryPortal, RGBA primaryStripeColor,
                                     RGBA secondaryStripeColor, ResourceLocation activationSound,
                                     ResourceLocation resetSound) {
    public static final RGBA FULL_COLOR = new RGBA(1F, 1F, 1F, 1F);

    private static final String PORTAL_BLOCK_PATH = "textures/block/portal/";
    private static final String ITEM_PATH = "textures/item/";

    public static final ResourceLocation DEFAULT_MODEL = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
            "geo/item/portal_gun.geo.json");
    public static final ResourceLocation DEFAULT_ANIMATION = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
            "animations/item/portal_gun.animation.json");
    public static final ResourceLocation DEFAULT_TEXTURE = ApertureInnovations.of(ITEM_PATH + "portal_gun.png");
    public static final ResourceLocation DEFAULT_IDLE_CORE_TEXTURE = ApertureInnovations.of(ITEM_PATH + "idle_core.png");
    public static final ResourceLocation DEFAULT_CROSSHAIR_TEXTURE = ApertureInnovations.of(ITEM_PATH + "chell/chell_crosshair.png");

    public static final GenericPortal DEFAULT_GENERIC = new GenericPortal(
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "generic/portal_closed.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "generic/portal_highlight.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "generic/portal_vortex.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "generic/gun_core.png"),
            ApertureInnovations.of("textures/gui/generic_crosshair.png")
    );

    public static final Portal DEFAULT_PRIMARY = new Portal(
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "chell/portal_blue_closed.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "chell/portal_blue_highlight.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "chell/portal_blue_vortex.png"),
            ApertureInnovations.of(ITEM_PATH + "chell/chell_blue_core.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "portal_mask.png"),
            FULL_COLOR, true,
            ApertureInnovations.of("portal_open_primary"),
            ApertureInnovations.of("portal_gun_fire_primary"),
            ApertureInnovations.of("portal_enter"),
            ApertureInnovations.of("portal_ambient"),
            ApertureInnovations.of("portal_invalid_surface"),
            ApertureInnovations.of("portal_fizzle")
    );

    public static final Portal DEFAULT_SECONDARY = new Portal(
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "chell/portal_orange_closed.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "chell/portal_orange_highlight.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "chell/portal_orange_vortex.png"),
            ApertureInnovations.of(ITEM_PATH + "chell/chell_orange_core.png"),
            ApertureInnovations.of(PORTAL_BLOCK_PATH + "portal_mask.png"),
            FULL_COLOR, true,
            ApertureInnovations.of("portal_open_secondary"),
            ApertureInnovations.of("portal_gun_fire_secondary"),
            ApertureInnovations.of("portal_enter"),
            ApertureInnovations.of("portal_ambient"),
            ApertureInnovations.of("portal_invalid_surface"),
            ApertureInnovations.of("portal_fizzle")
    );

    public static final ClientPortalGunVariant DEFAULT_VARIANT = new ClientPortalGunVariant(
            DEFAULT_TEXTURE, DEFAULT_MODEL, DEFAULT_ANIMATION,
            DEFAULT_IDLE_CORE_TEXTURE, DEFAULT_CROSSHAIR_TEXTURE,
            DEFAULT_GENERIC, DEFAULT_PRIMARY, DEFAULT_SECONDARY,
            FULL_COLOR, FULL_COLOR,
            ApertureInnovations.of("portal_gun_activation"),
            ApertureInnovations.of("portal_gun_reset")
    );

    public static final String TEXTURE = "texture";

    public static final String MODEL = "model";
    public static final String ANIMATION = "animation";

    public static final String IDLE_CORE_TEXTURE = "idle_core_texture";
    public static final String CROSSHAIR_TEXTURE = "crosshair_texture";

    public static final String GENERIC_PORTAL = "generic_portal";

    public static final String PRIMARY_PORTAL = "primary_portal";
    public static final String SECONDARY_PORTAL = "secondary_portal";

    public static final String PRIMARY_STRIPE_COLOR = "primary_stripe_color";
    public static final String SECONDARY_STRIPE_COLOR = "secondary_stripe_color";

    public static final String ACTIVATION_SOUND = "activation_sound";
    public static final String RESET_SOUND = "reset_sound";

    public static final Codec<ClientPortalGunVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(ClientPortalGunVariant::texture),
            ResourceLocation.CODEC.optionalFieldOf(MODEL, DEFAULT_MODEL).forGetter(ClientPortalGunVariant::model),
            ResourceLocation.CODEC.optionalFieldOf(ANIMATION, DEFAULT_ANIMATION).forGetter(ClientPortalGunVariant::animation),
            ResourceLocation.CODEC.fieldOf(IDLE_CORE_TEXTURE).forGetter(ClientPortalGunVariant::idleCoreTexture),
            ResourceLocation.CODEC.fieldOf(CROSSHAIR_TEXTURE).forGetter(ClientPortalGunVariant::crosshairTexture),
            GenericPortal.CODEC.optionalFieldOf(GENERIC_PORTAL, DEFAULT_GENERIC).forGetter(ClientPortalGunVariant::genericPortal),
            Portal.CODEC.fieldOf(PRIMARY_PORTAL).forGetter(ClientPortalGunVariant::primaryPortal),
            Portal.CODEC.fieldOf(SECONDARY_PORTAL).forGetter(ClientPortalGunVariant::secondaryPortal),
            RGBA.CODEC.fieldOf(PRIMARY_STRIPE_COLOR).forGetter(ClientPortalGunVariant::primaryStripeColor),
            RGBA.CODEC.fieldOf(SECONDARY_STRIPE_COLOR).forGetter(ClientPortalGunVariant::secondaryStripeColor),
            ResourceLocation.CODEC.fieldOf(ACTIVATION_SOUND).forGetter(ClientPortalGunVariant::activationSound),
            ResourceLocation.CODEC.fieldOf(RESET_SOUND).forGetter(ClientPortalGunVariant::resetSound)
    ).apply(instance, ClientPortalGunVariant::new));

    public static class Portal {
        public static final String CLOSED_TEXTURE = "closed_texture";
        public static final String HIGHLIGHT_TEXTURE = "highlight_texture";
        public static final String VORTEX_TEXTURE = "vortex_texture";
        public static final String CORE_TEXTURE = "core_texture";

        public static final String MASK_TEXTURE = "portal_mask_texture";

        public static final String COLOR = "color";
        public static final String GENERIC_COLORING = "generic_coloring";

        public static final String OPENING_SOUND = "opening_sound";
        public static final String SHOT_SOUND = "shot_sound";
        public static final String ENTER_SOUND = "enter_sound";
        public static final String AMBIENT_SOUND = "ambient_sound";
        public static final String INVALID_SURFACE_SOUND = "invalid_surface_sound";
        public static final String FIZZLE_SOUND = "fizzle_sound";

        public static final Codec<Portal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf(CLOSED_TEXTURE).forGetter(Portal::getClosedTexture),
                ResourceLocation.CODEC.fieldOf(HIGHLIGHT_TEXTURE).forGetter(Portal::getHighlightTexture),
                ResourceLocation.CODEC.fieldOf(VORTEX_TEXTURE).forGetter(Portal::getVortexTexture),
                ResourceLocation.CODEC.fieldOf(CORE_TEXTURE).forGetter(Portal::getCoreTexture),
                ResourceLocation.CODEC.fieldOf(MASK_TEXTURE).forGetter(Portal::getMaskTexture),

                RGBA.CODEC.optionalFieldOf(COLOR, FULL_COLOR).forGetter(Portal::getColor),
                Codec.BOOL.optionalFieldOf(GENERIC_COLORING, true).forGetter(Portal::isGenericColoring),

                ResourceLocation.CODEC.fieldOf(OPENING_SOUND).forGetter(Portal::getOpeningSound),
                ResourceLocation.CODEC.fieldOf(SHOT_SOUND).forGetter(Portal::getShotSound),
                ResourceLocation.CODEC.fieldOf(ENTER_SOUND).forGetter(Portal::getEnterSound),
                ResourceLocation.CODEC.fieldOf(AMBIENT_SOUND).forGetter(Portal::getAmbientSound),
                ResourceLocation.CODEC.fieldOf(INVALID_SURFACE_SOUND).forGetter(Portal::getInvalidSurfaceSound),
                ResourceLocation.CODEC.fieldOf(FIZZLE_SOUND).forGetter(Portal::getFizzleSound)
        ).apply(instance, Portal::new));

        private final ResourceLocation closedTexture;
        private final ResourceLocation highlightTexture;
        private final ResourceLocation vortexTexture;
        private final ResourceLocation coreTexture;

        private final ResourceLocation maskTexture;

        private final RGBA color;
        private final boolean genericColoring;

        private final ResourceLocation openingSound;
        private final ResourceLocation shotSound;
        private final ResourceLocation enterSound;
        private final ResourceLocation ambientSound;
        private final ResourceLocation invalidSurfaceSound;
        private final ResourceLocation fizzleSound;

        public Portal(ResourceLocation closedTexture, ResourceLocation highlightTexture,
                      ResourceLocation vortexTexture, ResourceLocation coreTexture,
                      ResourceLocation maskTexture,
                      RGBA color, boolean genericColoring,
                      ResourceLocation openingSound, ResourceLocation shotSound,
                      ResourceLocation enterSound, ResourceLocation ambientSound,
                      ResourceLocation invalidSurfaceSound, ResourceLocation fizzleSound) {
            this.closedTexture = closedTexture;
            this.highlightTexture = highlightTexture;
            this.vortexTexture = vortexTexture;
            this.coreTexture = coreTexture;

            this.maskTexture = maskTexture;

            this.color = color;
            this.genericColoring = genericColoring;

            this.openingSound = openingSound;
            this.shotSound = shotSound;
            this.enterSound = enterSound;
            this.ambientSound = ambientSound;
            this.invalidSurfaceSound = invalidSurfaceSound;
            this.fizzleSound = fizzleSound;
        }

        public ResourceLocation getClosedTexture() {
            return closedTexture;
        }

        public ResourceLocation getHighlightTexture() {
            return highlightTexture;
        }

        public ResourceLocation getVortexTexture() {
            return vortexTexture;
        }

        public ResourceLocation getCoreTexture() {
            return coreTexture;
        }

        public ResourceLocation getMaskTexture() {
            return maskTexture;
        }

        public RGBA getColor()
        {
            return color;
        }

        public boolean isGenericColoring()
        {
            return genericColoring;
        }

        public ResourceLocation getOpeningSound() {
            return openingSound;
        }

        public ResourceLocation getShotSound() {
            return shotSound;
        }

        public ResourceLocation getEnterSound() {
            return enterSound;
        }

        public ResourceLocation getAmbientSound() {
            return ambientSound;
        }

        public ResourceLocation getInvalidSurfaceSound() {
            return invalidSurfaceSound;
        }

        public ResourceLocation getFizzleSound() {
            return fizzleSound;
        }
    }

    public static class GenericPortal {
        public static final String CLOSED_TEXTURE = "closed_texture";
        public static final String HIGHLIGHT_TEXTURE = "highlight_texture";
        public static final String VORTEX_TEXTURE = "vortex_texture";
        public static final String CORE_TEXTURE = "core_texture";

        public static final String CROSSHAIR_TEXTURE = "crosshair_texture";

        public static final Codec<GenericPortal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf(CLOSED_TEXTURE).forGetter(GenericPortal::getClosedTexture),
                ResourceLocation.CODEC.fieldOf(HIGHLIGHT_TEXTURE).forGetter(GenericPortal::getHighlightTexture),
                ResourceLocation.CODEC.fieldOf(VORTEX_TEXTURE).forGetter(GenericPortal::getVortexTexture),
                ResourceLocation.CODEC.fieldOf(CORE_TEXTURE).forGetter(GenericPortal::getCoreTexture),
                ResourceLocation.CODEC.fieldOf(CROSSHAIR_TEXTURE).forGetter(GenericPortal::getCrosshairTexture)
        ).apply(instance, GenericPortal::new));

        public final ResourceLocation closedTexture;
        public final ResourceLocation highlightTexture;
        public final ResourceLocation vortexTexture;
        public final ResourceLocation coreTexture;

        public final ResourceLocation crosshairTexture;

        public GenericPortal(ResourceLocation closedTexture, ResourceLocation highlightTexture,
                             ResourceLocation vortexTexture, ResourceLocation coreTexture,
                             ResourceLocation crosshairTexture) {
            this.closedTexture = closedTexture;
            this.highlightTexture = highlightTexture;
            this.vortexTexture = vortexTexture;
            this.coreTexture = coreTexture;
            this.crosshairTexture = crosshairTexture;
        }

        public ResourceLocation getVortexTexture() {
            return vortexTexture;
        }

        public ResourceLocation getClosedTexture() {
            return closedTexture;
        }

        public ResourceLocation getHighlightTexture() {
            return highlightTexture;
        }

        public ResourceLocation getCoreTexture() {
            return coreTexture;
        }

        public ResourceLocation getCrosshairTexture() {
            return crosshairTexture;
        }
    }
}
