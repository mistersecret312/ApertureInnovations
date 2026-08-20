package net.mistersecret312.aperture_innovations.client.resourcepack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;

import java.util.Optional;

public record ClientMultiToolVariant(ResourceLocation modelPath, ResourceLocation hullTexture,
									 Optional<ResourceLocation> glowTexture, Optional<ResourceLocation> genericGlowTexture,
									 ResourceLocation menuTexture, ResourceLocation menuInsideTexture,
									 ResourceLocation genericMenuInsideTexture)
{
	public static final String MODEL = "model";
	public static final String HULL = "hull_texture";
	public static final String GLOW = "glow_texture";
	public static final String GENERIC_GLOW = "coloring_glow_texture";
	public static final String MENU = "menu_texture";
	public static final String MENU_INSIDE = "inside_menu_texture";
	public static final String GENERIC_MENU_INSIDE = "coloring_inside_menu_texture";

	public static final Codec<ClientMultiToolVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf(MODEL).forGetter(ClientMultiToolVariant::modelPath),
			ResourceLocation.CODEC.fieldOf(HULL).forGetter(ClientMultiToolVariant::hullTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(GLOW).forGetter(ClientMultiToolVariant::glowTexture),
			ResourceLocation.CODEC.lenientOptionalFieldOf(GENERIC_GLOW).forGetter(ClientMultiToolVariant::genericGlowTexture),
			ResourceLocation.CODEC.fieldOf(MENU).forGetter(ClientMultiToolVariant::menuTexture),
			ResourceLocation.CODEC.fieldOf(MENU_INSIDE).forGetter(ClientMultiToolVariant::menuInsideTexture),
			ResourceLocation.CODEC.fieldOf(GENERIC_MENU_INSIDE).forGetter(ClientMultiToolVariant::genericMenuInsideTexture)
	).apply(instance, ClientMultiToolVariant::new));


	public static final ClientMultiToolVariant DEFAULT_VARIANT = new ClientMultiToolVariant(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "geo/item/multi_tool.geo.json"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/item/multi_tool/tool.png"),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/item/multi_tool/tool_glow.png")),
			Optional.of(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/item/multi_tool/tool_glow_generic.png")),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/gui/multi_tool/menu.png"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/gui/multi_tool/menu_inside.png"),
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/gui/multi_tool/menu_inside_generic.png")
	);

}
