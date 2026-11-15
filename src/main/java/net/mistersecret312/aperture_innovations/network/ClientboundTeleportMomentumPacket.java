package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;


public record ClientboundTeleportMomentumPacket(Vector3f speed) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundTeleportMomentumPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_teleport_momentum"));

	public static final StreamCodec<ByteBuf, ClientboundTeleportMomentumPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VECTOR3F, ClientboundTeleportMomentumPacket::speed,
			ClientboundTeleportMomentumPacket::new
	);

	@Override
	public CustomPacketPayload.Type<ClientboundTeleportMomentumPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundTeleportMomentumPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.handleTeleportMomentumPacket(packet.speed);
			});
	}
}
