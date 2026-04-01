package net.mistersecret312.aperture_innovations.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.block_entities.LargeButtonBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.LargeButtonBlock;
import net.mistersecret312.aperture_innovations.init.*;
import net.mistersecret312.aperture_innovations.items.ColorfulGelItem;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WeightedStorageCubeEntity extends Entity implements GeoEntity
{
	private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(WeightedStorageCubeEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(WeightedStorageCubeEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ACTIVE_COLOR = SynchedEntityData.defineId(WeightedStorageCubeEntity.class, EntityDataSerializers.INT);

	private SimpleContainer container = new SimpleContainer(9);

	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYRot;
	private double lerpXRot;

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	public WeightedStorageCubeEntity(EntityType<?> type, Level level)
	{
		super(type, level);
	}

	public WeightedStorageCubeEntity(Level level)
	{
		super(EntityInit.WEIGHTED_STORAGE_CUBE.get(), level);
	}

	@Override
	public void tick()
	{
		super.tick();
		this.tickLerp();

		if(!isNoGravity())
		{
			this.addDeltaMovement(new Vec3(0f, -0.08f, 0f));
		}

		if(level().getBlockState(this.blockPosition()).getBlock() instanceof LargeButtonBlock button)
		{
			BlockState state = level().getBlockState(blockPosition());

			this.setActive(state.getValue(LargeButtonBlock.PRESSED));
			BlockPos masterPos = button.getMasterPos(blockPosition(), state);
			if(level().getBlockEntity(masterPos) instanceof LargeButtonBlockEntity blockEntity)
			{
				this.setActiveColor(blockEntity.activeColor);
			}
		}
		else this.setActive(false);

		float friction = 0.85f;
		if(!this.onGround())
			friction = 0.95f;

		this.setDeltaMovement(this.getDeltaMovement().multiply(friction, 1f, friction));
		this.move(MoverType.SELF, this.getDeltaMovement());
	}

	protected EntityDataAccessor<Boolean> getActiveDataAccessor()
	{
		return ACTIVE;
	}

	protected EntityDataAccessor<Integer> getColorDataAccessor()
	{
		return COLOR;
	}

	protected EntityDataAccessor<Integer> getActiveColorDataAccessor()
	{
		return ACTIVE_COLOR;
	}

	@Override
	public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source)
	{
		Level level = this.level();

		if(fallDistance > 5F)
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
		return true;
	}

	@Override
	public boolean isPickable()
	{
		return true;
	}

	@Override
	public boolean isPushable()
	{
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount)
	{
		return false;
	}

	@Override
	public boolean skipAttackInteraction(Entity entity)
	{
		return true;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand)
	{
		Level level = player.level();
		ItemStack stack = player.getItemInHand(hand);

		if(stack.isEmpty() && !player.isCrouching())
		{
			player.openMenu(new SimpleMenuProvider(
					(id, playerInv, playerEntity) ->  new ChestMenu(MenuType.GENERIC_9x1, id, playerInv, container, 1),
					getChestMenuTitle()));
			return InteractionResult.SUCCESS;
		}
		if(stack.isEmpty() && player.isCrouching()
				&& player instanceof ServerPlayer serverPlayer && serverPlayer.gameMode.getGameModeForPlayer() != GameType.ADVENTURE)
		{
			this.kill();

			if (!serverPlayer.gameMode.isCreative()) {
				ItemEntity item = new ItemEntity(player.level(), this.getX(), this.getY(), this.getZ(),
						getItemDrop());
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

	protected @NotNull ItemStack getItemDrop() {
		return ItemInit.WEIGHTED_STORAGE_CUBE.get().getDefaultInstance();
	}

	protected @NotNull MutableComponent getChestMenuTitle() {
		return Component.translatable("container.aperture_innovations.weighted_storage_cube");
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

	public void fizzle()
	{
		this.setNoGravity(true);

		//TODO - Fizzling

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
	public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
		this.lerpX = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.lerpYRot = (double)yRot;
		this.lerpXRot = (double)xRot;
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

	public int getColor()
	{
		return this.entityData.get(getColorDataAccessor());
	}

	public int getActiveColor()
	{
		return this.entityData.get(getActiveColorDataAccessor());
	}

	public boolean isActive()
	{
		return this.entityData.get(getActiveDataAccessor());
	}

	public void setActive(boolean active)
	{
		this.entityData.set(getActiveDataAccessor(), active);
	}

	public void setColor(int color)
	{
		this.entityData.set(getColorDataAccessor(), color);
	}

	public void setActiveColor(int color)
	{
		this.entityData.set(getActiveColorDataAccessor(), color);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		builder.define(ACTIVE, false);
		builder.define(COLOR, -1);
		builder.define(ACTIVE_COLOR, -1);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound)
	{
		if (compound.contains("Inventory")) {
			this.container.fromTag(compound.getList("Inventory", Tag.TAG_COMPOUND), this.registryAccess());
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound)
	{
		compound.put("Inventory", this.container.createTag(this.registryAccess()));
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
	{

	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.geoCache;
	}
}
