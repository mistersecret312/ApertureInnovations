package net.mistersecret312.aperture_innovations.recipes;

import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.mistersecret312.aperture_innovations.init.RecipeInit;
import net.mistersecret312.aperture_innovations.items.ColorfulGelItem;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;

import java.util.List;
import java.util.UUID;

public class PortalGunPiercingRecipe extends CustomRecipe
{
	public PortalGunPiercingRecipe(CraftingBookCategory category)
	{
		super(category);
	}

	@Override
	public boolean matches(CraftingInput container, Level level)
	{
		ItemStack portalGun = ItemStack.EMPTY;
		ItemStack otherItem = ItemStack.EMPTY;
		int nonEmptyCount = 0;

		for (int i = 0; i < container.width(); i++)
			for (int j = 0; j < container.height(); j++)
			{
				ItemStack stack = container.getItem(i, j);
				if(!stack.isEmpty())
				{
					nonEmptyCount++;
					if(stack.getItem() instanceof PortalGunItem)
					{
						if(!portalGun.isEmpty())
							return false;
						portalGun = stack;
					} else
					{
						if(!otherItem.isEmpty())
							return false;
						otherItem = stack;
					}
				}
			}

		if(nonEmptyCount == 2 && !portalGun.isEmpty() && !otherItem.isEmpty())
			return true;

		if (nonEmptyCount == 1 && !portalGun.isEmpty() && portalGun.getItem() instanceof PortalGunItem gunItem)
			return !gunItem.getPiercingItem(portalGun).isEmpty();

		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput container, HolderLookup.Provider registryAccess)
	{
		ItemStack portalGun = ItemStack.EMPTY;
		ItemStack otherItem = ItemStack.EMPTY;
		int nonEmptyCount = 0;

		for (int i = 0; i < container.width(); i++)
			for (int j = 0; j < container.height(); j++)
			{
				ItemStack stack = container.getItem(i, j);
				if (!stack.isEmpty())
				{
					nonEmptyCount++;
					if (stack.getItem() instanceof PortalGunItem)
					{
						if (!portalGun.isEmpty())
							return ItemStack.EMPTY;
						portalGun = stack.copy();
					}
					else
					{
						if (!otherItem.isEmpty())
							return ItemStack.EMPTY;
						otherItem = stack.copy();
					}
				}
			}

		if (portalGun.isEmpty() || !(portalGun.getItem() instanceof PortalGunItem gunItem) || (!gunItem.getPiercingItem(portalGun).isEmpty() && !otherItem.isEmpty()))
			return ItemStack.EMPTY;

		if (!otherItem.isEmpty() && gunItem.getPiercingItem(portalGun).isEmpty())
			gunItem.setPiercingItem(portalGun, otherItem);
		else
			gunItem.setPiercingItem(portalGun, ItemStack.EMPTY);

		return portalGun;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput container)
	{
		NonNullList<ItemStack> remainders = NonNullList.withSize(container.size(), ItemStack.EMPTY);
		for (int slot = 0; slot < container.size(); slot++)
		{
			ItemStack stack = container.getItem(slot);
			if (!stack.isEmpty() && stack.getItem() instanceof PortalGunItem gunItem)
			{
				boolean hasOtherItems = false;
				for (int otherSlot = 0; otherSlot < container.size(); otherSlot++)
					if (otherSlot != slot && !container.getItem(otherSlot).isEmpty())
					{
						hasOtherItems = true;
						break;
					}

				if (!hasOtherItems)
				{
					ItemStack piercingItem = gunItem.getPiercingItem(stack);
					if (!piercingItem.isEmpty())
						remainders.set(slot, piercingItem.copy());
				}
			}
		}

		return remainders;
	}

	@Override
	public boolean canCraftInDimensions(int x, int y)
	{
		return x+y >= 1;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return RecipeInit.GUN_PIERCING.get();
	}
}
