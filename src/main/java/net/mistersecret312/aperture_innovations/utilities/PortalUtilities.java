package net.mistersecret312.aperture_innovations.utilities;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.data.PortalLinkData;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;
import net.mistersecret312.aperture_innovations.data.portal.Portal;
import net.mistersecret312.aperture_innovations.data.portal.PortalLink;
import org.joml.Vector2d;

import java.util.*;

import static net.mistersecret312.aperture_innovations.client.renderer.PortalRenderer.LINKS;

public class PortalUtilities
{
	public static HashMap<UUID, PortalLink> getPortalLinks(Level level)
	{
		if(level.isClientSide())
			return new HashMap<>();

		PortalLinkData data = PortalLinkData.get(level);
		return data.portalLinks;
	}

	public static HashMap<UUID, ClientPortalLink> getPortalLinks()
	{
		return LINKS;
	}

	public static Vec3 getPortalPos(Level level, UUID uuid, boolean isPrimary)
	{
		if(level.isClientSide())
		{
			ClientPortalLink link = getPortalLinks().get(uuid);
			Vec3 portalPos;
			if(isPrimary)
				portalPos = link.getPrimaryPortal().getPosition();
			else
				portalPos = link.getSecondaryPortal().getPosition();

			return portalPos;
		}
		else
		{
			PortalLink link = getPortalLinks(level).get(uuid);
			Vec3 portalPos;
			if(isPrimary)
				portalPos = link.getPrimaryPortal().getPosition();
			else
				portalPos = link.getSecondaryPortal().getPosition();

			return portalPos;
		}
	}

	public static Vec2 getPortalRotation(Level level, UUID uuid, boolean isPrimary)
	{
		if(level.isClientSide())
		{
			ClientPortalLink link = getPortalLinks().get(uuid);
			if(isPrimary)
				return new Vec2(link.getPrimaryPortal().getXRotation(), link.getPrimaryPortal().getYRotation());
			else return new Vec2(link.getSecondaryPortal().getXRotation(), link.getSecondaryPortal().getYRotation());

		}
		else
		{
			PortalLink link = getPortalLinks(level).get(uuid);
			if(isPrimary)
				return new Vec2(link.getPrimaryPortal().getXRotation(), link.getPrimaryPortal().getYRotation());
			else return new Vec2(link.getSecondaryPortal().getXRotation(), link.getSecondaryPortal().getYRotation());
		}
	}

	public static Direction getPortalDirection(Level level, UUID uuid, boolean isPrimary)
	{
		Vec2 portalRotation = getPortalRotation(level, uuid, isPrimary);
		float xRot = portalRotation.x;
		float yRot = portalRotation.y;

		Direction direction = Direction.fromYRot(yRot);
		if(xRot == -90)
			direction = Direction.UP;
		if(xRot == 90)
			direction = Direction.DOWN;

		return direction;
	}

	public static boolean isPortalOnWall(Level level, UUID uuid, boolean isPrimary)
	{
		if(level.isClientSide())
		{
			ClientPortalLink link = getPortalLinks().get(uuid);
			return isPrimary ? link.getPrimaryPortal().isOnWall() : link.getSecondaryPortal().isOnWall();
		}
		else
		{
			PortalLink link = getPortalLinks(level).get(uuid);
			return isPrimary ? link.getPrimaryPortal().isOnWall() : link.getSecondaryPortal().isOnWall();
		}
	}

	public static boolean isPortalOpen(Level level, UUID uuid)
	{
		if(level.isClientSide())
		{
			ClientPortalLink link = getPortalLinks().get(uuid);
			return link.isOpen();
		}
		else
		{
			PortalLink link = getPortalLinks(level).get(uuid);
			return link.isOpen();
		}
	}

	public static boolean isPortalOnCeiling(Level level, UUID uuid, boolean isPrimary)
	{
		if(level.isClientSide())
		{
			ClientPortalLink link = getPortalLinks().get(uuid);
			return isPrimary ? link.getPrimaryPortal().isOnCeiling() : link.getSecondaryPortal().isOnCeiling();
		}
		else
		{
			PortalLink link = getPortalLinks(level).get(uuid);
			return isPrimary ? link.getPrimaryPortal().isOnCeiling() : link.getSecondaryPortal().isOnCeiling();
		}
	}

