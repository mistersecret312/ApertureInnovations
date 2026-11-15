package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.sounds.SoundAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ClientboundPortalAmbientSoundPacket(UUID linkID, boolean isPrimary, boolean stop) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundPortalAmbientSoundPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_ambient_sound"));

	public static final StreamCodec<ByteBuf, ClientboundPortalAmbientSoundPacket> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, ClientboundPortalAmbientSoundPacket::linkID,
			ByteBufCodecs.BOOL, ClientboundPortalAmbientSoundPacket::isPrimary,
			ByteBufCodecs.BOOL, ClientboundPortalAmbientSoundPacket::stop,
			ClientboundPortalAmbientSoundPacket::new
	);

	@Override
	public CustomPacketPayload.Type<ClientboundPortalAmbientSoundPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundPortalAmbientSoundPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				SoundAccess.playPortalAmbient(packet.linkID, packet.isPrimary, packet.stop);
			});
	}
}
