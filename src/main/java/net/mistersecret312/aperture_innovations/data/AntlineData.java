package net.mistersecret312.aperture_innovations.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.AntlineBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.enums.ConnectionState;
import net.mistersecret312.aperture_innovations.data.antline.Antline;

import javax.annotation.Nonnull;
import java.util.*;

public class AntlineData extends SavedData
{
	private static final String FILE_NAME = ApertureInnovations.MODID + "-antlines";

	private static final String ANTLINES = "antlines";

	public HashMap<Integer, Antline> antlines = new HashMap<>();

	private MinecraftServer server;

	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();

		tag.put(ANTLINES, serializeAntlineData());

		return tag;
	}

	private CompoundTag serializeAntlineData()
	{
		CompoundTag objectsTag = new CompoundTag();

		this.antlines.forEach((uuid, pad) ->
			{
				objectsTag.put(uuid.toString(), pad.save());
			});

		return objectsTag;
	}

	private void deserialize(CompoundTag tag)
	{
		deserializeAntlineData(tag.getCompound(ANTLINES));
	}

	private void deserializeAntlineData(CompoundTag tag)
	{
		for(String key : tag.getAllKeys())
		{
			this.antlines.put(Integer.valueOf(key),
					Antline.load(tag.getCompound(key)));
		}
	}

	public int createAntline()
	{
		Random random = new Random();
		int id = 0;

		while(id == 0 || antlines.containsKey(id))
		{
			id = random.nextInt();
		}

		return id;
	}

	public Antline getLine(Integer id)
	{
		if(id == 0)
			return null;

		return this.antlines.get(id);
	}

	public void removeAntline(Level level, BlockPos pos)
	{
		update(level, pos);
		if(level.getBlockEntity(pos) instanceof AntlineBlockEntity antline)
			antlines.remove(antline.getNetworkID());
	}

	public void update(Level level, BlockPos pos)
	{
		List<AntlineBlockEntity> antlines = findConnectedParts(level, pos);

		if(!antlines.isEmpty())
		{
			int id = createAntline();
			Antline antline = new Antline(id);

			for(AntlineBlockEntity antlineBlockEntity : antlines)
			{
				this.antlines.remove(antlineBlockEntity.getNetworkID());

				if(antlineBlockEntity.hasConnectionType(ConnectionState.LINK))
					antline.links.add(antlineBlockEntity.getBlockPos());

				antline.antlineBlocks.add(antlineBlockEntity.getBlockPos());
				antlineBlockEntity.outputting = false;
				antlineBlockEntity.updateConnections();
				antlineBlockEntity.trimConnections();
				antlineBlockEntity.setNetworkID(id);
			}

			if(!antline.antlineBlocks.isEmpty())
				this.antlines.put(id, antline);
		}

		this.setDirty();
	}

	public void toggle(Level level, BlockPos pos, int signal)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof AntlineBlockEntity antline)
		{
			Antline line = this.getLine(antline.getNetworkID());
			boolean shouldActive = false;
			for(BlockPos link : line.links)
			{
				if(level.getBlockEntity(link) instanceof AntlineBlockEntity linkBE)
					if(linkBE.outputting)
						continue;

				if(!shouldActive)
				{
					shouldActive = level.getBestNeighborSignal(link) != 0;
					signal = level.getBestNeighborSignal(link);
				}
			}


			for(BlockPos outputPos : line.antlineBlocks)
			{
				BlockEntity outputEntity = level.getBlockEntity(outputPos);
				if(outputEntity instanceof AntlineBlockEntity outputAntline)
				{
					outputAntline.active = shouldActive;
					outputAntline.signal = signal;
//					if(line.links.contains(outputPos))
//						outputAntline.outputting = true;
//					if(outputPos.equals(input))
//						outputAntline.outputting = false;

					outputAntline.setChanged();
					for(Direction connectedSide : outputAntline.getConnectedSides())
					{
						ConnectionState state = outputAntline.getState(connectedSide);
						if(state.equals(ConnectionState.LINK))
						{
							BlockPos otherPos = outputAntline.getBlockPos().relative(connectedSide);
							BlockState otherState = level.getBlockState(otherPos);

							level.neighborChanged(otherState, otherPos, otherState.getBlock(), outputAntline.getBlockPos(), false);
						}
					}
				}
			}
		}
	}

	public static List<AntlineBlockEntity> findConnectedParts(Level level, BlockPos startPos)
	{
		List<AntlineBlockEntity> antlines = new ArrayList<>();

		BlockEntity startingBlockEntity = level.getBlockEntity(startPos);
		if(!(startingBlockEntity instanceof AntlineBlockEntity startingAntline))
			return antlines;

		Set<BlockPos> visited = new HashSet<>();
		Queue<BlockPos> queue = new LinkedList<>();

		queue.add(startPos);

		for(int i = 0; !queue.isEmpty(); i++)
		{
			BlockPos pos = queue.remove();
			visited.add(pos);

			if(level.getBlockEntity(pos) instanceof AntlineBlockEntity antline)
			{
				if(startingAntline.color == antline.color
				&& startingAntline.activeColor == antline.activeColor)
					antlines.add(antline);

				Direction normal = antline.getNormal();
				for(Direction side : antline.getConnectedSides())
				{
					BlockPos relativePos = pos;
					ConnectionState state = antline.getState(side);
					if(state.equals(ConnectionState.SIDE) || state.equals(ConnectionState.LINK))
						relativePos = pos.relative(side);

					if(state.equals(ConnectionState.SIDE_UP))
						relativePos = pos.relative(side).relative(normal);

					if(state.equals(ConnectionState.UP))
						relativePos = pos.relative(normal);

					if(state.equals(ConnectionState.DOWN))
						relativePos = pos.relative(side).relative(normal.getOpposite());

					if(!visited.contains(relativePos))
						queue.add(relativePos);
				}

			}
		}

		return antlines;
	}


	@Override
	public void setDirty()
	{
		super.setDirty();
	}

	public AntlineData(MinecraftServer server)
	{
		this.server = server;
	}

	public static AntlineData create(MinecraftServer server)
	{
		return new AntlineData(server);
	}

	public static AntlineData load(MinecraftServer server, CompoundTag tag)
	{
		AntlineData data = create(server);

		data.server = server;
		data.deserialize(tag);

		return data;
	}

	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
	{
		tag = serialize();

		return tag;
	}

	@Nonnull
	public static AntlineData get(Level level)
	{
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");

		return AntlineData.get(level.getServer());
	}

	public static Factory<AntlineData> dataFactory(MinecraftServer server)
	{
		return new Factory<>(() -> create(server), (tag, provider) -> load(server, tag));
	}

	@Nonnull
	public static AntlineData get(MinecraftServer server)
	{
		DimensionDataStorage storage = server.overworld().getDataStorage();

		return storage.computeIfAbsent(dataFactory(server), FILE_NAME);
	}
}