	public static ResourceKey<Level> getPortalDimension(Level level, UUID uuid, boolean isPrimary)
	{
		if(level.isClientSide())
		{
			ClientPortalLink link = getPortalLinks().get(uuid);
			return isPrimary ? link.getPrimaryPortal().getDimension() : link.getSecondaryPortal().getDimension();
		}
		else
		{
			PortalLink link = getPortalLinks(level).get(uuid);
			return isPrimary ? link.getPrimaryPortal().getDimension() : link.getSecondaryPortal().getDimension();
		}
	}
	
	public static AABB getPortalBoundingBox(Vec3 portalPos, float xRot, float yRot)
	{
		AABB portal = new AABB(0D, 0D, 0D, 0D, 0D, 0D);
		Direction direction = Direction.fromYRot(yRot);
		Direction facing = Direction.fromYRot(yRot);
		if(xRot == -90)
			direction = Direction.UP;
		else if(xRot == 90)
			direction = Direction.DOWN;
		if(direction.getAxis().equals(Direction.Axis.X))
			direction = direction.getOpposite();

		if(direction.getAxis().isHorizontal())
		{
			Direction.Axis axis = direction.getAxis();
			boolean positive = direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
			if(axis.equals(Direction.Axis.X))
				portal = new AABB(portalPos.x - (positive ? 0.25 : 0.95), portalPos.y - 0.95, portalPos.z - 0.45,
						portalPos.x + (positive ? 0.95 : 0.25), portalPos.y + 0.95, portalPos.z + 0.45);
			else portal = new AABB(portalPos.x - 0.45, portalPos.y - 0.95, portalPos.z - (positive ? 0.95 : 0.25),
					portalPos.x + 0.45, portalPos.y + 0.95, portalPos.z + (positive ? 0.25 : 0.95));
		}
		else
		{
			Direction.Axis axis = facing.getAxis();
			boolean positive = direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
			if(axis.equals(Direction.Axis.X))
				portal = new AABB(portalPos.x - 0.9, portalPos.y - (positive ? 2 : 0.01), portalPos.z - 0.45,
						portalPos.x + 0.9, portalPos.y + (positive ? 0.01 : 2), portalPos.z + 0.45);
			else if(axis.equals(Direction.Axis.Z))
				portal = new AABB(portalPos.x - 0.45, portalPos.y - (positive ? 2 : 0.01), portalPos.z - 0.9,
						portalPos.x + 0.45, portalPos.y + (positive ? 0.01 : 2), portalPos.z + 0.9);

			return portal;
		}
		return portal;
	}

	public static List<VoxelShape> calculatePortalVoxels(Level level, Vec3 portalPos, float xRot, float yRot)
	{
		List<VoxelShape> shapes = new ArrayList<>();
		AABB portalBox = getPortalBoundingBox(portalPos, xRot, yRot);
		BlockPos.betweenClosedStream(portalBox).forEach(pos ->
			{
				BlockState state = level.getBlockState(pos);
				if(!state.isAir())
				{
					VoxelShape collisionShape = state.getCollisionShape(level, pos).move(pos.getX(),
							pos.getY(), pos.getZ());
					if(!collisionShape.isEmpty())
					{
						VoxelShape shape = Shapes.join(Shapes.create(portalBox), collisionShape, BooleanOp.ONLY_SECOND);
						shapes.add(shape);
					}
				}
			});

		return shapes;
	}

	public static List<VoxelShape> getPortalVoxels(Level level, UUID linkID, boolean isPrimary,
												   Vec3 pos, float xRot, float yRot)
	{
		if(level.isClientSide())
		{
			ClientPortalLink link = getPortalLinks().get(linkID);
			if(link != null)
			{
				Portal portal = isPrimary ? link.getPrimaryPortal() : link.getSecondaryPortal();
				List<VoxelShape> list = portal.getReplaceShapes();
				if(list.isEmpty())
				{
					List<VoxelShape> freshList = PortalUtilities.calculatePortalVoxels(level, pos, xRot, yRot);
					portal.setReplaceShapes(freshList);
					return freshList;
				}
				else return list;
			}
		}
		else
		{
			PortalLink link = getPortalLinks(level).get(linkID);
			if(link != null)
			{
				Portal portal = isPrimary ? link.getPrimaryPortal() : link.getSecondaryPortal();
				List<VoxelShape> list = portal.getReplaceShapes();
				if(list.isEmpty())
				{
					List<VoxelShape> freshList = PortalUtilities.calculatePortalVoxels(level, pos, xRot, yRot);
					portal.setReplaceShapes(freshList);
					return freshList;
				}
				else return list;
			}
		}

		return new ArrayList<>();
	}

