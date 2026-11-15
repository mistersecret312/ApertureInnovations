package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.sounds.SoundAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public abstract class ClientboundPortalSoundsPacket
{
	public static record OpenPortal(UUID linkID, boolean isPrimary) implements CustomPacketPayload
	{
		public static final CustomPacketPayload.Type<ClientboundPortalSoundsPacket.OpenPortal> TYPE = new CustomPacketPayload.Type<>(
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_open_portal"));

		public static final StreamCodec<ByteBuf, ClientboundPortalSoundsPacket.OpenPortal> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, ClientboundPortalSoundsPacket.OpenPortal::linkID,
				ByteBufCodecs.BOOL, ClientboundPortalSoundsPacket.OpenPortal::isPrimary,
				ClientboundPortalSoundsPacket.OpenPortal::new
		);

		public static void handle(ClientboundPortalSoundsPacket.OpenPortal packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
				{
					SoundAccess.playOpenPortalSound(packet.linkID, packet.isPrimary);
				});
		}

		@Override
		public Type<OpenPortal> type()
		{
			return TYPE;
		}
	}

	public static record EnterPortal(UUID linkID, boolean isPrimary) implements CustomPacketPayload
	{
		public static final CustomPacketPayload.Type<ClientboundPortalSoundsPacket.EnterPortal> TYPE = new CustomPacketPayload.Type<>(
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_enter_portal"));

		public static final StreamCodec<ByteBuf, ClientboundPortalSoundsPacket.EnterPortal> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, ClientboundPortalSoundsPacket.EnterPortal::linkID,
				ByteBufCodecs.BOOL, ClientboundPortalSoundsPacket.EnterPortal::isPrimary,
				ClientboundPortalSoundsPacket.EnterPortal::new
		);

		public static void handle(ClientboundPortalSoundsPacket.EnterPortal packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
				{
					SoundAccess.playEnterPortalSound(packet.linkID, packet.isPrimary);
				});
		}

		@Override
		public Type<EnterPortal> type()
		{
			return TYPE;
		}
	}

	public static record InvalidSurface(UUID linkID, BlockPos pos, boolean isPrimary) implements CustomPacketPayload
	{
		public static final CustomPacketPayload.Type<ClientboundPortalSoundsPacket.InvalidSurface> TYPE = new CustomPacketPayload.Type<>(
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_invalid_surface"));

		public static final StreamCodec<ByteBuf, ClientboundPortalSoundsPacket.InvalidSurface> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, ClientboundPortalSoundsPacket.InvalidSurface::linkID,
				BlockPos.STREAM_CODEC, ClientboundPortalSoundsPacket.InvalidSurface::pos,
				ByteBufCodecs.BOOL, ClientboundPortalSoundsPacket.InvalidSurface::isPrimary,
				ClientboundPortalSoundsPacket.InvalidSurface::new
		);

		public static void handle(ClientboundPortalSoundsPacket.InvalidSurface packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
				{
					SoundAccess.playInvalidSurfaceSound(packet.linkID, packet.pos, packet.isPrimary);
				});
		}

		@Override
		public Type<InvalidSurface> type()
		{
			return TYPE;
		}
	}

	public static record FizzlePortal(UUID linkID, boolean isPrimary) implements CustomPacketPayload
	{
		public static final CustomPacketPayload.Type<ClientboundPortalSoundsPacket.FizzlePortal> TYPE = new CustomPacketPayload.Type<>(
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_fizzle_portal"));

		public static final StreamCodec<ByteBuf, ClientboundPortalSoundsPacket.FizzlePortal> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, ClientboundPortalSoundsPacket.FizzlePortal::linkID,
				ByteBufCodecs.BOOL, ClientboundPortalSoundsPacket.FizzlePortal::isPrimary,
				ClientboundPortalSoundsPacket.FizzlePortal::new
		);

		public static void handle(ClientboundPortalSoundsPacket.FizzlePortal packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
				{
					SoundAccess.playFizzlePortalSound(packet.linkID, packet.isPrimary);
				});
		}

		@Override
		public Type<FizzlePortal> type()
		{
			return TYPE;
		}
	}

	public static record GunActivate(UUID linkID, BlockPos pos) implements CustomPacketPayload
	{
		public static final CustomPacketPayload.Type<ClientboundPortalSoundsPacket.GunActivate> TYPE = new CustomPacketPayload.Type<>(
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_gun_activate"));

		public static final StreamCodec<ByteBuf, ClientboundPortalSoundsPacket.GunActivate> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, ClientboundPortalSoundsPacket.GunActivate::linkID,
				BlockPos.STREAM_CODEC, ClientboundPortalSoundsPacket.GunActivate::pos,
				ClientboundPortalSoundsPacket.GunActivate::new
		);

		public static void handle(ClientboundPortalSoundsPacket.GunActivate packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
				{
					SoundAccess.playGunActivateSound(packet.linkID, packet.pos, true);
				});
		}

		@Override
		public CustomPacketPayload.Type<GunActivate> type()
		{
			return TYPE;
		}
	}

	public static record ShootPortal(UUID linkID, BlockPos pos, boolean isPrimary) implements CustomPacketPayload
	{
		public static final CustomPacketPayload.Type<ClientboundPortalSoundsPacket.ShootPortal> TYPE = new CustomPacketPayload.Type<>(
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_shoot_portal"));

		public static final StreamCodec<ByteBuf, ClientboundPortalSoundsPacket.ShootPortal> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, ClientboundPortalSoundsPacket.ShootPortal::linkID,
				BlockPos.STREAM_CODEC, ClientboundPortalSoundsPacket.ShootPortal::pos,
				ByteBufCodecs.BOOL, ClientboundPortalSoundsPacket.ShootPortal::isPrimary,
				ClientboundPortalSoundsPacket.ShootPortal::new
		);

		public static void handle(ClientboundPortalSoundsPacket.ShootPortal packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
				{
					SoundAccess.playShootPortalSound(packet.linkID, packet.pos, packet.isPrimary);
				});
		}

		@Override
		public CustomPacketPayload.Type<ShootPortal> type()
		{
			return TYPE;
		}
	}

	public static record ResetPortal(UUID linkID, BlockPos pos) implements CustomPacketPayload
	{
		public static final CustomPacketPayload.Type<ClientboundPortalSoundsPacket.ResetPortal> TYPE = new CustomPacketPayload.Type<>(
				ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "s2c_reset_portal"));

		public static final StreamCodec<ByteBuf, ClientboundPortalSoundsPacket.ResetPortal> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, ClientboundPortalSoundsPacket.ResetPortal::linkID,
				BlockPos.STREAM_CODEC, ClientboundPortalSoundsPacket.ResetPortal::pos,
				ClientboundPortalSoundsPacket.ResetPortal::new
		);

		public static void handle(ClientboundPortalSoundsPacket.ResetPortal packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
				{
					SoundAccess.playResetPortalSound(packet.linkID, packet.pos, true);
				});
		}

		@Override
		public CustomPacketPayload.Type<ResetPortal> type()
		{
			return TYPE;
		}
	}
}
