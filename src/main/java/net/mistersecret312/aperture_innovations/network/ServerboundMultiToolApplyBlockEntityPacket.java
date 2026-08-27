package net.mistersecret312.aperture_innovations.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.init.MultiToolConfigTypeInit;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationType;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ServerboundMultiToolApplyBlockEntityPacket(BlockPos pos, String name, ConfigurationType<?> dataType, Object data) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundMultiToolApplyBlockEntityPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "c2s_multitool_apply_block_entity"));

	public static final StreamCodec<ByteBuf, ServerboundMultiToolApplyBlockEntityPacket> STREAM_CODEC = new StreamCodec<ByteBuf, ServerboundMultiToolApplyBlockEntityPacket>() {

		@SuppressWarnings("unchecked")
		@Override
		public ServerboundMultiToolApplyBlockEntityPacket decode(@NotNull ByteBuf buffer)
		{
			BlockPos pos = FriendlyByteBuf.readBlockPos(buffer);
			String name = Utf8String.read(buffer, 32767);
			String typeString = Utf8String.read(buffer, 32767);
			ConfigurationType<Object> type = (ConfigurationType<Object>) MultiToolConfigTypeInit.REGISTRY.get(ResourceLocation.parse(typeString));

			Object value = null;
			if(type != null)
				value = type.codec().decode(buffer);

			return new ServerboundMultiToolApplyBlockEntityPacket(pos, name, type, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void encode(@NotNull ByteBuf buffer, ServerboundMultiToolApplyBlockEntityPacket packet)
		{
			FriendlyByteBuf.writeBlockPos(buffer, packet.pos);
			Utf8String.write(buffer, packet.name, 32767);

			ResourceLocation type = MultiToolConfigTypeInit.REGISTRY.getKey(packet.dataType());
			if(type == null)
				type = ResourceLocation.fromNamespaceAndPath("aperture_innovations", "empty");

			Utf8String.write(buffer, type.toString(), 32767);

			StreamCodec<ByteBuf, Object> codec = (StreamCodec<ByteBuf, Object>) packet.dataType().codec();
			codec.encode(buffer, packet.data);
		}
	};

	public static void handle(ServerboundMultiToolApplyBlockEntityPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			ServerPlayer player = (ServerPlayer) ctx.player();
			ServerLevel level = (ServerLevel) player.level();

			BlockEntity blockEntity = level.getBlockEntity(packet.pos);
			if(blockEntity instanceof IHaveConfiguration configuration)
			{
				for(ConfigurationProperty<?> property : configuration.getConfigurationProperties(blockEntity.getLevel().registryAccess()))
				{
					if(property.getName().equals(packet.name) && property.getType().equals(packet.dataType))
					{
						property.setUnsafe(packet.data);
					}
				}

				blockEntity.setChanged();

				BlockState state = blockEntity.getBlockState();
				level.sendBlockUpdated(packet.pos, state, state, 3);
			}
			if(blockEntity != null)
				blockEntity.setChanged();
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}
