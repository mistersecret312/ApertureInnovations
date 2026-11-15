package net.mistersecret312.aperture_innovations.data.antline;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;
import java.util.Set;

public class Antline
{
	public int id;

	public Set<BlockPos> antlineBlocks;
	public Set<BlockPos> links;

	public Antline(int id)
	{
		this.id = id;

		this.antlineBlocks = new HashSet<>();
		this.links = new HashSet<>();
	}

	public static Antline load(CompoundTag tag)
	{
		int id = tag.getInt("id");

		Antline antline = new Antline(id);

		CompoundTag blocks = tag.getCompound("blocks");
		CompoundTag links = tag.getCompound("links");

		for(String key : blocks.getAllKeys())
		{
			int[] xyz = blocks.getIntArray(key);
			antline.antlineBlocks.add(new BlockPos(xyz[0], xyz[1], xyz[2]));
		}

		for(String key : links.getAllKeys())
		{
			int[] xyz = blocks.getIntArray(key);
			antline.links.add(new BlockPos(xyz[0], xyz[1], xyz[2]));
		}

		return antline;
	}

	public CompoundTag save()
	{
		CompoundTag tag = new CompoundTag();

		tag.putInt("id", this.id);

		int i = 0;
		CompoundTag blocks = new CompoundTag();
		for(BlockPos pos : antlineBlocks)
		{
			blocks.putIntArray(String.valueOf(i), new int[]{pos.getX(), pos.getY(), pos.getZ()});
			i++;
		}
		tag.put("blocks", blocks);

		i = 0;
		CompoundTag linkTag = new CompoundTag();
		for(BlockPos link : links)
		{
			linkTag.putIntArray(String.valueOf(i), new int[]{link.getX(), link.getY(), link.getZ()});
			i++;
		}
		tag.put("links", linkTag);

		return tag;
	}
}
