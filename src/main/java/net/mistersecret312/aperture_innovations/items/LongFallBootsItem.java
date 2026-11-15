package net.mistersecret312.aperture_innovations.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.capabilities.ApertureEnergy;
import net.mistersecret312.aperture_innovations.config.LongFallBootsConfig;
import net.mistersecret312.aperture_innovations.init.DataComponentInit;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class LongFallBootsItem extends ArmorItem
{
	public static final String ENERGY = "Energy";

	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "textures/models/armor/long_fall_boots.png");

	public LongFallBootsItem(Holder<ArmorMaterial> material, Type type, Properties properties)
	{
		super(material, type, properties);
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}

	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return LongFallBootsConfig.long_fall_boots_use_energy.get() && getEnergy(stack) != getCapacity();
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getEnergy(stack) / getCapacity());
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		return new Color(0, 200, 255).getRGB();
	}

	@Override
	public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer)
	{
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components,
								TooltipFlag tooltipFlag)
	{
		if(LongFallBootsConfig.long_fall_boots_use_energy.get())
		{
			components.add(Component.translatable("item.aperture_innovations.long_fall_boots.energy").append(
					ApertureEnergy.energyToString(getEnergy(stack), getCapacity())).withStyle(ChatFormatting.DARK_RED));
		}
	}

	public static long getEnergy(ItemStack stack)
	{
		@Nullable IEnergyStorage cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if(cap != null && cap instanceof ApertureEnergy energy)
			return energy.getTrueEnergyStored();

		return 0;
	}

	@Override
	public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
													  ArmorMaterial.Layer layer, boolean innerModel)
	{
		return TEXTURE;
	}

	public long getCapacity()
	{
		return LongFallBootsConfig.long_fall_boots_max_energy_stored.get();
	}

	public long getTransfer()
	{
		return LongFallBootsConfig.long_fall_boots_use_energy.get() ? 1000L : 0L;
	}

	public static class Energy extends ApertureEnergy.Item
	{
		public Energy(ItemStack stack)
		{
			super(stack, LongFallBootsConfig.long_fall_boots_max_energy_stored.get(),
					1000L, 1000L);
		}

		@Override
		public long receiveLongEnergy(long maxReceive, boolean simulate)
		{
			return super.receiveLongEnergy(maxReceive, simulate);
		}

		@Override
		public long extractLongEnergy(long maxExtract, boolean simulate)
		{
			return super.extractLongEnergy(maxExtract, simulate);
		}

		@Override
		public long maxReceive()
		{
			if(stack.getItem() instanceof LongFallBootsItem boots)
				return boots.getTransfer();

			return 0;
		}

		@Override
		public long maxExtract()
		{
			if(stack.getItem() instanceof LongFallBootsItem boots)
				return boots.getTransfer();

			return 0;
		}

		@Override
		public long loadEnergy(ItemStack stack)
		{
			return stack.getOrDefault(DataComponentInit.ENERGY, LongFallBootsConfig.long_fall_boots_max_energy_stored.get());
		}

		@Override
		public long getTrueMaxEnergyStored()
		{
			if(stack.getItem() instanceof LongFallBootsItem boots)
				return boots.getCapacity();

			return 0;
		}

		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			stack.set(DataComponentInit.ENERGY, this.energy);
		}
	}
}

