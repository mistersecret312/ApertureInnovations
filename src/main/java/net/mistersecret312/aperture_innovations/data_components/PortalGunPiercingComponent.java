package net.mistersecret312.aperture_innovations.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record PortalGunPiercingComponent(ItemStack stack)
{
	public static final Codec<PortalGunPiercingComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ItemStack.CODEC.fieldOf("piercing_item").forGetter(PortalGunPiercingComponent::stack)
	).apply(instance, PortalGunPiercingComponent::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PortalGunPiercingComponent> STREAM_CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC,
			PortalGunPiercingComponent::stack,
			PortalGunPiercingComponent::new
	);
}
