package net.mistersecret312.aperture_innovations.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.LevelReader;

import java.util.List;

public class ColorfulGelItem extends Item
{

	public ColorfulGelItem(Properties pProperties)
	{
		super(pProperties);
	}
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components,
								TooltipFlag flag)
	{
		components.add(Component.translatable("item.aperture_innovations.colorful_gel.tooltip").withStyle(
				ChatFormatting.DARK_PURPLE));

		int gelColor = getColor(stack);
		if(gelColor != -1) components.add(Component.translatable("item.aperture_innovations.colorful_gel.color",
				Integer.toHexString(gelColor).toUpperCase()).withStyle(style -> style.withColor(gelColor)));
	}

	@Override
	public int getMaxStackSize(ItemStack stack)
	{
		return getColor(stack) != -1 ? 16 : 64;
	}

	public void setColor(ItemStack stack, int color)
	{
		stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
	}

	public int getColor(ItemStack stack)
	{
		DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
		if(color == null)
			return -1;
		if(color.showInTooltip())
			stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color.rgb(), false));

		return color.rgb();
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player)
	{
		return true;
	}
}
