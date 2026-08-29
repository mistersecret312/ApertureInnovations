package net.mistersecret312.aperture_innovations.network;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.LargeButtonBlockEntity;
import net.mistersecret312.aperture_innovations.capabilities.ApertureEnergy;
import net.mistersecret312.aperture_innovations.config.PortalGunConfig;
import net.mistersecret312.aperture_innovations.data.PortalLinkData;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.data.portal.Portal;
import net.mistersecret312.aperture_innovations.data.portal.PortalLink;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.init.TagInit;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public record ServerboundLargeButtonLoadPacket(BlockPos pos) implements CustomPacketPayload
{
	public static final Type<ServerboundLargeButtonLoadPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "c2s_load_large_button"));

	public static final StreamCodec<ByteBuf, ServerboundLargeButtonLoadPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ServerboundLargeButtonLoadPacket::pos,
			ServerboundLargeButtonLoadPacket::new
	);

	@Override
	public Type<ServerboundLargeButtonLoadPacket> type()
	{
		return TYPE;
	}

	public static void handle(ServerboundLargeButtonLoadPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			ServerPlayer player = (ServerPlayer) ctx.player();
			Level level = player.level();
			BlockEntity blockEntity = level.getBlockEntity(packet.pos);
			if(blockEntity instanceof LargeButtonBlockEntity button)
			{
				button.updatePressed = true;
			}
		});
	}

	public static Pair<Vec3, Vec2> positionPortal(Level level, Vec3 originalPos, Direction direction, Direction facing, UUID id, boolean isPrimary)
	{
		Vec3 position = originalPos;
		Vec2 rotation;

		float xRot = 0;
		float yRot = direction.toYRot();
		if(direction.equals(Direction.UP))
		{
			xRot = -90;
			yRot = facing.toYRot()+180;
		}
		if(direction.equals(Direction.DOWN))
		{
			xRot = 90;
			yRot = facing.toYRot();
		}

		AABB placementBox = PortalUtilities.getPortalPlacementBox(position, xRot, yRot);
		AABB portalBox = PortalUtilities.getPortalBoundingBox(position, xRot, yRot);

		Portal self;
		if(level.isClientSide())
		{
			ClientPortalLink link =  PortalUtilities.getPortalLinks().get(id);
			if(isPrimary)
				self = link.getPrimaryPortal();
			else self = link.getSecondaryPortal();
		}
		else
		{
			PortalLink link = PortalUtilities.getPortalLinks(level).get(id);
			if(isPrimary)
				self = link.getPrimaryPortal();
			else self = link.getSecondaryPortal();
		}
		Pair<UUID, Boolean> closestPortalPair;
		if(self.getPosition() == null)
			closestPortalPair = PortalUtilities.getClosestPortal(level, position, id, isPrimary);
		else closestPortalPair = PortalUtilities.getClosestPortal(level, self);

		Portal closestPortal;
		if(level.isClientSide() && closestPortalPair.getFirst() != null)
		{
			ClientPortalLink link = PortalUtilities.getPortalLinks().get(closestPortalPair.getFirst());
			if(closestPortalPair.getSecond())
				closestPortal = link.getPrimaryPortal();
			else closestPortal = link.getSecondaryPortal();
		}
		else if(closestPortalPair.getFirst() != null)
		{
			PortalLink link = PortalUtilities.getPortalLinks(level).get(closestPortalPair.getFirst());
			if(closestPortalPair.getSecond())
				closestPortal = link.getPrimaryPortal();
			else closestPortal = link.getSecondaryPortal();
		}
		else
			closestPortal = null;

		AtomicReference<VoxelShape> placementShape = new AtomicReference<>(
				Shapes.create(placementBox.inflate(0.025)));
		AtomicReference<VoxelShape> bumpingAirShape = new AtomicReference<>(Shapes.create(placementBox.inflate(0.025)));
		AABB blockBumpAABB = placementBox.inflate(0.025).move(direction.step().mul(0.05f));
		AtomicReference<VoxelShape> bumpingBlockShape = new AtomicReference<>(Shapes.create(blockBumpAABB));

		if(closestPortal != null)
		{
			VoxelShape shape = Shapes.create(PortalUtilities.getPortalPlacementBox(closestPortal.getPosition(),
					closestPortal.getXRotation(), closestPortal.getYRotation()).inflate(0.05));

			if(!placementShape.get().isEmpty())
				placementShape.set(Shapes.join(placementShape.get(), shape, BooleanOp.ONLY_FIRST));

			if(!bumpingAirShape.get().isEmpty())
				bumpingAirShape.set(Shapes.join(bumpingAirShape.get(), shape, BooleanOp.ONLY_FIRST));

			if(!bumpingBlockShape.get().isEmpty())
				bumpingBlockShape.set(Shapes.join(bumpingBlockShape.get(), shape, BooleanOp.ONLY_FIRST));
		}

		BlockPos.betweenClosedStream(portalBox.inflate(0.025)).forEach(pos ->
			{
				BlockState state = level.getBlockState(pos);
				if(!state.isAir())
				{
					VoxelShape shape = state.getCollisionShape(level, pos)
											.move(pos.getX(), pos.getY(), pos.getZ());

					if(state.is(TagInit.Blocks.IMPORTALABLE) || (PortalGunConfig.use_portalable_tag.get() && !state.is(TagInit.Blocks.PORTALABLE)))
						shape = Shapes.create(shape.bounds().inflate(0.025));

					if(!placementShape.get().isEmpty())
						placementShape.set(Shapes.join(placementShape.get(), shape, BooleanOp.ONLY_FIRST));

					if(!bumpingAirShape.get().isEmpty())
						bumpingAirShape.set(Shapes.join(bumpingAirShape.get(), shape, BooleanOp.ONLY_FIRST));

					if(!bumpingBlockShape.get().isEmpty())
						bumpingBlockShape.set(Shapes.join(bumpingBlockShape.get(), shape, BooleanOp.ONLY_FIRST));

				}
			});
		List<AABB> placements = placementShape.get().toAabbs();
		if(!placementShape.get().isEmpty() && placements.size() == 1)
		{
			AABB placementAABB = placementBox.inflate(0.025);

			boolean wall = xRot == 0;

			if(wall)
			{
				if(direction.getAxis().equals(Direction.Axis.X))
				{
					placementAABB = placementAABB.setMinX(placementShape.get().bounds().minX);
					placementAABB = placementAABB.setMaxX(placementShape.get().bounds().maxX);
				}

				if(direction.getAxis().equals(Direction.Axis.Z))
				{
					placementAABB = placementAABB.setMinZ(placementShape.get().bounds().minZ);
					placementAABB = placementAABB.setMaxZ(placementShape.get().bounds().maxZ);
				}
			} else
			{
				placementAABB = placementAABB.setMinY(placementShape.get().bounds().minY);
				placementAABB = placementAABB.setMaxY(placementShape.get().bounds().maxY);
			}

			bumpingAirShape.set(Shapes.join(Shapes.create(placementAABB), placementShape.get(),
					BooleanOp.ONLY_FIRST));
			bumpingBlockShape.set(Shapes.join(Shapes.create(placementAABB), placementShape.get(),
					BooleanOp.ONLY_FIRST));
		}

		List<AABB> aabbList = bumpingAirShape.get().toAabbs();
		List<AABB> aabbListBlock = bumpingBlockShape.get().toAabbs();
		aabbList.addAll(aabbListBlock);
		for(int i = 0; i < aabbList.size(); i++)
		{
			AABB aabb = aabbList.get(i);
			boolean smthn = BlockPos.betweenClosedStream(aabb).anyMatch(pos ->
				{
					VoxelShape shape = level.getBlockState(pos).getCollisionShape(level, pos);
					for(AABB shapeAabb : shape.toAabbs())
					{
						if(shapeAabb.intersects(aabb))
							return true;
					}
					return false;
				});

			if(!aabbListBlock.contains(aabb))
			{
				if(smthn || (aabb.getXsize() < 0.05D && direction.getAxis().equals(Direction.Axis.X))
						   || (aabb.getZsize() < 0.05D && direction.getAxis().equals(Direction.Axis.Z))
						   || (aabb.getYsize() < 0.05D && direction.getAxis().equals(Direction.Axis.Y)))
					continue;
			}

			Vec3i normal = new Vec3i(direction.getNormal().getX() == 0 ? 1 : 0,
					direction.getNormal().getY() == 0 ? 1 : 0,
					direction.getNormal().getZ() == 0 ? 1 : 0);

			Vec3 offsetToCenter = aabb.getCenter().vectorTo(position).multiply(2, 2, 2);
			offsetToCenter = offsetToCenter.multiply(normal.getX(), normal.getY(), normal.getZ());

			Direction nearest = Direction.getNearest(offsetToCenter);
			Vector3f sizes = new Vec3(aabb.getXsize(), aabb.getYsize(), aabb.getZsize()).toVector3f();
			sizes.mul(nearest.step());

			position = position.add(new Vec3(sizes));
			break;
		}

		rotation = new Vec2(xRot, yRot);
		return Pair.of(position, rotation);
	}

	public static boolean consumeEnergy(ItemStack stack, Player player)
	{
		@Nullable IEnergyStorage capability = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if(capability != null && capability instanceof ApertureEnergy energy)
		{
			long requiredEnergy = PortalGunConfig.portal_gun_shoot_consumption.get();
			if(energy.getTrueEnergyStored() > requiredEnergy)
			{
				energy.extractLongEnergy(requiredEnergy, false);
				return true;
			}
			else
			{
				player.displayClientMessage(Component.translatable("item.aperture_innovations.portal_gun.not_enough_energy").withStyle(ChatFormatting.DARK_RED), true);
				return false;
			}
		}
		return false;
	}
}