	public static AABB getPortalTeleportBox(Vec3 portalPos, float xRot, float yRot)
	{
		AABB portal = new AABB(0D, 0D, 0D, 0D, 0D, 0D);
		Direction direction = Direction.fromYRot(yRot);
		Direction facing = Direction.fromYRot(yRot);
		if(xRot == -90)
			direction = Direction.UP;
		else if(xRot == 90)
			direction = Direction.DOWN;
		if(direction.getAxis().equals(Direction.Axis.X))
			direction = direction.getOpposite();

		if(direction.getAxis().isHorizontal())
		{
			Direction.Axis axis = direction.getAxis();
			boolean positive = direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
			if(axis.equals(Direction.Axis.X))
				portal = new AABB(portalPos.x - (positive ? 0.1 : -0.1), portalPos.y - 0.95, portalPos.z - 0.45,
						portalPos.x + (positive ? 0.2 : -0.2), portalPos.y + 0.95, portalPos.z + 0.45);
			else portal = new AABB(portalPos.x - 0.45, portalPos.y - 0.95, portalPos.z - (positive ? 0.2 : -0.2),
					portalPos.x + 0.45, portalPos.y + 0.95, portalPos.z + (positive ? 0.1 : -0.1));
		}
		else
		{
			Direction.Axis axis = facing.getAxis();
			if(axis.equals(Direction.Axis.X))
				portal = new AABB(portalPos.x - 0.9, portalPos.y - 0.1, portalPos.z - 0.5,
						portalPos.x + 0.9, portalPos.y + 0.1, portalPos.z + 0.5);
			else if(axis.equals(Direction.Axis.Z))
				portal = new AABB(portalPos.x - 0.5, portalPos.y - 0.1, portalPos.z - 0.9,
						portalPos.x + 0.5, portalPos.y + 0.1, portalPos.z + 0.9);

			return portal;
		}
		return portal;
	}

	public static AABB getPortalPlacementBox(Vec3 portalPos, float xRot, float yRot)
	{
		AABB portal = new AABB(0D, 0D, 0D, 0D, 0D, 0D);
		if(portalPos == null)
			return portal;

		Direction direction = Direction.fromYRot(yRot);
		Direction facing = Direction.fromYRot(yRot);
		if(xRot == -90)
			direction = Direction.UP;
		else if(xRot == 90)
			direction = Direction.DOWN;

		if(direction.getAxis().equals(Direction.Axis.X))
			direction = direction.getOpposite();

		if(direction.getAxis().isHorizontal())
		{
			Direction.Axis axis = direction.getAxis();
			boolean positive = direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
			if(axis.equals(Direction.Axis.X))
				portal = new AABB(portalPos.x - (positive ? -0.01 : 0.2), portalPos.y - 0.95, portalPos.z - 0.45,
						portalPos.x - (positive ? -0.2 : 0.01), portalPos.y + 0.95, portalPos.z + 0.45);
			else portal = new AABB(portalPos.x - 0.45, portalPos.y - 0.95, portalPos.z - (positive ? 0.01 : -0.2),
					portalPos.x + 0.45, portalPos.y + 0.95, portalPos.z - (positive ? 0.2 : -0.01));
		}
		else
		{
			Direction.Axis axis = facing.getAxis();
			boolean ceiling = direction.equals(Direction.DOWN);
			if(axis.equals(Direction.Axis.X))
				portal = new AABB(portalPos.x - 0.9, portalPos.y - (ceiling ? -0.1 : 0.1), portalPos.z - 0.45,
						portalPos.x + 0.9, portalPos.y - (ceiling ? -0.01 : 0.01), portalPos.z + 0.45);
			else if(axis.equals(Direction.Axis.Z))
				portal = new AABB(portalPos.x - 0.45, portalPos.y - (ceiling ? -0.1 : 0.1), portalPos.z - 0.9,
						portalPos.x + 0.45, portalPos.y - (ceiling ? -0.01 : 0.01), portalPos.z + 0.9);

			return portal;
		}
		return portal;
	}

