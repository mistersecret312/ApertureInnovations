package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ClientboundAntlineOutputUpdatePacket(BlockPos pos, int color, int activeColor) implements CustomPacketPayload
{
	public static final Type<ClientboundAntlineOutputUpdatePacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_antline_output_update"));

	public static final StreamCodec<ByteBuf, ClientboundAntlineOutputUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ClientboundAntlineOutputUpdatePacket::pos,
			ByteBufCodecs.INT, ClientboundAntlineOutputUpdatePacket::color,
			ByteBufCodecs.INT, ClientboundAntlineOutputUpdatePacket::activeColor,
			ClientboundAntlineOutputUpdatePacket::new
	);

	@Override
	public Type<ClientboundAntlineOutputUpdatePacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundAntlineOutputUpdatePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.handleAntlineOutputUpdate(packet.pos, packet.color, packet.activeColor);
			});
	}
}
