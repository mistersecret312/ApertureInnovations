package net.mistersecret312.aperture_innovations.data.portal;

import com.mojang.datafixers.util.Pair;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.capabilities.ApertureCapability;
import net.mistersecret312.aperture_innovations.config.PortalGunConfig;
import net.mistersecret312.aperture_innovations.data.PortalLinkData;
import net.mistersecret312.aperture_innovations.datapack.PortalGunVariant;
import net.mistersecret312.aperture_innovations.init.AdvancementInit;
import net.mistersecret312.aperture_innovations.init.StatisticsInit;
import net.mistersecret312.aperture_innovations.init.TagInit;
import net.mistersecret312.aperture_innovations.neo_events.PortalTravelEvent;
import net.mistersecret312.aperture_innovations.network.ClientboundEntityPortalLerpPacket;
import net.mistersecret312.aperture_innovations.network.ClientboundPortalSoundsPacket;
import net.mistersecret312.aperture_innovations.network.ClientboundPortalTransitPacket;
import net.mistersecret312.aperture_innovations.network.ClientboundTeleportMomentumPacket;
import net.mistersecret312.aperture_innovations.utilities.CoordUtil;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PortalLink
{
	public UUID linkID;

	private Portal primaryPortal;
	private Portal secondaryPortal;

	public ResourceLocation variantKey = null;

	public PortalLink(UUID linkID, ResourceLocation variantKey)
	{
		this.linkID = linkID;
		this.variantKey = variantKey;

		this.primaryPortal = new Portal();
		this.secondaryPortal = new Portal();
	}

	public PortalLink(UUID linkID, Portal primaryPortal, Portal secondaryPortal,
					  ResourceLocation variantKey)
	{
		this.linkID = linkID;

		this.primaryPortal = primaryPortal;
		this.secondaryPortal = secondaryPortal;

		this.variantKey = variantKey;
	}

	public void createPrimaryPortal(Level level, Vec3 pos, ResourceKey<Level> dimension, Direction direction, Direction facing)
	{
		float xRot = 0;
		float yRot = direction.toYRot();
		if(direction.equals(Direction.UP))
		{
			xRot = -90;
			yRot = facing.toYRot()+(facing.getAxis().equals(Direction.Axis.X) ? 0 : 180);
		}
		if(direction.equals(Direction.DOWN))
		{
			xRot = 90;
			yRot = facing.toYRot();
		}

		this.primaryPortal.setPosition(pos);
		this.primaryPortal.setYRotation(yRot);
		this.primaryPortal.setXRotation(xRot);
		this.primaryPortal.setDirection(direction);
		this.primaryPortal.setFacing(facing);
		this.primaryPortal.setDimension(dimension);
		this.primaryPortal.setMoonshot(false);
		this.primaryPortal.setReplaceShapes(PortalUtilities.calculatePortalVoxels(level, pos, xRot, yRot));

		ServerLevel portalLevel = level.getServer().getLevel(dimension);

		if(portalLevel != null)
		{
			int x = (int) pos.x;
			int z = (int) pos.z;
			PacketDistributor.sendToPlayersTrackingChunk(portalLevel, new ChunkPos(x, z),
					new ClientboundPortalSoundsPacket.OpenPortal(linkID, true));

		}

		PortalLinkData.get(level).setDirty(linkID, true);
	}

	public void createSecondaryPortal(Level level, Vec3 pos, ResourceKey<Level> dimension,
									  Direction direction, Direction facing)
	{
		float xRot = 0;
		float yRot = direction.toYRot();
		if(direction.equals(Direction.UP))
		{
			xRot = -90;
			yRot = facing.toYRot()+(facing.getAxis().equals(Direction.Axis.X) ? 0 : 180);
		}
		if(direction.equals(Direction.DOWN))
		{
			xRot = 90;
			yRot = facing.toYRot();
		}

		this.secondaryPortal.setPosition(pos);
		this.secondaryPortal.setYRotation(yRot);
		this.secondaryPortal.setXRotation(xRot);
		this.secondaryPortal.setDirection(direction);
		this.secondaryPortal.setFacing(facing);
		this.secondaryPortal.setDimension(dimension);
		this.secondaryPortal.setMoonshot(false);
		this.secondaryPortal.setReplaceShapes(PortalUtilities.calculatePortalVoxels(level, pos, xRot, yRot));

		ServerLevel portalLevel = level.getServer().getLevel(dimension);

		if(portalLevel != null)
		{
			int x = (int) pos.x;
			int z = (int) pos.z;
			PacketDistributor.sendToPlayersTrackingChunk(portalLevel, new ChunkPos(x, z),
					new ClientboundPortalSoundsPacket.OpenPortal(linkID, false));
		}

		PortalLinkData.get(level).setDirty(linkID, false);
	}

	public boolean checkForValidity(Level level, Vec3 position, float xRot, float yRot, Direction direction, UUID id, boolean isPrimary)
	{
		AABB portalBox = PortalUtilities.getPortalBoundingBox(position, xRot, yRot).inflate(0.025);
		AABB placementBox = PortalUtilities.getPortalPlacementBox(position, xRot, yRot).inflate(0.025);

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

		if(self.equals(closestPortal))
			closestPortal = null;

		VoxelShape placementShape = Shapes.create(placementBox);

		AABB portalBoxCopy = PortalUtilities.getPortalPlacementBox(position, xRot, yRot).inflate(0.025).deflate(0.025)
											.move(Vec3.directionFromRotation(xRot+(direction.getAxis().isVertical() ? 180 : 0), yRot+180)
													  .multiply(0.15, 0.15, 0.15));

		AtomicBoolean hasAir = new AtomicBoolean();
		BlockPos.betweenClosedStream(portalBoxCopy).forEach(pos ->
			{
				BlockState state = level.getBlockState(pos);
				if(state.isAir())
					hasAir.set(true);
			});

		if(hasAir.get())
			return false;

		AtomicReference<VoxelShape> placementReference = new AtomicReference<>(placementShape);
		Portal finalClosestPortal = closestPortal;
		BlockPos.betweenClosedStream(portalBox).forEach(pos ->
			{
				BlockState state = level.getBlockState(pos);
				if(finalClosestPortal != null)
				{
					VoxelShape shape = Shapes.create(PortalUtilities.getPortalPlacementBox(finalClosestPortal.getPosition(),
							finalClosestPortal.getXRotation(), finalClosestPortal.getYRotation()).inflate(0.05));

					if(!placementReference.get().isEmpty())
						placementReference.set(Shapes.join(placementReference.get(), shape, BooleanOp.ONLY_FIRST));
				}
				if(!state.isAir())
				{
					VoxelShape shape = state.getCollisionShape(level, pos).move(pos.getX(), pos.getY(), pos.getZ());

					if(!shape.isEmpty())
					{
						if(state.is(TagInit.Blocks.IMPORTALABLE) || (PortalGunConfig.use_portalable_tag.get() && !state.is(TagInit.Blocks.PORTALABLE)))
							shape = Shapes.create(shape.bounds().inflate(0.025));

						if(!placementReference.get().isEmpty())
							placementReference.set(Shapes.join(placementReference.get(), shape, BooleanOp.ONLY_FIRST));
					}
				}
			});

		placementShape = placementReference.get();
		List<AABB> shapeList = placementShape.toAabbs();

		if(!shapeList.isEmpty())
		{
			AABB firstPart = shapeList.getFirst();

			boolean wall = direction.getAxis().isHorizontal();

			boolean equal = false;
			if(!wall)
			{
				equal = firstPart.maxX == placementBox.maxX && firstPart.minX == placementBox.minX
						&& firstPart.maxZ == placementBox.maxZ && firstPart.minZ == placementBox.minZ;
			}
			if(wall)
			{
				if(direction.getAxis().equals(Direction.Axis.Z))
				{
					equal = firstPart.maxX == placementBox.maxX && firstPart.minX == placementBox.minX
							&& firstPart.maxY == placementBox.maxY && firstPart.minY == placementBox.minY;

				}
				if(direction.getAxis().equals(Direction.Axis.X))
				{
					equal = firstPart.maxY == placementBox.maxY && firstPart.minY == placementBox.minY
							&& firstPart.maxZ == placementBox.maxZ && firstPart.minZ == placementBox.minZ;
				}
			}

			return equal && shapeList.size() == 1;
		}

		return false;
	}

	public void updateColors(Level level, int primaryPortalColor, int secondaryPortalColor)
	{
		if(this.primaryPortal.getColor() != primaryPortalColor)
		{
			this.primaryPortal.setColor(primaryPortalColor);
			PortalLinkData.get(level).setDirty(linkID, true);
		}

		if(this.secondaryPortal.getColor() != secondaryPortalColor)
		{
			this.secondaryPortal.setColor(secondaryPortalColor);
			PortalLinkData.get(level).setDirty(linkID, false);
		}
	}

	public void updateVariant(Level level, ResourceLocation variantKey)
	{
		if(!this.variantKey.equals(variantKey))
		{
			this.variantKey = variantKey;
			PortalLinkData.get(level).setDirty(linkID, true);
			PortalLinkData.get(level).setDirty(linkID, false);
		}
	}

	public void reset(Level level)
	{
		if(primaryPortal.isOpen())
			resetPrimary(level);

		if(secondaryPortal.isOpen())
			resetSecondary(level);
	}

	public void resetPrimary(Level level)
	{
		if(primaryPortal.isInWorld())
		{
			ServerLevel portalLevel = level.getServer().getLevel(primaryPortal.getDimension());
			if(portalLevel != null)
			{
				int x = (int) primaryPortal.getPosition().x;
				int z = (int) primaryPortal.getPosition().z;
				PacketDistributor.sendToPlayersTrackingChunk(portalLevel, new ChunkPos(x, z),
						new ClientboundPortalSoundsPacket.FizzlePortal(linkID, true));
			}

			if(isOpen())
			{
				AABB box = PortalUtilities.getPortalBoundingBox(primaryPortal.getPosition(),
						primaryPortal.getXRotation(), primaryPortal.getYRotation()).deflate(0.05);
				for(Entity entity : level.getEntitiesOfClass(Entity.class, box))
				{
					Direction portalDirection = PortalUtilities.getPortalDirection(level, linkID, true);
					entity.push(Vec3.atLowerCornerOf(portalDirection.getNormal()).multiply(0.75f, 0.75f, 0.75f));
					if(entity instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player,
							new ClientboundTeleportMomentumPacket(player.getDeltaMovement().toVector3f()));

				}
			}
		}
		int color = primaryPortal.getColor();

		this.primaryPortal = new Portal();
		this.primaryPortal.setColor(color);
		this.primaryPortal.setReplaceShapes(new ArrayList<>());

		PortalLinkData.get(level).setDirty(linkID, true);
	}

	public void resetSecondary(Level level)
	{
		if(secondaryPortal.isInWorld())
		{
			ServerLevel portalLevel = level.getServer().getLevel(secondaryPortal.getDimension());
			if(portalLevel != null)
			{
				int x = (int) secondaryPortal.getPosition().x;
				int z = (int) secondaryPortal.getPosition().z;
				PacketDistributor.sendToPlayersTrackingChunk(portalLevel, new ChunkPos(x, z),
						new ClientboundPortalSoundsPacket.FizzlePortal(linkID, false));

			}

			if(isOpen())
			{
				AABB box = PortalUtilities.getPortalBoundingBox(secondaryPortal.getPosition(),
						secondaryPortal.getXRotation(), secondaryPortal.getYRotation()).deflate(0.05);
				for(Entity entity : level.getEntitiesOfClass(Entity.class, box))
				{
					Direction portalDirection = PortalUtilities.getPortalDirection(level, linkID, false);
					entity.push(Vec3.atLowerCornerOf(portalDirection.getNormal()).multiply(0.75f, 0.75f, 0.75f));
					if(entity instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player,
							new ClientboundTeleportMomentumPacket(player.getDeltaMovement().toVector3f()));
				}
			}
		}
		int color = secondaryPortal.getColor();

		this.secondaryPortal = new Portal();
		this.secondaryPortal.setColor(color);
		this.secondaryPortal.setReplaceShapes(new ArrayList<>());

		PortalLinkData.get(level).setDirty(linkID, false);
	}

	public void setMoonshot(boolean isPrimary, boolean moonshot, Level level)
	{
		if(isPrimary)
		{
			primaryPortal.setPosition(null);
			primaryPortal.setMoonshot(moonshot);
		}
		else
		{
			secondaryPortal.setPosition(null);
			secondaryPortal.setMoonshot(moonshot);
		}

		PortalLinkData.get(level).setDirty(linkID, isPrimary);
	}

	public boolean isOpen()
	{
		return primaryPortal.isOpen() && secondaryPortal.isOpen();
	}

	public boolean isInWorld()
	{
		return primaryPortal.isInWorld() && secondaryPortal.isInWorld();
	}

	public boolean isInterdimensionalLink()
	{
		if(isOpen())
		{
			return !primaryPortal.getDimension().equals(secondaryPortal.getDimension());
		}
		return false;
	}

	public boolean teleportLogic(Entity entity, ApertureCapability aperture,
								 PortalLink link, Level level, boolean isPrimary)
	{
		UUID linkID = link.linkID;
		if (level.isClientSide())
			return false;

		if (link == null || !link.isOpen())
			return false;

		Portal portal = isPrimary ? link.getPrimaryPortal() : link.getSecondaryPortal();
		Portal otherPortal = isPrimary ? link.getSecondaryPortal() : link.getPrimaryPortal();

		Vec3 portalPosition = portal.getPosition();
		SubLevelAccess portalAccess = null;
		if (!portal.isMoonshot())
		{
			portalAccess = SableCompanion.INSTANCE.getContaining(level, portalPosition);
			//noinspection UnstableApiUsage
			portalPosition = SableCompanion.INSTANCE.projectOutOfSubLevel(level, portalPosition);
		}

		Vec3 otherPortalPosition = new Vec3(0, 0, 0);
		SubLevelAccess otherPortalAccess = null;
		if (!otherPortal.isMoonshot())
		{
			otherPortalPosition = otherPortal.getPosition();
			otherPortalAccess = SableCompanion.INSTANCE.getContaining(level, otherPortalPosition);
			//noinspection UnstableApiUsage
			otherPortalPosition = SableCompanion.INSTANCE.projectOutOfSubLevel(level, otherPortalPosition);
		}

		Vec3 currentPos = new Vec3(entity.xo, entity.yo + entity.getBbHeight() / 2.0, entity.zo);
		Vec3 nextPos = entity.position().add(0, entity.getBbHeight() / 2.0, 0);

		Direction direction = PortalUtilities.getPortalDirection(level, linkID, isPrimary);
		Vector3f normal = direction.step();
		Vec3 normalVec = new Vec3(normal.x(), normal.y(), normal.z());

		double intersectionFraction = getPlaneIntersectionFraction(currentPos, nextPos, portalPosition, normalVec);
		if (intersectionFraction < 0.0)
			return false;

		Vec3 exactIntersectionPoint = currentPos.lerp(nextPos, intersectionFraction);
		AABB teleportBox = PortalUtilities.getPortalTeleportBox(portalPosition, portal.getXRotation(), portal.getYRotation());

		if (!teleportBox.inflate(0.05).contains(exactIntersectionPoint))
			return false;

		boolean movingToward = entity.getDeltaMovement().dot(normalVec) <= 0.05;
		if (!movingToward)
			return false;

		double xSpeed = entity.position().x() - entity.xOld;
		double ySpeed = entity.position().y() - entity.yOld;
		double zSpeed = entity.position().z() - entity.zOld;

		entity.resetFallDistance();

		ServerLevel targetLevel;
		if (otherPortal.isMoonshot()) targetLevel = (ServerLevel) level;
		else targetLevel = level.getServer().getLevel(otherPortal.getDimension());
		if (targetLevel == null)
			return false;

		boolean smallEntity = entity.getBoundingBox().getYsize() < 1.5f;
		double verticalOffset = smallEntity ? -entity.getBoundingBox().getYsize() :
										otherPortal.isOnCeiling() ? -entity.getBoundingBox().getYsize() / 2 : entity.getBoundingBox().getYsize();
		verticalOffset = Math.min(entity.getBoundingBox().getYsize() * 0.95f, verticalOffset);
		Vec3 relativePosition = new Vec3(otherPortal.isOnWall() ? -0.15 : portal.isOnWall() ? 0 : verticalOffset,
				otherPortal.isOnWall() ? -entity.getBoundingBox().getYsize() / 2 : 0, 0);

		if (otherPortalAccess != null)
			relativePosition = relativePosition.add(-0.75, 0, 0);

		Vec3 relativeMomentum;
		if (direction.getAxis().isHorizontal())
			relativeMomentum = CoordUtil.toPortalCoords(portal, new Vec3(xSpeed, 0, zSpeed));
		else relativeMomentum = CoordUtil.toPortalCoords(portal, new Vec3(0, entity.getDeltaMovement().y(), 0));

		Vec3 lookAngle = entity.getLookAngle();
		if (portalAccess != null)
		{
			Vec3 momentum = new Vec3(xSpeed, 0, zSpeed);
			momentum = portalAccess.logicalPose().transformNormalInverse(momentum);

			relativeMomentum = CoordUtil.toPortalCoords(portal, momentum);
			lookAngle = portalAccess.logicalPose().transformNormalInverse(lookAngle);
		}
		Vec3 relativeLookAngle = CoordUtil.toPortalCoords(portal, lookAngle);

		Vec3 destinationPosition;
		Vec3 destinationMomentum;
		Vec3 destinationLookAngle;
		if (!otherPortal.isMoonshot())
		{
			destinationPosition = CoordUtil.fromPortalCoords(otherPortal, relativePosition, true);
			if (entity instanceof Player)
				destinationPosition = destinationPosition.add(otherPortal.getPosition());
			else destinationPosition = destinationPosition.add(otherPortalPosition);

			destinationMomentum = CoordUtil.fromPortalCoords(otherPortal, relativeMomentum, true);
			destinationLookAngle = CoordUtil.fromPortalCoords(otherPortal, relativeLookAngle, true);
		}
		else
		{
			destinationPosition = portalPosition.add(0, 1000, 0);
			destinationMomentum = new Vec3(0, 0, 0);
			destinationLookAngle = entity.getLookAngle();
		}

		if (otherPortalAccess != null)
		{
			destinationMomentum = otherPortalAccess.logicalPose().transformNormal(destinationMomentum);
			destinationLookAngle = otherPortalAccess.logicalPose().transformNormal(destinationLookAngle);
		}

		PortalTravelEvent.Pre event = NeoForge.EVENT_BUS.post(new PortalTravelEvent.Pre(link, portal, isPrimary, level,
				targetLevel, currentPos, destinationPosition, otherPortal.isMoonshot()));

		targetLevel = (ServerLevel) event.getTargetLevel();
		destinationPosition = event.getTargetPos();

		if (event.isCanceled())
			return false;

		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
				new ChunkPos(BlockPos.containing(portalPosition)),
				new ClientboundPortalSoundsPacket.EnterPortal(link.linkID, isPrimary));

		entity.hasImpulse = false;
		entity.setOldPosAndRot();

		PacketDistributor.sendToPlayersTrackingChunk(targetLevel,
				new ChunkPos(BlockPos.containing(destinationPosition)),
				new ClientboundPortalSoundsPacket.EnterPortal(link.linkID, !isPrimary));

		if (entity instanceof ServerPlayer player)
		{
			player.awardStat(StatisticsInit.TIMES_USED_PORTALS.get(), 1);
			player.teleportTo(targetLevel, destinationPosition.x(), destinationPosition.y(), destinationPosition.z(),
					Set.of(), CoordUtil.CoordinateSystems.lookAngleY(destinationLookAngle), entity.getXRot());
			player.setDeltaMovement(destinationMomentum);

			aperture.setPortal(Pair.of(linkID, isPrimary));
			aperture.updateDistance();
			AdvancementInit.PORTAL_TRAVEL.get().trigger(player, portal.getDimension().location(),
					targetLevel.dimension().location(), portalPosition.distanceToSqr(destinationPosition),
					aperture.verticalDistance, aperture.horizontalDistance, otherPortal.isMoonshot());
		}
		else
		{
			entity.teleportTo(targetLevel, destinationPosition.x(), destinationPosition.y(), destinationPosition.z(),
					Set.of(), CoordUtil.CoordinateSystems.lookAngleY(destinationLookAngle), entity.getXRot());
			entity.setDeltaMovement(destinationMomentum);

			PacketDistributor.sendToPlayersTrackingEntity(entity,
					new ClientboundEntityPortalLerpPacket(entity.getId(), destinationPosition.toVector3f(),
							entity.getDeltaMovement().toVector3f(),
							entity.getXRot(),
							CoordUtil.CoordinateSystems.lookAngleY(destinationLookAngle)));
		}

		NeoForge.EVENT_BUS.post(new PortalTravelEvent.Post(link, portal, isPrimary, level,
				targetLevel, currentPos, destinationPosition, otherPortal.isMoonshot()));

		return true;
	}

	public boolean portalIntersects(Entity entity, Vec3 movement, boolean isPrimary)
	{
		Portal portal = isPrimary ? primaryPortal : secondaryPortal;
		if (portal == null || !portal.isInWorld()) return false;

		Vec3 portalPos = portal.getPosition();
		if (entity.position().distanceToSqr(portalPos) > 64.0D)
			return false;

		Direction direction = portal.getDirection();
		Vector3f normal = direction.step();
		Vec3 normalVec = new Vec3(normal.x(), normal.y(), normal.z());

		Vec3 currentPos = new Vec3(entity.xo, entity.yo + entity.getBbHeight() / 2.0, entity.zo);
		Vec3 nextPos = currentPos.add(movement);

		double currentDot = currentPos.subtract(portalPos).dot(normalVec);
		double nextDot = nextPos.subtract(portalPos).dot(normalVec);

		boolean crossedPlane = (currentDot >= 0 && nextDot < 0) || (currentDot <= 0 && nextDot > 0);
		boolean movingToward = entity.getDeltaMovement().dot(normalVec) <= 0.05;

		return crossedPlane && movingToward;
	}

	public double getPlaneIntersectionFraction(Vec3 startPos, Vec3 endPos, Vec3 portalPos, Vec3 normalVec)
	{
		Vec3 direction = endPos.subtract(startPos);
		double denominator = direction.dot(normalVec);

		if (Math.abs(denominator) < 1.0E-5)
			return -1.0;

		double numerator = portalPos.subtract(startPos).dot(normalVec);
		double t = numerator / denominator;

		return (t >= 0.0 && t <= 1.0) ? t : -1.0;
	}

	public double getPortalIntersectionFraction(Entity entity, ApertureCapability aperture, Vec3 movement, boolean isPrimary)
	{
		Portal portal = isPrimary ? primaryPortal : secondaryPortal;
		if (portal == null || !portal.isInWorld()) return -1.0;

		Vec3 portalPos = portal.getPosition();
		if (entity.position().distanceToSqr(portalPos) > 64.0D)
			return -1.0;

		Vec3 startPos = entity.getBoundingBox().getCenter();
		Vec3 endPos = startPos.add(movement);

		Vec3 startOffset = startPos.subtract(portalPos);
		Vec3 endOffset = endPos.subtract(portalPos);

		Vec3 localStart = CoordUtil.toPortalCoords(portal, startOffset);
		Vec3 localEnd = CoordUtil.toPortalCoords(portal, endOffset);

		double xStart = localStart.x();
		double xEnd = localEnd.x();

		if (xStart > 0.0 && xEnd <= 0.0)
		{
			double denominator = xEnd - xStart;
			if (Math.abs(denominator) < 1.0E-5)
				return -1.0;

			double t = -xStart / denominator;
			if (t < 0.0 || t > 1.0)
				return -1.0;

			double intersectionY = localStart.y() + (localEnd.y() - localStart.y()) * t;
			double intersectionZ = localStart.z() + (localEnd.z() - localStart.z()) * t;

			double halfWidth = entity.getBbWidth() / 2.0;
			double halfHeight = entity.getBbHeight() / 2.0;

			if (intersectionY >= -halfHeight && intersectionY <= 2.0 + halfHeight &&
						intersectionZ >= -0.5 - halfWidth && intersectionZ <= 0.5 + halfWidth)
			{
				return t;
			}
		}

		return -1.0;
	}

	public boolean executeSubTickTeleport(Entity entity, ServerLevel level, boolean isPrimary, double intersectionT, Vec3 movement, MoverType moverType)
	{
		Portal portal = isPrimary ? primaryPortal : secondaryPortal;
		Portal otherPortal = isPrimary ? secondaryPortal : primaryPortal;

		Vec3 portalPosition = portal.getPosition();
		Vec3 otherPortalPosition = otherPortal.getPosition();
		Vec3 entityCenter = entity.getBoundingBox().getCenter();

		float entityHeight = entity.getBbHeight();
		entity.resetFallDistance();

		ServerLevel targetLevel = otherPortal.isMoonshot() ? level : level.getServer().getLevel(otherPortal.getDimension());
		if (targetLevel == null)
			return false;

		Vec3 relativePosition = CoordUtil.toPortalCoords(portal, entityCenter.subtract(portalPosition));
		relativePosition = relativePosition.add(0, 0, 0);

		Vec3 currentVelocity = entity.getDeltaMovement();
		Vec3 relativeVelocity = CoordUtil.toPortalCoords(portal, currentVelocity);
		Vec3 lookAngle = entity.getLookAngle();
		Vec3 relativeLookAngle = CoordUtil.toPortalCoords(portal, lookAngle);

		Vec3 destinationVelocity = CoordUtil.fromPortalCoords(otherPortal, relativeVelocity, true);
		Vec3 destinationLookAngle = CoordUtil.fromPortalCoords(otherPortal, relativeLookAngle, true);
		Vec3 destinationPosition = CoordUtil.fromPortalCoords(otherPortal, relativePosition, true)
											.add(otherPortalPosition);
		destinationPosition = destinationPosition.subtract(0, entityHeight / 2.0, 0);

		entity.hasImpulse = false;
		entity.setOldPosAndRot();

		if (entity instanceof ServerPlayer player)
		{
			System.out.println("Pre-TP portal relative: " + relativePosition);
			System.out.println("Pre-TP position: " + entityCenter);
			System.out.println("Pre-TP speed: " + entity.getDeltaMovement());

			player.awardStat(StatisticsInit.TIMES_USED_PORTALS.get(), 1);
			player.teleportTo(targetLevel, destinationPosition.x(), destinationPosition.y(), destinationPosition.z(),
					Set.of(), CoordUtil.CoordinateSystems.lookAngleY(destinationLookAngle), entity.getXRot());
			player.setDeltaMovement(destinationVelocity);
			player.connection.send(new ClientboundSetEntityMotionPacket(player));

			entityCenter = entity.getBoundingBox().getCenter();
			System.out.println("Post-TP portal relative: " + CoordUtil.toPortalCoords(otherPortal, entityCenter.subtract(otherPortalPosition)));
			System.out.println("Post-TP position: " + entityCenter);
			System.out.println("Post-TP speed: " + entity.getDeltaMovement());
		}
		else
		{
			System.out.println("Pre-TP position: " + entity.position());
			System.out.println("Pre-TP speed: " + entity.getDeltaMovement());

			entity.teleportTo(targetLevel, destinationPosition.x(), destinationPosition.y(), destinationPosition.z(),
					Set.of(), CoordUtil.CoordinateSystems.lookAngleY(destinationLookAngle), entity.getXRot());
			entity.setDeltaMovement(destinationVelocity);

			System.out.println("Post-TP position: " + entity.position());
			System.out.println("Post-TP speed: " + entity.getDeltaMovement());


			PacketDistributor.sendToPlayersTrackingEntity(entity,
					new ClientboundEntityPortalLerpPacket(entity.getId(), destinationPosition.toVector3f(),
							destinationVelocity.toVector3f(),
							entity.getXRot(),
							CoordUtil.CoordinateSystems.lookAngleY(destinationLookAngle)));
		}

		return true;
	}

	private double firstLengthSqr(Vec3 vec) {
		return vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
	}

	public static PortalLink load(CompoundTag tag)
	{
		UUID linkID = tag.getUUID("link");
		Portal primaryPortal = new Portal();
		Portal secondaryPortal = new Portal();

		primaryPortal.load(tag.getCompound("primaryPortal"));
		secondaryPortal.load(tag.getCompound("secondaryPortal"));

		ResourceLocation variantKey = ResourceLocation.parse(tag.getString("variantKey"));

		PortalLink link = new PortalLink(linkID, variantKey);
		link.setPrimaryPortal(primaryPortal);
		link.setSecondaryPortal(secondaryPortal);

		return link;
	}

	public CompoundTag save()
	{
		CompoundTag tag = new CompoundTag();

		tag.putUUID("link", linkID);

		tag.put("primaryPortal", this.primaryPortal.save());
		tag.put("secondaryPortal", this.secondaryPortal.save());

		if(variantKey == null)
			variantKey = ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, "chell");
		tag.putString("variantKey", variantKey.toString());

		return tag;
	}

	public PortalGunVariant getGunVariant(Level level)
	{
		Registry<PortalGunVariant> registry =  level.getServer().registryAccess()
													.registryOrThrow(PortalGunVariant.REGISTRY_KEY);
		return registry.get(variantKey);
	}

	public Portal getSecondaryPortal()
	{
		return secondaryPortal;
	}

	public void setSecondaryPortal(Portal secondaryPortal)
	{
		this.secondaryPortal = secondaryPortal;
	}

	public Portal getPrimaryPortal()
	{
		return primaryPortal;
	}

	public void setPrimaryPortal(Portal primaryPortal)
	{
		this.primaryPortal = primaryPortal;
	}
}