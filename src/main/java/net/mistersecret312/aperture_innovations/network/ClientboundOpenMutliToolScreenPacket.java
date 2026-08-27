package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ClientboundOpenMutliToolScreenPacket(boolean toolMain, boolean targetMain) implements CustomPacketPayload
{
	public static final Type<ClientboundOpenMutliToolScreenPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_item_multitool"));

	public static final StreamCodec<ByteBuf, ClientboundOpenMutliToolScreenPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, ClientboundOpenMutliToolScreenPacket::toolMain,
			ByteBufCodecs.BOOL, ClientboundOpenMutliToolScreenPacket::targetMain,
			ClientboundOpenMutliToolScreenPacket::new
	);

	@Override
	public Type<ClientboundOpenMutliToolScreenPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundOpenMutliToolScreenPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.openMultiToolItemScreen(packet.toolMain, packet.targetMain);
			});
	}
}
