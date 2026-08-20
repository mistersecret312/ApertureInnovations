package net.mistersecret312.aperture_innovations.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.multiblock.MasterBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.multiblock.DummyBlock;
import net.mistersecret312.aperture_innovations.client.renderer.item.CubeItemRenderer;
import net.mistersecret312.aperture_innovations.client.renderer.item.MultiTooltemRenderer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariants;
import net.mistersecret312.aperture_innovations.client.screen.MultiToolScreen;
import net.mistersecret312.aperture_innovations.client.screen.renderers.BlockEntityPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.ItemPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.PreviewRenderer;
import net.mistersecret312.aperture_innovations.datapack.MultiToolVariant;
import net.mistersecret312.aperture_innovations.datapack.PortalGunVariant;
import net.mistersecret312.aperture_innovations.init.DataComponentInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.init.ItemTabInit;
import net.mistersecret312.aperture_innovations.init.MultiToolConfigTypeInit;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;
import net.mistersecret312.aperture_innovations.multitool.IItemConfiguration;
import net.mistersecret312.aperture_innovations.multitool.InteractionType;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MultiToolItem extends Item implements GeoItem, IItemConfiguration
{
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public MultiToolItem(Properties properties)
	{
		super(properties);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	public static ItemStack createTool(ResourceLocation variant, int hullColor, int glowColor)
	{
		MultiToolItem item = ItemInit.MULTI_TOOL.get();
		ItemStack stack = item.getDefaultInstance();
		item.setVariantKey(stack, variant);
		item.setHullColor(stack, hullColor);
		item.setGlowColor(stack, glowColor);

		return stack;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		InteractionHand otherHand;
		if(usedHand == InteractionHand.MAIN_HAND)
			otherHand = InteractionHand.OFF_HAND;
		else otherHand = InteractionHand.MAIN_HAND;

		ItemStack otherStack = player.getItemInHand(otherHand);
		if(otherStack.isEmpty() && player.isShiftKeyDown())
		{
			otherStack = player.getItemInHand(usedHand);
			otherHand = usedHand;
		}
		if(level.isClientSide() && !otherStack.isEmpty())
		{
			ItemStack stack = player.getItemInHand(usedHand);
			ClientMultiToolVariant variant = ClientMultiToolVariants.getMultiToolVariant(getVariantKey(stack));
			PreviewRenderer renderer = new ItemPreviewRenderer(otherStack, otherHand);
			MultiToolScreen screen = new MultiToolScreen(otherStack.getHoverName(), otherStack.getItem() instanceof IItemConfiguration config ? config : null,
					renderer, variant, getHullColor(stack), getGlowColor(stack));

			Minecraft.getInstance().setScreen(screen);
		}

		return super.use(level, player, usedHand);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		BlockState state = level.getBlockState(pos);
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if(state.getBlock() instanceof DummyBlock dummyBlock)
		{
			MasterBlockEntity master = dummyBlock.getMaster(level, pos);
			if(master != null)
			{
				blockEntity = master;
				state = master.getBlockState();
			}
		}

		if(level.isClientSide())
		{
			ItemStack stack = context.getItemInHand();
			ClientMultiToolVariant variant = ClientMultiToolVariants.getMultiToolVariant(getVariantKey(stack));
			PreviewRenderer renderer = new BlockEntityPreviewRenderer(state, blockEntity, level.registryAccess());
			MultiToolScreen screen = new MultiToolScreen(state.getBlock().getName(),
					blockEntity instanceof IHaveConfiguration configuration ? configuration : null,
					renderer, variant, getHullColor(stack), getGlowColor(stack));

			Minecraft.getInstance().setScreen(screen);
		}

		return super.useOn(context);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private MultiTooltemRenderer renderer;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new MultiTooltemRenderer();

				return this.renderer;
			}
		});
	}

	public ResourceLocation getVariantKey(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.VARIANT,
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "multi_tool"));
	}

	public void setVariantKey(ItemStack stack, ResourceLocation variant)
	{
		stack.set(DataComponentInit.VARIANT, variant);
	}

	public int getHullColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.HULL_COLOR, -1);
	}

	public int getGlowColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ACTIVE_COLOR, -1);
	}

	public void setHullColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.HULL_COLOR, color);
	}

	public void setGlowColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.ACTIVE_COLOR, color);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
	{

	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}

	@Override
	public List<ConfigurationProperty<?>> getConfigurationProperties(ItemStack stack, RegistryAccess registryAccess)
	{
		List<ConfigurationProperty<?>> properties = new ArrayList<>();

		properties.add(new ConfigurationProperty<>("hull_color",
				"color", "multi_tool.aperture_innovations.multi_tool.hull_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setHullColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getHullColor(stack))));

		properties.add(new ConfigurationProperty<>("glow_color",
				"color", "multi_tool.aperture_innovations.multi_tool.glow_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setGlowColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getGlowColor(stack))));

		List<String> variants = new ArrayList<>();
		for(Map.Entry<ResourceKey<MultiToolVariant>, MultiToolVariant> entry : registryAccess
																					   .registryOrThrow(MultiToolVariant.REGISTRY_KEY)
																					   .entrySet())
		{
			variants.add(entry.getKey().location().toString());
		}

		properties.add(new ConfigurationProperty<>("variant", "variant",
				"multi_tool.aperture_innovations.variant",
				MultiToolConfigTypeInit.RESOURCE_LOCATION.get(),
				new InteractionType.ListChoice(variants, getVariantKey(stack).toString()),
				variant -> setVariantKey(stack, variant),
				() -> getVariantKey(stack)));

		return properties;
	}
}
