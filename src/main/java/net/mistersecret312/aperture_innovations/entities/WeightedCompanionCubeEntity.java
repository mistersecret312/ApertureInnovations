package net.mistersecret312.aperture_innovations.entities;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.mistersecret312.aperture_innovations.init.EntityInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import org.jetbrains.annotations.NotNull;

public class WeightedCompanionCubeEntity extends WeightedStorageCubeEntity
{
	private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(WeightedCompanionCubeEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(WeightedCompanionCubeEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ACTIVE_COLOR = SynchedEntityData.defineId(WeightedCompanionCubeEntity.class, EntityDataSerializers.INT);


	public WeightedCompanionCubeEntity(EntityType<?> type, Level level) {
		super(type, level);
	}

	public WeightedCompanionCubeEntity(Level level) {
		super(EntityInit.WEIGHTED_COMPANION_CUBE.get(), level);
	}

	@Override
	protected EntityDataAccessor<Boolean> getActiveDataAccessor() {
		return ACTIVE;
	}

	@Override
	protected EntityDataAccessor<Integer> getColorDataAccessor() {
		return COLOR;
	}

	@Override
	protected EntityDataAccessor<Integer> getActiveColorDataAccessor() {
		return ACTIVE_COLOR;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ACTIVE, false);
		builder.define(COLOR, -1);
		builder.define(ACTIVE_COLOR, -1);
	}

	@Override
	protected @NotNull ItemStack getItemDrop() {
		return ItemInit.WEIGHTED_COMPANION_CUBE.get().getDefaultInstance();
	}

	@Override
	protected @NotNull MutableComponent getChestMenuTitle() {
		return Component.translatable("container.aperture_innovations.weighted_companion_cube");
	}
}
