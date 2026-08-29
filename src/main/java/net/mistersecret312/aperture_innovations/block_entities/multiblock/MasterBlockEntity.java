package net.mistersecret312.aperture_innovations.block_entities.multiblock;

import mekanism.common.util.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.aperture_innovations.blocks.multiblock.DummyBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MasterBlockEntity extends BlockEntity
{
	public List<BlockPos> dummies = new ArrayList<>();
	public boolean beingRemoved = false;

	public MasterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
	{
		super(type, pos, blockState);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);

		ListTag dummiesTag = new ListTag();
		for(BlockPos dummy : dummies)
		{
			Tag posTag = NbtUtils.writeBlockPos(dummy);
			dummiesTag.add(posTag);
		}
		tag.put("dummies", dummiesTag);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		ListTag dummiesTag = tag.getList("dummies", Tag.TAG_INT_ARRAY);
		for(Tag dummyTag : dummiesTag)
		{
			if(dummyTag instanceof IntArrayTag array)
			{
				int[] aint = array.getAsIntArray();
				Optional<BlockPos> pos = aint.length == 3 ? Optional.of(
						new BlockPos(aint[0], aint[1], aint[2])) : Optional.empty();

				pos.ifPresent(blockPos -> dummies.add(blockPos));
			}
		}
	}

	public void addDummy(BlockPos pos)
	{
		this.dummies.add(pos);
		setChanged();
	}

	public void clearDummies()
	{
		this.beingRemoved = true;
		if(level == null)
			return;

		for(BlockPos dummy : this.dummies)
		{
			BlockPos dummyPos = dummy.offset(this.getBlockPos());
			BlockState state = level.getBlockState(dummyPos);
			if(state.getBlock() instanceof DummyBlock)
				level.setBlock(dummyPos, Blocks.AIR.defaultBlockState(), 3);
		}
		this.dummies.clear();
		this.beingRemoved = false;
		setChanged();
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
	public void setChanged()
	{
		super.setChanged();
		if(level != null)
			level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), 3, 512);
	}
}
