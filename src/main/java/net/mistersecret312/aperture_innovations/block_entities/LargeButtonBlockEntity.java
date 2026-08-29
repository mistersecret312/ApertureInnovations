package net.mistersecret312.aperture_innovations.block_entities;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.multiblock.MasterBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.LargeButtonBlock;
import net.mistersecret312.aperture_innovations.blocks.multiblock.OrientedMasterBlock;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientLargeButtonVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientLargeButtonVariants;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPedestalButtonVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPedestalButtonVariants;
import net.mistersecret312.aperture_innovations.datapack.LargeButtonVariant;
import net.mistersecret312.aperture_innovations.datapack.PedestalButtonVariant;
import net.mistersecret312.aperture_innovations.init.BlockEntityInit;
import net.mistersecret312.aperture_innovations.init.MultiToolConfigTypeInit;
import net.mistersecret312.aperture_innovations.init.SoundInit;
import net.mistersecret312.aperture_innovations.init.TagInit;
import net.mistersecret312.aperture_innovations.multitool.Color;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;
import net.mistersecret312.aperture_innovations.multitool.InteractionType;
import net.mistersecret312.aperture_innovations.network.ServerboundLargeButtonLoadPacket;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LargeButtonBlockEntity extends MasterBlockEntity implements GeoBlockEntity, IHaveConfiguration
{
	protected static final RawAnimation PRESS = RawAnimation.begin().thenPlay("down");
	protected static final RawAnimation UNPRESS = RawAnimation.begin().thenPlay("up");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public Color color = new Color(0, 0, 0);
	public Color activeColor = new Color(0, 0, 0);
	public Color buttonColor = new Color(0, 0, 0);
	public Color hullColor = new Color(0, 0, 0);

	public ResourceLocation variant = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
			"large_button");
	public boolean updatePressed = false;

	public LargeButtonBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(BlockEntityInit.LARGE_BUTTON.get(), pos, blockState);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		return this.saveWithoutMetadata(registries);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		int[] hullArray = {hullColor.red(), hullColor.green(), hullColor.blue()};
		int[] idleArray = {color.red(), color.green(), color.blue()};
		int[] buttonArray = {buttonColor.red(), buttonColor.green(), buttonColor.blue()};
		int[] activeArray = {activeColor.red(), activeColor.green(), activeColor.blue()};

		tag.putIntArray("hull_color", hullArray);
		tag.putIntArray("idle_color", idleArray);
		tag.putIntArray("button_color", buttonArray);
		tag.putIntArray("active_color", activeArray);

		tag.putString("variant", variant.toString());

		super.saveAdditional(tag, registries);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		int[] hullArray = tag.getIntArray("hull_color");
		int[] idleArray = tag.getIntArray("idle_color");
		int[] buttonArray = tag.getIntArray("button_color");
		int[] activeArray = tag.getIntArray("active_color");

		this.hullColor = new Color(hullArray[0], hullArray[1], hullArray[2]);
		this.color = new Color(idleArray[0], idleArray[1], idleArray[2]);
		this.buttonColor = new Color(buttonArray[0], buttonArray[1], buttonArray[2]);
		this.activeColor = new Color(activeArray[0], activeArray[1], activeArray[2]);

		this.variant = ResourceLocation.parse(tag.getString("variant"));
	}

	public ClientLargeButtonVariant getClientVariant()
	{
		if(level == null)
			return ClientLargeButtonVariant.DEFAULT_VARIANT;
		LargeButtonVariant dataVariant = getVariant(level);
		if(dataVariant == null)
			return ClientLargeButtonVariant.DEFAULT_VARIANT;

		return ClientLargeButtonVariants.getButtonVariant(dataVariant.getClientVariant());
	}

	public LargeButtonVariant getVariant(Level level)
	{
		Registry<LargeButtonVariant> registry =
				level.registryAccess().registryOrThrow(LargeButtonVariant.REGISTRY_KEY);
		if(registry.get(getVariant()) == null)
			return registry.get(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"large_button"));
		return registry.get(getVariant());
	}

	public ResourceLocation getVariant()
	{
		return variant;
	}

	public void setVariant(ResourceLocation variant)
	{
		this.variant = variant;
	}


	@Override
	public void onLoad()
	{
		super.onLoad();
		if(level != null && level.isClientSide())
			PacketDistributor.sendToServer(new ServerboundLargeButtonLoadPacket(this.getBlockPos()));
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
	{
		AnimationController<LargeButtonBlockEntity> controller =
				new AnimationController<>(this, "press", 0, this::pressController);
		controller.triggerableAnim("down", PRESS);
		controller.triggerableAnim("up", UNPRESS);
		controllers.add(controller);
	}

	private PlayState pressController(AnimationState<LargeButtonBlockEntity> state)
	{
		return PlayState.STOP;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}

	public static void tick(Level level, BlockPos pos, BlockState blockState,
							LargeButtonBlockEntity button)
	{
		if(!level.isClientSide())
		{
			if(blockState.getValue(LargeButtonBlock.UPDATE))
			{
				level.setBlock(pos, blockState.setValue(LargeButtonBlock.UPDATE, false), 1 | 2);
				blockState.getBlock().setPlacedBy(level, pos, blockState, null, ItemStack.EMPTY);
			}

		}

		if(!(blockState.getBlock() instanceof OrientedMasterBlock master))
			return;

		Vec3 normal = Vec3.atLowerCornerOf(blockState.getValue(OrientedMasterBlock.NORMAL).getNormal()).multiply(0.15, 0.15, 0.15);
		AABB box = master.getFullShape(level, pos, blockState).bounds().move(pos).deflate(0.15, 0, 0.15).move(normal.x, normal.y, normal.z);

		List<Entity> entities = level.getEntities((Entity) null, box, entity -> entity.getBoundingBox().getSize() > 0.25 && !entity.getType().is(
				TagInit.Entities.BUTTON_IGNORE));

		boolean isPressed = blockState.getValue(LargeButtonBlock.PRESSED);
		if(level.isClientSide())
			return;

		if(isPressed || button.updatePressed)
		{
			if(entities.isEmpty())
			{
				level.setBlock(pos, blockState.setValue(LargeButtonBlock.PRESSED, false), 3);
				button.triggerAnim("press", "up");
				if(!button.updatePressed)
					level.playSound(null, pos, SoundInit.LARGE_BUTTON_UP.get(), SoundSource.BLOCKS, 0.5f, 1f);
				button.updatePressed = false;
			}
		}

		if(!isPressed || button.updatePressed)
		{
			if(!entities.isEmpty())
			{
				level.setBlock(pos, blockState.setValue(LargeButtonBlock.PRESSED, true), 3);
				button.triggerAnim("press", "down");
				if(!button.updatePressed)
					level.playSound(null, pos, SoundInit.LARGE_BUTTON_DOWN.get(), SoundSource.BLOCKS, 0.5f, 1f);
				button.updatePressed = false;
			}
		}
	}

	public Color getHullColor()
	{
		return hullColor;
	}

	public void setHullColor(Color hullColor)
	{
		this.hullColor = hullColor;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public Color getButtonColor()
	{
		return buttonColor;
	}

	public void setButtonColor(Color buttonColor)
	{
		this.buttonColor = buttonColor;
	}

	public Color getActiveColor()
	{
		return activeColor;
	}

	public void setActiveColor(Color activeColor)
	{
		this.activeColor = activeColor;
	}

	@Override
	public List<ConfigurationProperty<?>> getConfigurationProperties(RegistryAccess registryAccess)
	{
		List<ConfigurationProperty<?>> list = new ArrayList<>();
		list.add(new ConfigurationProperty<>("hull_color", "color",
				"multi_tool.aperture_innovations.vital_apparatus_vent.hull_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				this::setHullColor, this::getHullColor));
		list.add(new ConfigurationProperty<>("active_color", "color",
				"multi_tool.aperture_innovations.vital_apparatus_vent.active_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				this::setActiveColor, this::getActiveColor));
		list.add(new ConfigurationProperty<>("idle_color", "color",
				"multi_tool.aperture_innovations.vital_apparatus_vent.idle_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				this::setColor, this::getColor));
		list.add(new ConfigurationProperty<>("button_color", "color",
				"multi_tool.aperture_innovations.vital_apparatus_vent.button_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				this::setButtonColor, this::getButtonColor));

		List<String> variants = new ArrayList<>();
		for(Map.Entry<ResourceKey<LargeButtonVariant>, LargeButtonVariant> entry : registryAccess
																								 .registryOrThrow(LargeButtonVariant.REGISTRY_KEY)
																								 .entrySet())
		{
			variants.add(entry.getKey().location().toString());
		}

		list.add(new ConfigurationProperty<>("variant", "variant",
				"multi_tool.aperture_innovations.variant",
				MultiToolConfigTypeInit.RESOURCE_LOCATION.get(),
				new InteractionType.ListChoice(variants, getVariant().toString()),
				this::setVariant, this::getVariant));


		for(ConfigurationProperty<?> property : list)
		{
			if(property.getName().equals("hull_color"))
				property.setUnsafe(hullColor);
			if(property.getName().equals("active_color"))
				property.setUnsafe(activeColor);
			if(property.getName().equals("idle_color"))
				property.setUnsafe(color);
			if(property.getName().equals("button_color"))
				property.setUnsafe(buttonColor);
			if(property.getName().equals("variant"))
				property.setUnsafe(variant);
		}

		return list;
	}
}
