package net.mistersecret312.aperture_innovations.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.client.renderer.item.CubeItemRenderer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariants;
import net.mistersecret312.aperture_innovations.datapack.CubeVariant;
import net.mistersecret312.aperture_innovations.entities.CubeEntity;
import net.mistersecret312.aperture_innovations.init.DataComponentInit;
import net.mistersecret312.aperture_innovations.init.EntityInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class CubeItem extends Item implements GeoItem
{
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public CubeItem(Properties properties)
	{
		super(properties);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	public static ItemStack createCube(ResourceLocation variant)
	{
		CubeItem item = ItemInit.CUBE.get();
		ItemStack stack = item.getDefaultInstance();

		item.setVariant(stack, variant);

		return stack;
	}

	public static DispenseItemBehavior getDispenserBehaviour()
	{
		return (blockSource, item) ->
			{
				if(!(item.getItem() instanceof CubeItem cubeItem))
					return item;

				ServerLevel level = blockSource.level();
				Vec3 dirVec = Vec3.atLowerCornerOf(blockSource.state().getValue(DispenserBlock.FACING).getNormal());
				EntityType<?> entitytype = EntityInit.CUBE.get();
				CompoundTag tag = new CompoundTag();

				tag.putInt("color", cubeItem.getColor(item));
				tag.putInt("active_color", cubeItem.getActiveColor(item));
				tag.putInt("hull_color", cubeItem.getHullColor(item));
				tag.putString("variant", cubeItem.getVariant(item).toString());

				ListTag motion = new ListTag(3);
				Vec3 speed = dirVec.multiply(0.5, dirVec.y < 0 ? 0 : 0.5, 0.5);
				motion.add(DoubleTag.valueOf(speed.x));
				motion.add(DoubleTag.valueOf(speed.y));
				motion.add(DoubleTag.valueOf(speed.z));
				tag.put("Motion", motion);

				item.set(DataComponents.ENTITY_DATA, CustomData.of(tag));
				Entity entity = entitytype.spawn(level, item, null,
						BlockPos.containing(blockSource.center().add(dirVec)), MobSpawnType.SPAWN_EGG,
						true, false);
				item.remove(DataComponents.ENTITY_DATA);
				if(entity != null)
				{
					item.shrink(1);
					return item;
				}
				return item;
			};
	}

	@Override
	public @NotNull Component getName(@NotNull ItemStack stack)
	{
		ResourceLocation key = getVariant(stack);
		return Component.translatable("item."+key.getNamespace()+"."+key.getPath());
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components,
								@NotNull TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, components, tooltipFlag);

		Level level = context.level();
		if(level != null)
		{
			Color hsbColor = Color.getHSBColor(level.getTimeOfDay(1f)*50, 1f, 1f);
			components.add(Component.translatable("tooltip.aperture_innovations.is_configurable").withStyle((style -> style.withColor(
					hsbColor.getRGB()))));
		}
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private CubeItemRenderer renderer;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new CubeItemRenderer();

				return this.renderer;
			}
		});
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		if(level.isClientSide())
			return InteractionResult.SUCCESS;

		ItemStack itemstack = context.getItemInHand();
		if(!(itemstack.getItem() instanceof CubeItem item))
			return InteractionResult.FAIL;

		BlockPos blockpos = context.getClickedPos();
		Direction direction = context.getClickedFace();
		BlockState blockstate = level.getBlockState(blockpos);
		BlockPos blockpos1;
		if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
			blockpos1 = blockpos;
		} else {
			blockpos1 = blockpos.relative(direction);
		}

		EntityType<?> entitytype = EntityInit.CUBE.get();

		CompoundTag tag = new CompoundTag();

		tag.putInt("color", this.getColor(itemstack));
		tag.putInt("active_color", this.getActiveColor(itemstack));
		tag.putInt("hull_color", this.getHullColor(itemstack));
		tag.putString("variant", this.getVariant(itemstack).toString());

		itemstack.set(DataComponents.ENTITY_DATA, CustomData.of(tag));
		Entity spawned = entitytype.spawn((ServerLevel)level, itemstack, context.getPlayer(),
				blockpos1, MobSpawnType.SPAWN_EGG, true, false);
		itemstack.remove(DataComponents.ENTITY_DATA);
		if (spawned != null)
		{
			itemstack.shrink(1);
			level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
		}

		return InteractionResult.CONSUME;
	}

	public ResourceLocation getVariant(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.VARIANT,
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "weighted_storage_cube"));
	}

	@OnlyIn(Dist.CLIENT)
	public ClientCubeVariant getCubeVariant(ItemStack stack)
	{
		if(Minecraft.getInstance().level == null)
			return ClientCubeVariant.DEFAULT_VARIANT;

		CubeVariant variant =  Minecraft.getInstance().level.registryAccess()
											.registryOrThrow(CubeVariant.REGISTRY_KEY)
											.get(getVariant(stack));

		if(variant != null)
			return ClientCubeVariants.getCubeVariant(variant.getClientVariant());

		return ClientCubeVariant.DEFAULT_VARIANT;
	}

	public void setVariant(ItemStack stack, ResourceLocation location)
	{
		stack.set(DataComponentInit.VARIANT, location);
	}

	public Integer getColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.COLOR, 0);
	}

	public void setColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.COLOR, color);
	}

	public Integer getActiveColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ACTIVE_COLOR, 0);
	}

	public void setActiveColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.ACTIVE_COLOR, color);
	}

	public Integer getHullColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.HULL_COLOR.get(), 0);
	}

	public void setHullColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.HULL_COLOR, color);
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

}
