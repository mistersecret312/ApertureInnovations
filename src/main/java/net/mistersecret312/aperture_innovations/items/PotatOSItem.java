package net.mistersecret312.aperture_innovations.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class PotatOSItem extends Item implements IPortalGunPiercingAction
{

	public PotatOSItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public void action(Player player, Level level, ItemStack gunStack, BlockHitResult blockHitResult)
	{

	}
}
