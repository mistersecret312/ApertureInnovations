package net.mistersecret312.aperture_innovations.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.items.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemInit
{
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ApertureInnovations.MODID);

	public static final DeferredItem<PortalGunItem> PORTAL_GUN = ITEMS.register("portal_gun",
			() -> new PortalGunItem(new Item.Properties().stacksTo(1).fireResistant()));
	public static final DeferredItem<ColorfulGelItem> COLORFUL_GEL = ITEMS.register("colorful_gel",
			() -> new ColorfulGelItem(new Item.Properties().stacksTo(64)));

	public static final DeferredItem<LongFallBootsItem> LONG_FALL_BOOTS = ITEMS.register("long_fall_boots",
			() -> new LongFallBootsItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1).fireResistant()));

	public static final DeferredItem<CubeItem> CUBE = ITEMS.register("cube",
			() -> new CubeItem(new Item.Properties().stacksTo(64).fireResistant()));

	public static final DeferredItem<MultiBlockItem> VITAL_APPARATUS_VENT = ITEMS.register("vital_apparatus_vent",
			() -> new MultiBlockItem(BlockInit.VITAL_APPARATUS_VENT.get(), new Item.Properties().stacksTo(64).fireResistant()));
	public static final DeferredItem<MultiBlockItem> PEDESTAL_BUTTON = ITEMS.register("pedestal_button",
			() -> new MultiBlockItem(BlockInit.PEDESTAL_BUTTON.get(), new Item.Properties().stacksTo(64).fireResistant()));
	public static final DeferredItem<MultiBlockItem> LARGE_BUTTON = ITEMS.register("large_button",
			() -> new MultiBlockItem(BlockInit.LARGE_BUTTON.get(), new Item.Properties().stacksTo(64).fireResistant()));


	public static final DeferredItem<MultiToolItem> MULTI_TOOL = ITEMS.register("multi_tool",
			() -> new MultiToolItem(new Item.Properties().stacksTo(1).fireResistant()));

	public static void register(IEventBus bus)
	{
		ITEMS.register(bus);
	}
}
