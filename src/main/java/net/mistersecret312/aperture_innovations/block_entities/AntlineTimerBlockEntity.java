package net.mistersecret312.aperture_innovations.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.aperture_innovations.blocks.AntlineTimerBlock;
import net.mistersecret312.aperture_innovations.init.BlockEntityInit;
import net.mistersecret312.aperture_innovations.init.SoundInit;

public class AntlineTimerBlockEntity extends BlockEntity
{
	public int color = -1;
	public int activeColor = -1;

	public int signal = 0;

	public int maxTime = 160;
	public int time = maxTime;

	public int soundTime = 20;

	public AntlineTimerBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(BlockEntityInit.TIMER.get(), pos, blockState);
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

		tag.putInt("color", this.color);
		tag.putInt("active_color", this.activeColor);
		tag.putInt("signal", this.signal);

		tag.putInt("time", this.time);
		tag.putInt("max_time", this.maxTime);
		tag.putInt("sound_time", this.soundTime);

		super.saveAdditional(tag, registries);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		this.color = tag.getInt("color");
		this.activeColor = tag.getInt("active_color");

		this.signal = tag.getInt("signal");

		this.time = tag.getInt("time");
		this.maxTime = tag.getInt("max_time");
		this.soundTime = tag.getInt("sound_time");
	}

	public static void tick(Level level, BlockPos pos, BlockState blockState,
							AntlineTimerBlockEntity timer)
	{
		if(timer.isActive())
		{
			if(timer.time <= timer.maxTime && timer.time > 0)
			{
				if(!level.isClientSide() && timer.soundTime == 20)
				{
					level.playSound(null, timer.getBlockPos(), SoundInit.TIMER_TICK.get(), SoundSource.BLOCKS,
							0.25f, 1f);
				}
				timer.time--;
				if(!level.isClientSide())
				{
					timer.soundTime++;
					if(timer.soundTime > 20)
						timer.soundTime = 0;

					BlockPos relativePos = pos.relative(blockState.getValue(AntlineTimerBlock.NORMAL).getOpposite());
					level.updateNeighborsAt(relativePos, blockState.getBlock());
					level.scheduleTick(relativePos, blockState.getBlock(), 2);
				}
			}
			else
			{
				timer.signal = 0;
				timer.time = timer.maxTime;
				timer.soundTime = 20;
				level.playSound(null, pos, SoundInit.PEDESTAL_BUTTON_UP.get(), SoundSource.BLOCKS,
						0.25f, 1f);
				level.setBlock(pos, blockState.setValue(AntlineTimerBlock.ACTIVE, false), 16 | 2);
				BlockPos relativePos = pos.relative(blockState.getValue(AntlineTimerBlock.NORMAL).getOpposite());
				level.updateNeighborsAt(relativePos, blockState.getBlock());
				level.scheduleTick(relativePos, blockState.getBlock(), 2);
			}
		}
	}

	public boolean isActive()
	{
		return getBlockState().getValue(AntlineTimerBlock.ACTIVE);
	}

	public Direction getNormal()
	{
		return getBlockState().getValue(AntlineTimerBlock.NORMAL);
	}

	public Direction getFacing()
	{
		return getBlockState().getValue(AntlineTimerBlock.FACING);
	}

}
