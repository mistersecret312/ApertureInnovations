package net.mistersecret312.aperture_innovations.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.aperture_innovations.client.renderer.item.PortalGunRenderer;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public class PortalGunRenderProperties implements IClientItemExtensions
{
	public static final PortalGunRenderProperties INSTANCE = new PortalGunRenderProperties();
	private PortalGunRenderer renderer;

	private PortalGunRenderProperties()
	{

	}

	

	@Override
	public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand,
													  ItemStack itemStack)
	{
		if(entityLiving.getItemInHand(hand).getItem() instanceof PortalGunItem)
			return HumanoidModel.ArmPose.THROW_SPEAR;

		return HumanoidModel.ArmPose.EMPTY;
	}

	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer()
	{
		if(renderer == null)
			renderer = new PortalGunRenderer();
		return renderer;
	}
}
