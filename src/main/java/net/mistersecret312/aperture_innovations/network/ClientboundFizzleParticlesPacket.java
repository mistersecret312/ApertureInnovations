package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.sounds.SoundAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundFizzleParticlesPacket(int id) implements CustomPacketPayload
{
	public static final Type<ClientboundFizzleParticlesPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_fizzle_particles"));

	public static final StreamCodec<ByteBuf, ClientboundFizzleParticlesPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundFizzleParticlesPacket::id,
			ClientboundFizzleParticlesPacket::new
	);

	@Override
	public Type<ClientboundFizzleParticlesPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundFizzleParticlesPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.fizzleParticles(packet.id);
			});
	}
}
