package net.mistersecret312.aperture_innovations.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.aperture_innovations.blocks.AntlineBlock;
import net.mistersecret312.aperture_innovations.blocks.enums.ConnectionState;
import net.mistersecret312.aperture_innovations.data.AntlineData;
import net.mistersecret312.aperture_innovations.data.antline.Antline;
import net.mistersecret312.aperture_innovations.init.BlockEntityInit;
import net.mistersecret312.aperture_innovations.init.TagInit;
import net.mistersecret312.aperture_innovations.network.ClientboundAntlineUpdatePacket;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class AntlineBlockEntity extends BlockEntity
{
	public int color = -1;
	public int activeColor = -1;

	public ConnectionState north = ConnectionState.NONE;
	public ConnectionState south = ConnectionState.NONE;
	public ConnectionState west = ConnectionState.NONE;
	public ConnectionState east = ConnectionState.NONE;
	public ConnectionState up = ConnectionState.NONE;
	public ConnectionState down = ConnectionState.NONE;

	public boolean active;
	public boolean outputting;

	public int networkId = 0;
	private Antline antline = null;

	public int signal = 0;
	public BlockState fakeState = null;

	public AntlineBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(BlockEntityInit.ANTLINE.get(), pos, blockState);
	}

	@Override
	public void setChanged()
	{
		if(getLevel() != null && !getLevel().isClientSide())
		{
			PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) getLevel(), new ChunkPos(getBlockPos()),
					new ClientboundAntlineUpdatePacket(getBlockPos(), active, color, activeColor));
		}
		super.setChanged();
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
	public void onLoad()
	{
		super.onLoad();
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putInt("network_id", this.networkId);

		tag.putInt("color", this.color);
		tag.putInt("active_color", this.activeColor);

		tag.putBoolean("active", this.active);
		tag.putBoolean("outputting", this.outputting);
		tag.putInt("signal", this.signal);

		tag.putString("north", this.north.name);
		tag.putString("south", this.south.name);
		tag.putString("west", this.west.name);
		tag.putString("east", this.east.name);
		tag.putString("up", this.up.name);
		tag.putString("down", this.down.name);

		if(this.fakeState != null)
			tag.put("fake_state", NbtUtils.writeBlockState(this.fakeState));

		super.saveAdditional(tag, registries);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		this.networkId = tag.getInt("network_id");

		this.color = tag.getInt("color");
		this.activeColor = tag.getInt("active_color");

		this.active = tag.getBoolean("active");
		this.outputting = tag.getBoolean("outputting");
		this.signal = tag.getInt("signal");

		this.north = ConnectionState.fromString(tag.getString("north"));
		this.south = ConnectionState.fromString(tag.getString("south"));
		this.west = ConnectionState.fromString(tag.getString("west"));
		this.east = ConnectionState.fromString(tag.getString("east"));
		this.up = ConnectionState.fromString(tag.getString("up"));
		this.down = ConnectionState.fromString(tag.getString("down"));

		if(tag.contains("fake_state"))
			this.fakeState = NbtUtils.readBlockState(registries.lookupOrThrow(BuiltInRegistries.BLOCK.key()),
					tag.getCompound("fake_state"));
	}

	public BlockState getFakeState()
	{
		if(this.getLevel() != null && !this.getLevel().isClientSide())
		{
			this.getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}
		return fakeState;
	}

	public void setFakeState(BlockState fakeState)
	{
		this.fakeState = fakeState;
		this.setChanged();
	}

	public boolean isActive()
	{
		return active;
	}

	public Direction getNormal()
	{
		return getBlockState().getValue(AntlineBlock.NORMAL);
	}

	public ConnectionState getState(Direction direction)
	{
		return switch(direction)
		{
			case UP -> up;
			case DOWN -> down;
			case NORTH -> north;
			case SOUTH -> south;
			case EAST -> east;
			case WEST -> west;
		};
	}

	public List<Direction> getConnectedSides()
	{
		List<Direction> connections = new ArrayList<>();
		for(Direction direction : Direction.values())
		{
			ConnectionState state = getState(direction);
			if(!state.equals(ConnectionState.NONE))
				connections.add(direction);
		}

		return connections;
	}

	public boolean hasConnectionType(ConnectionState filter)
	{
		for(Direction direction : Direction.values())
		{
			ConnectionState state = getState(direction);
			if(state.equals(filter)) return true;
		}

		return false;
	}

	public void setState(Direction direction, ConnectionState state)
	{
		switch(direction)
		{
			case SOUTH -> this.south = state;
			case NORTH -> this.north = state;
			case EAST -> this.east = state;
			case WEST -> this.west = state;
			case UP -> this.up = state;
			case DOWN -> this.down = state;
		}

		setChanged();
	}

	public int getNetworkID()
	{
		return networkId;
	}

	public void setNetworkID(int networkId)
	{
		this.networkId = networkId;
		this.antline = null;

		setChanged();
	}

	public Antline getAntline()
	{
		if(antline != null) return antline;

		if(networkId == 0) return null;

		antline = AntlineData.get(getLevel()).getLine(networkId);
		return antline;
	}

	public void updateConnections()
	{
		Level level = this.getLevel();
		Direction normal = this.getNormal();

		if(level == null)
			return;

		for(Direction direction : Direction.values())
		{
			if(direction.getAxis().equals(normal.getAxis()))
				continue;

			ConnectionState state;
			BlockPos relativePos = getBlockPos().relative(direction);

			state = validateBlock(level, relativePos, direction);
			if(state.equals(ConnectionState.NONE))
			{
				state = validateBlock(level, relativePos.relative(normal.getOpposite()), direction);
				if(state.equals(ConnectionState.NONE))
				{
					state = validateBlock(level, relativePos.relative(normal), direction);

					if(!state.equals(ConnectionState.NONE) &&
							   level.getBlockEntity(relativePos.relative(normal)) instanceof AntlineBlockEntity antlineBlockEntity)
					{
						if(antlineBlockEntity.getNormal().equals(this.getNormal()))
						{
							this.setState(direction, ConnectionState.SIDE_UP);
							continue;
						}
						else continue;
					}

					if(state.equals(ConnectionState.NONE))
					{
						if(level.getBlockEntity(getBlockPos().relative(normal)) instanceof AntlineBlockEntity antlineBlockEntity)
						{
							if(antlineBlockEntity.getNormal().getOpposite().equals(direction))
							{
								state = validateBlock(level, getBlockPos().relative(normal), direction);
								if(!state.equals(ConnectionState.NONE))
									state = ConnectionState.UP;
							}
						}
					}
				}
				else state = ConnectionState.DOWN;
			}

			this.setState(direction, state);
		}
	}

	public ConnectionState validateBlock(Level level, BlockPos relativePos, Direction direction)
	{
		BlockState blockState = level.getBlockState(relativePos);
		BlockEntity blockEntity = level.getBlockEntity(relativePos);

		if(blockEntity instanceof AntlineBlockEntity antline)
		{
			if(antline.color != this.color || antline.activeColor != this.activeColor)
				return ConnectionState.NONE;

			return ConnectionState.SIDE;
		}

		if(blockState.is(TagInit.Blocks.CONNECTS_TO_ANTLINE))
			return ConnectionState.LINK;

		if(blockState.isSignalSource())
			return ConnectionState.LINK;

		return ConnectionState.NONE;
	}

	public boolean checkValidBlock(Level level, BlockPos relativePos, Direction direction)
	{
		BlockState blockState = level.getBlockState(relativePos);
		BlockEntity blockEntity = level.getBlockEntity(relativePos);

		if(blockEntity instanceof AntlineBlockEntity antline)
			return antline.color == this.color && antline.activeColor == this.activeColor;

		if(blockState.is(TagInit.Blocks.CONNECTS_TO_ANTLINE))
			return true;

		return blockState.canRedstoneConnectTo(level, relativePos, direction);
	}

	public void trimConnections()
	{
		Level level = getLevel();
		BlockPos pos = getBlockPos();
		Direction normal = getNormal();

		if(level == null)
			return;

		for(Direction direction : getConnectedSides())
		{
			boolean valid = false;
			ConnectionState state = getState(direction);

			if(state.equals(ConnectionState.SIDE) || state.equals(ConnectionState.LINK))
				valid = checkValidBlock(level, pos.relative(direction), direction);

			if(state.equals(ConnectionState.SIDE_UP))
				valid = checkValidBlock(level, pos.relative(direction).relative(normal), direction);

			if(state.equals(ConnectionState.UP))
				valid = checkValidBlock(level, pos.relative(normal), direction);

			if(state.equals(ConnectionState.DOWN))
				valid = checkValidBlock(level, pos.relative(direction).relative(normal.getOpposite()), direction);

			if(!valid)
				setState(direction, ConnectionState.NONE);
		}
	}
}
