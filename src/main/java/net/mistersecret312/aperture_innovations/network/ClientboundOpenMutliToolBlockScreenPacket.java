package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ClientboundOpenMutliToolBlockScreenPacket(BlockPos pos, boolean toolMain) implements CustomPacketPayload
{
	public static final Type<ClientboundOpenMutliToolBlockScreenPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_block_multitool"));

	public static final StreamCodec<ByteBuf, ClientboundOpenMutliToolBlockScreenPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ClientboundOpenMutliToolBlockScreenPacket::pos,
			ByteBufCodecs.BOOL, ClientboundOpenMutliToolBlockScreenPacket::toolMain,
			ClientboundOpenMutliToolBlockScreenPacket::new
	);

	@Override
	public Type<ClientboundOpenMutliToolBlockScreenPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundOpenMutliToolBlockScreenPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.openMultiToolBlockScreen(packet.pos, packet.toolMain);
			});
	}
}