	public static AABB getPortalFloorBox(Vec3 portalPos, float xRot, float yRot)
	{
		Direction direction = Direction.fromYRot(yRot);
		if(xRot == -90)
			direction = Direction.UP;
		else if(xRot == 90)
			direction = Direction.DOWN;

		if(direction.getAxis().isHorizontal())
		{
			return new AABB(portalPos.x-0.25, portalPos.y-1.1, portalPos.z-0.25,
					portalPos.x+0.25, portalPos.y-1.1, portalPos.z+0.25);
		}
		else return new AABB(0D,0D,0D,0D,0D,0D);
	}

	public static Pair<UUID, Boolean> getClosestPortal(Entity entity)
	{
		UUID uuid = null;
		boolean isPrimary = false;
		double closestDistance = Double.MAX_VALUE;
		if(entity == null)
			return Pair.of(uuid, isPrimary);

		Vec3 entityPos;
		try
		{
			entityPos = entity.position();
		} catch(Exception e)
		{
			return Pair.of(uuid, isPrimary);
		}

		Level level = entity.level();
		if(level.isClientSide())
		{
			for(Map.Entry<UUID, ClientPortalLink> entry : getPortalLinks().entrySet())
			{
				ClientPortalLink link = entry.getValue();
				for(int i = 0; i < 2; i++)
				{
					Vec3 pos = i == 0 ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();
					if(pos == null)
						continue;

					double distance = entityPos.distanceTo(pos);
					if(closestDistance > distance)
					{
						closestDistance = distance;
						uuid = entry.getKey();
						isPrimary = i == 0;
					}
				}
			}
			return Pair.of(uuid, isPrimary);
		}
		else
		{
			for(Map.Entry<UUID, PortalLink> entry : getPortalLinks(level).entrySet())
			{
				PortalLink link = entry.getValue();
				for(int i = 0; i < 2; i++)
				{
					Vec3 pos = i == 0 ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();
					if(pos == null)
						continue;

					double distance = entityPos.distanceTo(pos);
					if(closestDistance > distance)
					{
						closestDistance = distance;
						uuid = entry.getKey();
						isPrimary = i == 0;
					}
				}
			}
			return Pair.of(uuid, isPrimary);
		}
	}

	public static Pair<UUID, Boolean> getClosestPortal(Level level, Vec3 position, UUID id, boolean checkPrimary)
	{
		UUID uuid = null;
		boolean isPrimary = false;
		double closestDistance = Double.MAX_VALUE;
		if(position == null)
			return Pair.of(uuid, isPrimary);

		if(level.isClientSide())
		{
			for(Map.Entry<UUID, ClientPortalLink> entry : getPortalLinks().entrySet())
			{
				ClientPortalLink link = entry.getValue();
				for(int i = 0; i < 2; i++)
				{
					boolean linkPrimary = i == 0;
					Vec3 pos = linkPrimary ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();
					if(pos == null)
						continue;
					if(link.linkID.equals(id) && linkPrimary == checkPrimary)
						continue;

					double distance = position.distanceTo(pos);
					if(closestDistance > distance)
					{
						closestDistance = distance;
						uuid = entry.getKey();
						isPrimary = i == 0;
					}
				}
			}
			return Pair.of(uuid, isPrimary);
		}
		else
		{
			for(Map.Entry<UUID, PortalLink> entry : getPortalLinks(level).entrySet())
			{
				PortalLink link = entry.getValue();
				for(int i = 0; i < 2; i++)
				{
					boolean linkPrimary = i == 0;
					Vec3 pos = linkPrimary ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();
					if(pos == null)
						continue;
					if(link.linkID.equals(id) && linkPrimary == checkPrimary)
						continue;

					double distance = position.distanceTo(pos);
					if(closestDistance > distance)
					{
						closestDistance = distance;
						uuid = entry.getKey();
						isPrimary = i == 0;
					}
				}
			}
			return Pair.of(uuid, isPrimary);
		}
	}

