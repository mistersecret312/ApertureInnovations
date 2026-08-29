package net.mistersecret312.aperture_innovations.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundMultiToolApplyItemStackPacket(ItemStack stack, boolean mainHand) implements CustomPacketPayload
{
	public static final Type<ServerboundMultiToolApplyItemStackPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "c2s_multitool_apply_itemstack"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundMultiToolApplyItemStackPacket> STREAM_CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC, ServerboundMultiToolApplyItemStackPacket::stack,
			ByteBufCodecs.BOOL, ServerboundMultiToolApplyItemStackPacket::mainHand,
			ServerboundMultiToolApplyItemStackPacket::new
	);

	public static void handle(ServerboundMultiToolApplyItemStackPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			ServerPlayer player = (ServerPlayer) ctx.player();
			player.setItemInHand(packet.mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, packet.stack);
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}
