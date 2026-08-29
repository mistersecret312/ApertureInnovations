package net.mistersecret312.aperture_innovations.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.mistersecret312.aperture_innovations.blocks.multiblock.MasterBlock;

public class MultiBlockItem extends BlockItem
{
	public MultiBlockItem(Block block, Properties properties)
	{
		super(block, properties);
	}

	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state)
	{
		if(state.getBlock() instanceof MasterBlock master)
		{
			AABB box = master.getFullShape(context.getLevel(), context.getClickedPos(), state).bounds().move(context.getClickedPos());
			boolean hasBlocks = BlockPos.betweenClosedStream(box).anyMatch(pos -> {
				BlockState otherState = context.getLevel().getBlockState(pos);
				if(!otherState.isAir())
					return !otherState.canBeReplaced();

				return false;
			});

			boolean hasEntities = !context.getLevel().getEntitiesOfClass(Entity.class, box).isEmpty();

			if(hasBlocks || hasEntities)
				return false;
		}

		return super.canPlace(context, state);
	}
}