	public static Pair<UUID, Boolean> getClosestPortal(Level level, Portal portal)
	{
		UUID uuid = null;
		boolean isPrimary = false;
		double closestDistance = Double.MAX_VALUE;
		if(portal == null || portal.getPosition() == null)
			return Pair.of(uuid, isPrimary);

		if(level.isClientSide())
		{
			for(Map.Entry<UUID, ClientPortalLink> entry : getPortalLinks().entrySet())
			{
				ClientPortalLink link = entry.getValue();
				for(int i = 0; i < 2; i++)
				{
					Portal linkPortal = i == 0 ? link.getPrimaryPortal() : link.getSecondaryPortal();
					Vec3 pos = i == 0 ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();
					float xRot = i == 0 ? link.getPrimaryPortal().getXRotation() : link.getSecondaryPortal().getXRotation();
					float yRot = i == 0 ? link.getPrimaryPortal().getYRotation() : link.getSecondaryPortal().getYRotation();
					AABB portalBox = PortalUtilities.getPortalPlacementBox(pos, xRot, yRot).inflate(0.05f);
					if(pos == null)
						continue;
					if(linkPortal.equals(portal))
						continue;

					double distance = portal.getPosition().distanceTo(pos);
					if(closestDistance > distance)
					{
						closestDistance = distance;
						uuid = entry.getKey();
						isPrimary = i == 0;
					}
				}
			}
			return Pair.of(uuid, isPrimary);
		}
		else
		{
			for(Map.Entry<UUID, PortalLink> entry : getPortalLinks(level).entrySet())
			{
				PortalLink link = entry.getValue();
				for(int i = 0; i < 2; i++)
				{
					Portal linkPortal = i == 0 ? link.getPrimaryPortal() : link.getSecondaryPortal();
					Vec3 pos = i == 0 ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();
					float xRot = i == 0 ? link.getPrimaryPortal().getXRotation() : link.getSecondaryPortal().getXRotation();
					float yRot = i == 0 ? link.getPrimaryPortal().getYRotation() : link.getSecondaryPortal().getYRotation();
					AABB portalBox = PortalUtilities.getPortalPlacementBox(pos, xRot, yRot).inflate(0.05f);
					if(pos == null)
						continue;
					if(linkPortal.equals(portal))
						continue;

					double distance = portal.getPosition().distanceTo(pos);
					if(closestDistance > distance)
					{
						closestDistance = distance;
						uuid = entry.getKey();
						isPrimary = i == 0;
					}
				}
			}
			return Pair.of(uuid, isPrimary);
		}
	}

	public static Pair<Portal, Boolean> getPortalByPosition(Level level, Vec3 position)
	{
		Portal portal = null;
		boolean isPrimary = false;
		if(level.isClientSide())
		{
			HashMap<UUID, ClientPortalLink> links = getPortalLinks();
			for(Map.Entry<UUID, ClientPortalLink> entry : links.entrySet())
			{
				ClientPortalLink link = entry.getValue();
				for(int i = 0; i < 2; i++)
				{
					isPrimary = i == 0;
					if(isPrimary && position.equals(link.getPrimaryPortal().getPosition()))
					{
						portal = link.getPrimaryPortal();
						return Pair.of(portal, true);
					}

					if(!isPrimary && position.equals(link.getSecondaryPortal().getPosition()))
					{
						portal = link.getSecondaryPortal();
						return Pair.of(portal, false);
					}
				}
			}
		}
		else
		{
			HashMap<UUID, PortalLink> links = getPortalLinks(level);
			for(Map.Entry<UUID, PortalLink> entry : links.entrySet())
			{
				PortalLink link = entry.getValue();
				for(int i = 0; i < 2; i++)
				{
					isPrimary = i == 0;
					if(isPrimary && position.equals(link.getPrimaryPortal().getPosition()))
					{
						portal = link.getPrimaryPortal();
						return Pair.of(portal, true);
					}

					if(!isPrimary && position.equals(link.getSecondaryPortal().getPosition()))
					{
						portal = link.getSecondaryPortal();
						return Pair.of(portal, false);
					}
				}
			}
		}

		return Pair.of(portal, isPrimary);
	}
}
