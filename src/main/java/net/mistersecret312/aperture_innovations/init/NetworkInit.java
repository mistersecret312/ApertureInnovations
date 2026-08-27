package net.mistersecret312.aperture_innovations.init;

import net.mistersecret312.aperture_innovations.network.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkInit
{
	@SubscribeEvent
	public static void registerPackets(final RegisterPayloadHandlersEvent event)
	{
		final PayloadRegistrar registrar = event.registrar("1");

		//Server

		registrar.playToServer(
				ServerboundOpenPortalPacket.TYPE,
				ServerboundOpenPortalPacket.STREAM_CODEC,
				ServerboundOpenPortalPacket::handle
		);

		registrar.playToServer(
				ServerboundResetPortalLinkPacket.TYPE,
				ServerboundResetPortalLinkPacket.STREAM_CODEC,
				ServerboundResetPortalLinkPacket::handle
		);

		registrar.playToServer(
				ServerboundPickUpEntityPacket.TYPE,
				ServerboundPickUpEntityPacket.STREAM_CODEC,
				ServerboundPickUpEntityPacket::handle
		);

		registrar.playToServer(
				ServerboundMultiToolApplyBlockEntityPacket.TYPE,
				ServerboundMultiToolApplyBlockEntityPacket.STREAM_CODEC,
				ServerboundMultiToolApplyBlockEntityPacket::handle
		);

		registrar.playToServer(
				ServerboundMultiToolApplyEntityPacket.TYPE,
				ServerboundMultiToolApplyEntityPacket.STREAM_CODEC,
				ServerboundMultiToolApplyEntityPacket::handle
		);

		registrar.playToServer(
				ServerboundMultiToolApplyItemStackPacket.TYPE,
				ServerboundMultiToolApplyItemStackPacket.STREAM_CODEC,
				ServerboundMultiToolApplyItemStackPacket::handle
		);

		registrar.playToServer(
				ServerboundLargeButtonLoadPacket.TYPE,
				ServerboundLargeButtonLoadPacket.STREAM_CODEC,
				ServerboundLargeButtonLoadPacket::handle
		);

		//Client

		registrar.playToClient(
				ClientBoundPortalSyncPacket.TYPE,
				ClientBoundPortalSyncPacket.STREAM_CODEC,
				ClientBoundPortalSyncPacket::handle
		);

		registrar.playToClient(
				ClientboundTeleportMomentumPacket.TYPE,
				ClientboundTeleportMomentumPacket.STREAM_CODEC,
				ClientboundTeleportMomentumPacket::handle
		);

		registrar.playToClient(
				ClientboundApertureCapabilityPacket.TYPE,
				ClientboundApertureCapabilityPacket.STREAM_CODEC,
				ClientboundApertureCapabilityPacket::handle);

		registrar.playToClient(
				ClientboundPortalAmbientSoundPacket.TYPE,
				ClientboundPortalAmbientSoundPacket.STREAM_CODEC,
				ClientboundPortalAmbientSoundPacket::handle
		);

		registrar.playToClient(
				ClientboundPortalSoundsPacket.GunActivate.TYPE,
				ClientboundPortalSoundsPacket.GunActivate.STREAM_CODEC,
				ClientboundPortalSoundsPacket.GunActivate::handle
		);

		registrar.playToClient(
				ClientboundPortalSoundsPacket.EnterPortal.TYPE,
				ClientboundPortalSoundsPacket.EnterPortal.STREAM_CODEC,
				ClientboundPortalSoundsPacket.EnterPortal::handle
		);

		registrar.playToClient(
				ClientboundPortalSoundsPacket.FizzlePortal.TYPE,
				ClientboundPortalSoundsPacket.FizzlePortal.STREAM_CODEC,
				ClientboundPortalSoundsPacket.FizzlePortal::handle
		);

		registrar.playToClient(
				ClientboundPortalSoundsPacket.OpenPortal.TYPE,
				ClientboundPortalSoundsPacket.OpenPortal.STREAM_CODEC,
				ClientboundPortalSoundsPacket.OpenPortal::handle
		);

		registrar.playToClient(
				ClientboundPortalSoundsPacket.ShootPortal.TYPE,
				ClientboundPortalSoundsPacket.ShootPortal.STREAM_CODEC,
				ClientboundPortalSoundsPacket.ShootPortal::handle
		);

		registrar.playToClient(
				ClientboundPortalSoundsPacket.InvalidSurface.TYPE,
				ClientboundPortalSoundsPacket.InvalidSurface.STREAM_CODEC,
				ClientboundPortalSoundsPacket.InvalidSurface::handle
		);

		registrar.playToClient(
				ClientboundPortalSoundsPacket.ResetPortal.TYPE,
				ClientboundPortalSoundsPacket.ResetPortal.STREAM_CODEC,
				ClientboundPortalSoundsPacket.ResetPortal::handle
		);

		registrar.playToClient(
				ClientboundEntityPortalLerpPacket.TYPE,
				ClientboundEntityPortalLerpPacket.STREAM_CODEC,
				ClientboundEntityPortalLerpPacket::handle
		);

		registrar.playToClient(
				ClientboundAntlineUpdatePacket.TYPE,
				ClientboundAntlineUpdatePacket.STREAM_CODEC,
				ClientboundAntlineUpdatePacket::handle
		);

		registrar.playToClient(
				ClientboundAntlineOutputUpdatePacket.TYPE,
				ClientboundAntlineOutputUpdatePacket.STREAM_CODEC,
				ClientboundAntlineOutputUpdatePacket::handle
		);

		registrar.playToClient(
				ClientboundEntityHeldUpdatePacket.TYPE,
				ClientboundEntityHeldUpdatePacket.STREAM_CODEC,
				ClientboundEntityHeldUpdatePacket::handle
		);

		registrar.playToClient(
				ClientboundGunZapSoundPacket.TYPE,
				ClientboundGunZapSoundPacket.STREAM_CODEC,
				ClientboundGunZapSoundPacket::handle
		);

		registrar.playToClient(
				ClientboundFizzleParticlesPacket.TYPE,
				ClientboundFizzleParticlesPacket.STREAM_CODEC,
				ClientboundFizzleParticlesPacket::handle
		);

		registrar.playToClient(
				ClientboundOpenMutliToolScreenPacket.TYPE,
				ClientboundOpenMutliToolScreenPacket.STREAM_CODEC,
				ClientboundOpenMutliToolScreenPacket::handle
		);
		registrar.playToClient(
				ClientboundOpenMutliToolBlockScreenPacket.TYPE,
				ClientboundOpenMutliToolBlockScreenPacket.STREAM_CODEC,
				ClientboundOpenMutliToolBlockScreenPacket::handle
		);
		registrar.playToClient(
				ClientboundOpenMutliToolEntityScreenPacket.TYPE,
				ClientboundOpenMutliToolEntityScreenPacket.STREAM_CODEC,
				ClientboundOpenMutliToolEntityScreenPacket::handle
		);
	}
}
