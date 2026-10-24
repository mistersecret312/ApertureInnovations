package net.mistersecret312.aperture_innovations.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public interface IPortalGunPiercingAction
{
	void action(Player player, Level level, ItemStack gunStack, BlockHitResult blockHitResult);
}
