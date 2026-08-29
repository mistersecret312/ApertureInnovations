package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundClearPortalCachePacket() implements CustomPacketPayload
{

	public static final CustomPacketPayload.Type<ClientboundClearPortalCachePacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_clear_cache"));

	public static final StreamCodec<ByteBuf, ClientboundClearPortalCachePacket> STREAM_CODEC = new StreamCodec<>()
	{
		@Override
		public ClientboundClearPortalCachePacket decode(ByteBuf buffer)
		{
			return new ClientboundClearPortalCachePacket();
		}

		@Override
		public void encode(ByteBuf buffer, ClientboundClearPortalCachePacket value)
		{

		}
	};

	@Override
	public CustomPacketPayload.Type<ClientboundClearPortalCachePacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundClearPortalCachePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.handleClearPortalCache();
			});
	}

}
