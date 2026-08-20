package net.mistersecret312.aperture_innovations.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.items.CubeItem;
import net.mistersecret312.aperture_innovations.items.MultiToolItem;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemTabInit
{
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
			ApertureInnovations.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> APERTURE_INNOVATIONS = TABS.register("aperture_innovations",
			() -> CreativeModeTab.builder()
						  .icon(() -> new ItemStack(ItemInit.PORTAL_GUN.get()))
						  .title(Component.translatable("tabs.aperture_innovations"))
						  .displayItems((parameters, output) -> {
							  output.accept(PortalGunItem.createPortalGun(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "chell")));
							  output.accept(PortalGunItem.createPortalGun(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "atlas")));
							  output.accept(PortalGunItem.createPortalGun(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "pbody")));
							  output.accept(PortalGunItem.createPortalGun(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "reloaded")));

							  output.accept(MultiToolItem.createTool(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "multi_tool"),
									  -1, -1));

							  output.accept(ItemInit.LONG_FALL_BOOTS.get());

							  output.accept(ItemInit.COLORFUL_GEL.get());

							  output.accept(BlockInit.CONCRETE_SURFACE_BLOCK.get());
							  output.accept(BlockInit.CONCRETE_SURFACE_TILE_BLOCK.get());
							  output.accept(BlockInit.CONCRETE_SURFACE_1x2_BLOCK.get());

							  output.accept(BlockInit.METAL_SURFACE_BLOCK.get());
							  output.accept(BlockInit.METAL_SURFACE_TILE_BLOCK.get());
							  output.accept(BlockInit.METAL_SURFACE_1x2_BLOCK.get());

							  output.accept(BlockInit.ANTLINE.get());
							  output.accept(BlockInit.CHECKMARK.get());
							  output.accept(BlockInit.TIMER.get());

							  output.accept(CubeItem.createCube(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "weighted_storage_cube")));
							  output.accept(CubeItem.createCube(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "weighted_companion_cube")));

							  output.accept(BlockInit.PEDESTAL_BUTTON.get());
							  output.accept(BlockInit.LARGE_BUTTON.get());

							  output.accept(BlockInit.VITAL_APPARATUS_VENT.get());
						  }
						  ).build());

	public static void register(IEventBus bus)
	{
		TABS.register(bus);
	}
}
