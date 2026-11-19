package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalTransitHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundPortalTransitPacket(Vec3 entrancePos, Vec3 exitPos,
        float targetYaw, float targetPitch, boolean isInterdimensional) implements CustomPacketPayload
{
    public static final Type<ClientboundPortalTransitPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "portal_transit"));

    public static final StreamCodec<ByteBuf, ClientboundPortalTransitPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeDouble(packet.entrancePos.x);
                buf.writeDouble(packet.entrancePos.y);
                buf.writeDouble(packet.entrancePos.z);
                buf.writeDouble(packet.exitPos.x);
                buf.writeDouble(packet.exitPos.y);
                buf.writeDouble(packet.exitPos.z);
                buf.writeFloat(packet.targetYaw);
                buf.writeFloat(packet.targetPitch);
                buf.writeBoolean(packet.isInterdimensional);
            },
            buf -> new ClientboundPortalTransitPacket(
                    new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                    new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean()
            )
    );

    public static void handle(ClientboundPortalTransitPacket packet, IPayloadContext context) {
        context.enqueueWork(
                () -> {
//                    ClientPortalTransitHandler.startTransit(
//                    packet.entrancePos(),
//                    packet.exitPos(),
//                    packet.targetYaw(),
//                    packet.targetPitch());
        });
    }

    @Override
    public Type<? extends ClientboundPortalTransitPacket> type() {
        return TYPE;
    }
}