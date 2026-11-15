package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundApertureCapabilityPacket(int frictionlessTime) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundApertureCapabilityPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_capability"));

	public static final StreamCodec<ByteBuf, ClientboundApertureCapabilityPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundApertureCapabilityPacket::frictionlessTime,
			ClientboundApertureCapabilityPacket::new
	);

	@Override
	public CustomPacketPayload.Type<ClientboundApertureCapabilityPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundApertureCapabilityPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.handlePlayerCapabilityPacket(packet.frictionlessTime);
			});
	}
}
