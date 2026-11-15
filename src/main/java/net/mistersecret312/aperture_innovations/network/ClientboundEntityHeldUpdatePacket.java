package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ClientboundEntityHeldUpdatePacket(int id, boolean held) implements CustomPacketPayload
{
	public static final Type<ClientboundEntityHeldUpdatePacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_entity_held_update"));

	public static final StreamCodec<ByteBuf, ClientboundEntityHeldUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundEntityHeldUpdatePacket::id,
			ByteBufCodecs.BOOL, ClientboundEntityHeldUpdatePacket::held,
			ClientboundEntityHeldUpdatePacket::new
	);

	@Override
	public Type<ClientboundEntityHeldUpdatePacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundEntityHeldUpdatePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.handleEntityHeldUpdate(packet.id, packet.held);
			});
	}
}
