package net.mistersecret312.aperture_innovations.entities;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.LargeButtonBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.LargeButtonBlock;
import net.mistersecret312.aperture_innovations.blocks.multiblock.DummyBlock;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientCubeVariants;
import net.mistersecret312.aperture_innovations.datapack.CubeVariant;
import net.mistersecret312.aperture_innovations.init.*;
import net.mistersecret312.aperture_innovations.items.ColorfulGelItem;
import net.mistersecret312.aperture_innovations.items.CubeItem;
import net.mistersecret312.aperture_innovations.multitool.*;
import net.mistersecret312.aperture_innovations.network.ClientboundFizzleParticlesPacket;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CubeEntity extends Entity implements IFizzle, GeoEntity, IHaveConfiguration
{
	public static final EntityDataAccessor<ResourceLocation> VARIANT = SynchedEntityData.defineId(
			CubeEntity.class, EntityDataSerializerInit.RESOURCE_LOCATION.get());

	private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(
			CubeEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> HULL_COLOR = SynchedEntityData.defineId(
			CubeEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(
			CubeEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ACTIVE_COLOR = SynchedEntityData.defineId(
			CubeEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> FIZZLE_TIME = SynchedEntityData.defineId(
			CubeEntity.class, EntityDataSerializers.INT);

	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYRot;
	private double lerpXRot;

	private final SimpleContainer container = new SimpleContainer(9);
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	public CubeEntity(EntityType<?> entityType, Level level)
	{
		super(entityType, level);
	}

	public CubeEntity(Level level)
	{
		this(EntityInit.CUBE.get(), level);
	}

	@Override
	public void tick()
	{
		super.tick();
		this.tickLerp();

		if(getFizzlingTick() >= 0 && getFizzlingTick() < getMaxFizzleTime())
		{
			setFizzlingTick(getFizzlingTick() + 1);
			if(!level().isClientSide())
				PacketDistributor.sendToPlayersTrackingEntity(this, new ClientboundFizzleParticlesPacket(this.getId()));
		}

		if(getFizzlingTick() == getMaxFizzleTime())
		{
			onFinishFizzling();
			return;
		}

		if(!isNoGravity())
		{
			this.addDeltaMovement(new Vec3(0f, -0.08f, 0f));
		}

		BlockState state = level().getBlockState(this.blockPosition());
		if(state.getBlock() instanceof DummyBlock dummy)
		{
			BlockEntity master = dummy.getMaster(level(), this.blockPosition());
			if(master instanceof LargeButtonBlockEntity)
				this.setActive(master.getBlockState().getValue(LargeButtonBlock.PRESSED));
			else this.setActive(false);
		}
		else this.setActive(false);

		if(state.getBlock() instanceof LargeButtonBlock)
		{
			this.setActive(state.getValue(LargeButtonBlock.PRESSED));
		}

		float friction = 0.85f;
		if(!this.onGround())
			friction = 0.95f;
		if(getFizzlingTick() != -1)
			friction = 1f;

		this.setDeltaMovement(this.getDeltaMovement().multiply(friction, 1f, friction));
		this.move(MoverType.SELF, this.getDeltaMovement());
	}

	@Override
	public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source)
	{
		Level level = this.level();

		if(fallDistance > 5F && this.getFizzlingTick() == -1)
		{
			AABB impactZone = this.getBoundingBox().expandTowards(0f, -0.25f, 0f);
			for(Entity entity : level.getEntities(this, impactZone))
			{
				entity.hurt(entity.damageSources().fallingBlock(this), fallDistance/10);
			}
		}

		if(!level.isClientSide())
			level.playSound(null, blockPosition(), SoundInit.CUBE_IMPACT.get(), SoundSource.NEUTRAL, 0.5f, 1f);
		return false;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return this.getFizzlingTick() == -1;
	}

	@Override
	public boolean isPickable()
	{
		return this.getFizzlingTick() == -1;
	}

	@Override
	public boolean isPushable()
	{
		return this.getFizzlingTick() == -1;
	}

	@Override
	public boolean hurt(@NotNull DamageSource source, float amount)
	{
		return false;
	}

	@Override
	public boolean skipAttackInteraction(@NotNull Entity entity)
	{
		return true;
	}

	@Override
	public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand)
	{
		Level level = player.level();
		ItemStack stack = player.getItemInHand(hand);

		if(stack.isEmpty() && !player.isCrouching() && getVariant(level).hasContainer())
		{
			player.openMenu(new SimpleMenuProvider(
					(id, playerInv, playerEntity) ->  new ChestMenu(MenuType.GENERIC_9x1, id, playerInv, container, 1),
					Component.translatable("container.aperture_innovations.weighted_storage_cube")));
			return InteractionResult.SUCCESS;
		}
		if(stack.isEmpty() && player.isShiftKeyDown())
		{
			this.kill();

			if(!player.getAbilities().instabuild)
			{
				CubeItem cubeItem = ItemInit.CUBE.get();
				ItemStack cubeStack = ItemInit.CUBE.get().getDefaultInstance();

				cubeItem.setVariant(cubeStack, getVariantKey());
				cubeItem.setColor(cubeStack, getColor().packagedInt());
				cubeItem.setActiveColor(cubeStack, getActiveColor().packagedInt());
				cubeItem.setHullColor(cubeStack, getHullColor().packagedInt());

				ItemEntity item = new ItemEntity(player.level(), this.getX(), this.getY(), this.getZ(),
						cubeStack);
				level.addFreshEntity(item);
			}
			return InteractionResult.SUCCESS;
		}
		if(stack.getItem() instanceof ColorfulGelItem gelItem)
		{
			int color = gelItem.getColor(stack);
			setColor(color);

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.CONSUME;
	}

	private void tickLerp() {
		if (this.isControlledByLocalInstance()) {
			this.lerpSteps = 0;
			this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
		}

		if (this.lerpSteps > 0) {
			this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
			this.lerpSteps--;
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		builder.define(VARIANT, ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
				"weighted_storage_cube"));

		builder.define(ACTIVE, false);
		builder.define(COLOR, 0);
		builder.define(ACTIVE_COLOR, 0);
		builder.define(FIZZLE_TIME, -1);
		builder.define(HULL_COLOR, 0);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag)
	{
		String variantSring = tag.getString("variant");
		setVariantKey(ResourceLocation.parse(variantSring));

		this.setColor(tag.getInt("color"));
		this.setActiveColor(tag.getInt("active_color"));
		this.setHullColor(tag.getInt("hull_color"));

		this.container.fromTag(tag.getList("container", Tag.TAG_COMPOUND), this.registryAccess());
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag)
	{
		tag.putString("variant", getVariantKey().toString());

		tag.putInt("color", this.getColor().packagedInt());
		tag.putInt("active_color", this.getActiveColor().packagedInt());
		tag.putInt("hull_color", this.getHullColor().packagedInt());

		tag.put("container", this.container.createTag(this.registryAccess()));
	}

	public ResourceLocation getVariantKey()
	{
		return this.entityData.get(VARIANT);
	}

	public void setVariantKey(ResourceLocation location)
	{
		this.entityData.set(VARIANT, location);
	}

	public ClientCubeVariant getClientVariant()
	{
		CubeVariant dataVariant = getVariant(level());
		if(dataVariant == null)
			return ClientCubeVariant.DEFAULT_VARIANT;

		return ClientCubeVariants.getCubeVariant(dataVariant.getClientVariant());
	}

	public CubeVariant getVariant(Level level)
	{
		Registry<CubeVariant> registry =
				level.registryAccess().registryOrThrow(CubeVariant.REGISTRY_KEY);
		if(registry.get(getVariantKey()) == null)
			return registry.get(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID,
					"weighted_storage_cube"));
		return registry.get(getVariantKey());
	}

	public void fizzle()
	{
		this.setNoGravity(true);
		setFizzlingTick(0);
		playSound(SoundInit.FIZZLE.get(), 0.33f, 1f);
	}

	@Override
	public int getFizzlingTick()
	{
		return this.entityData.get(FIZZLE_TIME);
	}

	@Override
	public void setFizzlingTick(int tick)
	{
		this.entityData.set(FIZZLE_TIME, tick);
	}

	@Override
	public int getMaxFizzleTime()
	{
		return 40;
	}

	@Override
	public void onFinishFizzling()
	{
		super.kill();
	}

	@Override
	public void kill()
	{
		for(ItemStack stack : this.container.getItems())
		{
			ItemEntity item = new ItemEntity(level(), position().x, position().y, position().z, stack);
			level().addFreshEntity(item);
		}

		super.kill();
	}

	@Override
	public boolean canCollideWith(@NotNull Entity entity)
	{
		if(entity instanceof IFizzle fizzle)
			return fizzle.getFizzlingTick() == -1;

		return false;
	}

	@Override
	public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
		this.lerpX = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.lerpYRot = yRot;
		this.lerpXRot = xRot;
		this.lerpSteps = 10;
	}

	@Override
	public double lerpTargetX() {
		return this.lerpSteps > 0 ? this.lerpX : this.getX();
	}

	@Override
	public double lerpTargetY() {
		return this.lerpSteps > 0 ? this.lerpY : this.getY();
	}

	@Override
	public double lerpTargetZ() {
		return this.lerpSteps > 0 ? this.lerpZ : this.getZ();
	}

	@Override
	public float lerpTargetXRot() {
		return this.lerpSteps > 0 ? (float)this.lerpXRot : this.getXRot();
	}

	@Override
	public float lerpTargetYRot() {
		return this.lerpSteps > 0 ? (float)this.lerpYRot : this.getYRot();
	}

	public Color getColor()
	{
		java.awt.Color rgb = new java.awt.Color(this.entityData.get(COLOR), false);
		return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

	public Color getActiveColor()
	{
		java.awt.Color rgb = new java.awt.Color(this.entityData.get(ACTIVE_COLOR));
		return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

	public Color getHullColor()
	{
		java.awt.Color rgb = new java.awt.Color(this.entityData.get(HULL_COLOR));
		return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

	public boolean isActive()
	{
		return this.entityData.get(ACTIVE);
	}

	public void setActive(boolean active)
	{
		this.entityData.set(ACTIVE, active);
	}

	public void setColor(Color color)
	{
		this.entityData.set(COLOR, color.packagedInt());
	}

	public void setActiveColor(Color color)
	{
		this.entityData.set(ACTIVE_COLOR, color.packagedInt());
	}

	public void setHullColor(Color color)
	{
		this.entityData.set(HULL_COLOR, color.packagedInt());
	}

	public void setColor(int color)
	{
		this.entityData.set(COLOR, color);
	}

	public void setActiveColor(int color)
	{
		this.entityData.set(ACTIVE_COLOR, color);
	}

	public void setHullColor(int color)
	{
		this.entityData.set(HULL_COLOR, color);
	}

	@Override
	public @Nullable ItemStack getPickResult()
	{
		CubeItem cubeItem = ItemInit.CUBE.get();
		ItemStack stack = cubeItem.getDefaultInstance();

		cubeItem.setHullColor(stack, getHullColor().packagedInt());
		cubeItem.setColor(stack, getColor().packagedInt());
		cubeItem.setActiveColor(stack, getActiveColor().packagedInt());
		cubeItem.setVariant(stack, getVariantKey());

		return stack;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
	{

	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return geoCache;
	}

	@Override
	public List<ConfigurationProperty<?>> getConfigurationProperties(RegistryAccess registryAccess)
	{
		List<ConfigurationProperty<?>> list = new ArrayList<>();
		list.add(new ConfigurationProperty<>("hull_color", "color",
				"multi_tool.aperture_innovations.cube.hull_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				this::setHullColor, this::getHullColor));

		list.add(new ConfigurationProperty<>("active_color", "color",
				"multi_tool.aperture_innovations.cube.active_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				this::setActiveColor, this::getActiveColor));

		list.add(new ConfigurationProperty<>("idle_color", "color",
				"multi_tool.aperture_innovations.cube.idle_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				this::setColor, this::getColor));

		List<String> variants = new ArrayList<>();
		for(Map.Entry<ResourceKey<CubeVariant>, CubeVariant> entry : registryAccess
																		 .registryOrThrow(CubeVariant.REGISTRY_KEY)
																		 .entrySet())
		{
			variants.add(entry.getKey().location().toString());
		}

		list.add(new ConfigurationProperty<>("variant", "variant",
				"multi_tool.aperture_innovations.cube.variant",
				MultiToolConfigTypeInit.RESOURCE_LOCATION.get(),
				new InteractionType.ListChoice(variants, getVariantKey().toString()),
				this::setVariantKey, this::getVariantKey));

		return list;
	}
}
