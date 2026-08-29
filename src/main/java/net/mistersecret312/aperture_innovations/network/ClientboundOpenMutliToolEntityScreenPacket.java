package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ClientboundOpenMutliToolEntityScreenPacket(int id, boolean toolMain) implements CustomPacketPayload
{
	public static final Type<ClientboundOpenMutliToolEntityScreenPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_entity_multitool"));

	public static final StreamCodec<ByteBuf, ClientboundOpenMutliToolEntityScreenPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundOpenMutliToolEntityScreenPacket::id,
			ByteBufCodecs.BOOL, ClientboundOpenMutliToolEntityScreenPacket::toolMain,
			ClientboundOpenMutliToolEntityScreenPacket::new
	);

	@Override
	public Type<ClientboundOpenMutliToolEntityScreenPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundOpenMutliToolEntityScreenPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.openMultiToolEntityScreen(packet.id, packet.toolMain);
			});
	}
}
