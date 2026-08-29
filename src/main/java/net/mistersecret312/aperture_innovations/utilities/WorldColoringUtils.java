package net.mistersecret312.aperture_innovations.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.mixin.BlockColorAccessor;

import java.awt.*;

public class WorldColoringUtils
{
	public static int getColor(BlockColors colors, BlockState state, BlockAndTintGetter getter, BlockPos pos, int tintIndex)
	{
//		AABB box = new AABB(new Vec3(38, -57, -20), new Vec3(35, -60, -13)).inflate(1);
//		if(!box.contains(Vec3.atLowerCornerOf(pos)))
//			return -1;

		if(true)
			return -1;

		if(tintIndex == 100)
		{
			Level level = Minecraft.getInstance().level;
			if(level != null)
			{
				Color hsbColor = Color.getHSBColor(level.getTimeOfDay(1f) * 50, 1f, 1f);
				return hsbColor.getRGB();
			}
			return 0x00FF00;
		}

		return -1;
	}

	public static boolean isBlockAlreadyTinted(Block block)
	{
		BlockColors colors = BlockColors.createDefault();
		boolean vanillaTinted = block.defaultBlockState().is(Blocks.GRASS_BLOCK);


		return vanillaTinted;
	}
}
