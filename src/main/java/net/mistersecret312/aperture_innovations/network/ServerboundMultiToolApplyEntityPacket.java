package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.init.MultiToolConfigTypeInit;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationType;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ServerboundMultiToolApplyEntityPacket(UUID uuid, String name, ConfigurationType<?> dataType, Object data) implements CustomPacketPayload
{
	public static final Type<ServerboundMultiToolApplyEntityPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "c2s_multitool_apply_entity"));

	public static final StreamCodec<ByteBuf, ServerboundMultiToolApplyEntityPacket> STREAM_CODEC = new StreamCodec<ByteBuf, ServerboundMultiToolApplyEntityPacket>() {

		@SuppressWarnings("unchecked")
		@Override
		public ServerboundMultiToolApplyEntityPacket decode(@NotNull ByteBuf buffer)
		{
			UUID uuid = UUIDUtil.STREAM_CODEC.decode(buffer);
			String name = Utf8String.read(buffer, 32767);
			String typeString = Utf8String.read(buffer, 32767);
			ConfigurationType<Object> type = (ConfigurationType<Object>) MultiToolConfigTypeInit.REGISTRY.get(ResourceLocation.parse(typeString));

			Object value = null;
			if(type != null)
				value = type.codec().decode(buffer);

			return new ServerboundMultiToolApplyEntityPacket(uuid, name, type, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void encode(@NotNull ByteBuf buffer, ServerboundMultiToolApplyEntityPacket packet)
		{
			UUIDUtil.STREAM_CODEC.encode(buffer, packet.uuid);
			Utf8String.write(buffer, packet.name, 32767);

			ResourceLocation type = MultiToolConfigTypeInit.REGISTRY.getKey(packet.dataType());
			if(type == null)
				type = ResourceLocation.fromNamespaceAndPath("aperture_innovations", "empty");

			Utf8String.write(buffer, type.toString(), 32767);

			StreamCodec<ByteBuf, Object> codec = (StreamCodec<ByteBuf, Object>) packet.dataType().codec();
			codec.encode(buffer, packet.data);
		}
	};

	public static void handle(ServerboundMultiToolApplyEntityPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			ServerPlayer player = (ServerPlayer) ctx.player();
			ServerLevel level = (ServerLevel) player.level();

			Entity entity = level.getEntity(packet.uuid);
			if(entity instanceof IHaveConfiguration configuration)
			{
				for(ConfigurationProperty<?> property : configuration.getConfigurationProperties(entity.registryAccess()))
				{
					if(property.getName().equals(packet.name) && property.getType().equals(packet.dataType))
					{
						property.setUnsafe(packet.data);
					}
				}

			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}
