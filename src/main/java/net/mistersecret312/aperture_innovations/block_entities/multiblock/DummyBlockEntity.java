package net.mistersecret312.aperture_innovations.block_entities.multiblock;

import mekanism.common.util.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.aperture_innovations.init.BlockEntityInit;

public class DummyBlockEntity extends BlockEntity
{
	private BlockPos masterPos = BlockPos.ZERO;

	public DummyBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(BlockEntityInit.DUMMY.get(), pos, blockState);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);

		int[] masterPos = {this.masterPos.getX(), this.masterPos.getY(), this.masterPos.getZ()};
		tag.putIntArray("master", masterPos);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		int[] masterArray = tag.getIntArray("master");
		this.masterPos = new BlockPos(masterArray[0], masterArray[1], masterArray[2]);
	}

	public BlockPos getMasterPos()
	{
		return masterPos.offset(this.getBlockPos());
	}

	public BlockPos getOffset()
	{
		return masterPos;
	}

	public void setMasterPos(BlockPos masterPos)
	{
		this.masterPos = masterPos;
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
