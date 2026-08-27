package net.mistersecret312.aperture_innovations.events;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.block_entities.AntlineBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.AntlineOutputBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.AntlineTimerBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.VitalApparatusVentBlockEntity;
import net.mistersecret312.aperture_innovations.block_entities.multiblock.MasterBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.AntlineOutputBlock;
import net.mistersecret312.aperture_innovations.blocks.AntlineTimerBlock;
import net.mistersecret312.aperture_innovations.blocks.multiblock.DummyBlock;
import net.mistersecret312.aperture_innovations.capabilities.ApertureCapability;
import net.mistersecret312.aperture_innovations.capabilities.ApertureEnergy;
import net.mistersecret312.aperture_innovations.capabilities.HoldEntityCapability;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariants;
import net.mistersecret312.aperture_innovations.client.screen.MultiToolScreen;
import net.mistersecret312.aperture_innovations.client.screen.renderers.EntityPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.PreviewRenderer;
import net.mistersecret312.aperture_innovations.config.LongFallBootsConfig;
import net.mistersecret312.aperture_innovations.init.*;
import net.mistersecret312.aperture_innovations.items.LongFallBootsItem;
import net.mistersecret312.aperture_innovations.items.MultiToolItem;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.mistersecret312.aperture_innovations.multitool.IHaveConfiguration;
import net.mistersecret312.aperture_innovations.neo_events.AntlineActivateEvent;
import net.mistersecret312.aperture_innovations.network.*;
import net.mistersecret312.aperture_innovations.data.portal.Portal;
import net.mistersecret312.aperture_innovations.data.portal.PortalLink;
import net.mistersecret312.aperture_innovations.data.PortalLinkData;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@EventBusSubscriber(modid = ApertureInnovations.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents
{
	@SubscribeEvent
	public static void levelTick(LevelTickEvent.Pre event)
	{
		Level level = event.getLevel();
		if(level instanceof ServerLevel)
		{
			PortalLinkData data = PortalLinkData.get(level);
			for(Map.Entry<UUID, PortalLink> entry : data.portalLinks.entrySet())
			{
				PortalLink link = entry.getValue();

				portals:
				for(int i = 0; i < 2; i++)
				{
					boolean isPrimary = i == 0;
					Portal portal = isPrimary ? link.getPrimaryPortal() : link.getSecondaryPortal();
					if(portal.getPosition() == null)
						continue;

					ResourceKey<Level> portalDim = isPrimary ? link.getPrimaryPortal().getDimension() : link.getSecondaryPortal()
																											.getDimension();
					if(!portalDim.equals(event.getLevel().dimension()))
						continue;

					List<Entity> entities = level.getEntitiesOfClass(Entity.class, new AABB(portal.getPosition(), portal.getPosition()).inflate(0, ((ServerLevel) level).getLogicalHeight(), 0).inflate(3));
					for(Entity entity : entities)
					{
						ApertureCapability aperture = entity.getData(AttachmentTypeInit.APERTURE);
						if(aperture.ignorePortalsTime != 0)
							break;

						aperture.setFrictionlessTime(4);

						if(link.teleportLogic(entity, aperture, link, level, isPrimary))
							break portals;
					}
				}

				for(int i = 0; i < 2; i++)
				{
					boolean isPrimary = i == 0;
					Vec3 portalPos = isPrimary ? link.getPrimaryPortal().getPosition() : link.getSecondaryPortal().getPosition();

					if(portalPos == null)
						continue;

					ResourceKey<Level> portalDim = isPrimary ? link.getPrimaryPortal().getDimension() : link.getSecondaryPortal()
																											.getDimension();
					if(!portalDim.equals(event.getLevel().dimension()))
						continue;


					float xRot = isPrimary ? link.getPrimaryPortal().getXRotation() : link.getSecondaryPortal()
																						  .getXRotation();
					float yRot = isPrimary ? link.getPrimaryPortal().getYRotation() : link.getSecondaryPortal().getYRotation();

					Direction direction = Direction.fromYRot(yRot);
					if(xRot == -90)
						direction = Direction.UP;
					if(xRot == 90)
						direction = Direction.DOWN;

					if(!link.checkForValidity(level, portalPos, xRot, yRot, direction, link.linkID, isPrimary))
					{
						if(isPrimary)
							link.resetPrimary(level);
						else link.resetSecondary(level);
					}

					if(link.isOpen())
					{
						PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level,
								new ChunkPos(BlockPos.containing(portalPos)),
								new ClientboundPortalAmbientSoundPacket(link.linkID, isPrimary, false));
					}
				}

			}
		}
	}


	@SubscribeEvent
	public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event)
	{
		Player player = event.getEntity();
		if(player instanceof ServerPlayer serverPlayer)
		{
			PortalLinkData data = PortalLinkData.get(serverPlayer.level());
			data.portalLinks.forEach((uuid, link) -> {
				for(int i = 0; i < 2; i++)
				{
					boolean isPrimary = i == 0;
					if(isPrimary)
						PacketDistributor.sendToPlayer(serverPlayer, new ClientBoundPortalSyncPacket(uuid, true,
								link.getPrimaryPortal(), link.variantKey));
					else
						PacketDistributor.sendToPlayer(serverPlayer, new ClientBoundPortalSyncPacket(uuid, false,
								link.getSecondaryPortal(), link.variantKey));
				}
			});
		}
	}

	@SubscribeEvent
	public static void entityInteract(PlayerInteractEvent.EntityInteract event)
	{
		Entity entity = event.getTarget();
		Player player = event.getEntity();

		Level level = player.level();
		if(!level.isClientSide())
		{
			ItemStack main = player.getMainHandItem();
			ItemStack off = player.getOffhandItem();
			boolean hasMultiTool = main.is(ItemInit.MULTI_TOOL.get()) || off.is(ItemInit.MULTI_TOOL.get());
			if(!hasMultiTool) return;

			PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundOpenMutliToolEntityScreenPacket(entity.getId(), main.is(ItemInit.MULTI_TOOL.get())));
		}
	}

	@SubscribeEvent
	public static void playerFall(EntityInvulnerabilityCheckEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof LivingEntity living)
		{
			for(ItemStack stack : living.getArmorSlots())
			{
				if((event.getSource().equals(entity.damageSources().fall()) || event.getSource().equals(entity.damageSources().flyIntoWall()))
						   && stack.getItem() instanceof LongFallBootsItem)
				{
					if(!living.level().isClientSide())
					{
						@Nullable IEnergyStorage cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
						if(cap != null && cap instanceof ApertureEnergy energy)
						{
							if(LongFallBootsConfig.long_fall_boots_use_energy.get())
							{
								long toExtract = LongFallBootsConfig.fall_energy_consumption.get();
								long extracted = energy.extractLongEnergy(toExtract, false);
								if(extracted < toExtract)
									return;
							}
						}
					}
					ServerLevel level = (ServerLevel) living.level();
					level.playSound(null, living.blockPosition(), SoundInit.LONG_FALL_BOOTS_LAND.get(),
							SoundSource.PLAYERS, 0.05f, 1F);
					event.setInvulnerable(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerDied(LivingDeathEvent event)
	{
		LivingEntity living = event.getEntity();
		Level level = living.level();
		if(living instanceof ServerPlayer player)
		{
			Pair<UUID, Boolean> closestPortal = PortalUtilities.getClosestPortal(player);
			UUID linkID = closestPortal.getFirst();
			if(linkID == null)
				return;
			boolean isPrimary = closestPortal.getSecond();

			Vec3 portalPos = PortalUtilities.getPortalPos(level, linkID, isPrimary);
			if(portalPos == null)
				return;

			boolean onWall = PortalUtilities.isPortalOnWall(level, linkID, isPrimary);
			boolean onCeiling = PortalUtilities.isPortalOnCeiling(level, linkID, isPrimary);

			double distance = portalPos.distanceTo(player.position());
			if(distance > 16)
				return;

			AdvancementInit.NEAR_PORTAL_DEATH.get().trigger(player, distance, !onWall && !onCeiling);
		}
	}

	@SubscribeEvent
	public static void totemDeath(LivingUseTotemEvent event)
	{
		LivingEntity living = event.getEntity();
		Level level = living.level();
		if(living instanceof ServerPlayer player)
		{
			Pair<UUID, Boolean> closestPortal = PortalUtilities.getClosestPortal(player);
			UUID linkID = closestPortal.getFirst();
			if(linkID == null)
				return;

			boolean isPrimary = closestPortal.getSecond();

			Vec3 portalPos = PortalUtilities.getPortalPos(level, linkID, isPrimary);
			if(portalPos == null)
				return;

			boolean onWall = PortalUtilities.isPortalOnWall(level, linkID, isPrimary);
			boolean onCeiling = PortalUtilities.isPortalOnCeiling(level, linkID, isPrimary);

			long distance = (long) portalPos.distanceTo(player.position());
			if(distance > 16)
				return;

			AdvancementInit.NEAR_PORTAL_DEATH.get().trigger(player, distance, !onWall && !onCeiling);
		}
	}

	@SubscribeEvent
	public static void livingTick(EntityTickEvent.Pre event)
	{
		Entity entity = event.getEntity();
		Level level = entity.level();

		ApertureCapability aperture = entity.getData(AttachmentTypeInit.APERTURE.get());
		aperture.tick(level, entity);
		HoldEntityCapability holdEntity = entity.getData(AttachmentTypeInit.HOLD_ENTITY.get());
		holdEntity.tick(level, entity);
	}

	@SubscribeEvent
	public static void itemToss(ItemTossEvent event)
	{
		if(event.getEntity().getItem().getItem() instanceof PortalGunItem)
		{
			event.getEntity().setThrower(event.getPlayer());
		}
	}

	@SubscribeEvent
	public static void antlineActivate(AntlineActivateEvent event)
	{
		Level level = event.getLevel();
		BlockPos antlinePos = event.getAntlinePos();
		BlockPos activatedBlockPos = event.getActivatedBlockPos();
		int signal = event.getSignal();

		AntlineBlockEntity antline = (AntlineBlockEntity) level.getBlockEntity(antlinePos);
		if(antline == null)
			return;

		BlockState activatedBlockState = level.getBlockState(activatedBlockPos);

		if(activatedBlockState.getBlock() instanceof DummyBlock dummyBlock)
		{
			MasterBlockEntity master = dummyBlock.getMaster(level, activatedBlockPos);
			if(master instanceof VitalApparatusVentBlockEntity vent)
			{
				vent.toggleHatch(signal != 0);

				if(signal != 0)
				{
					vent.fizzleTrackedEntity(level);
				}
			}
		}

		if(activatedBlockState.getBlock() instanceof AntlineOutputBlock)
		{
			activatedBlockState = activatedBlockState.setValue(AntlineOutputBlock.ACTIVE, signal != 0);
			BlockEntity blockEntity = level.getBlockEntity(activatedBlockPos);
			if(blockEntity instanceof AntlineOutputBlockEntity output)
				output.signal = signal;

			level.setBlock(activatedBlockPos, activatedBlockState, 16 | 2);
			BlockPos relativePos = activatedBlockPos.relative(activatedBlockState.getValue(AntlineOutputBlock.NORMAL).getOpposite());
			level.updateNeighborsAt(relativePos, activatedBlockState.getBlock());
		}

		if(activatedBlockState.getBlock() instanceof AntlineTimerBlock)
		{
			if(signal == 0)
				return;
			if(activatedBlockState.getValue(AntlineTimerBlock.ACTIVE))
				return;

			activatedBlockState = activatedBlockState.setValue(AntlineTimerBlock.ACTIVE, true);
			BlockEntity blockEntity = level.getBlockEntity(activatedBlockPos);
			if(blockEntity instanceof AntlineTimerBlockEntity output)
			{
				output.signal = signal;
				output.time = output.maxTime;
				output.soundTime = 20;
			}

			level.setBlock(activatedBlockPos, activatedBlockState, 16 | 2);
			BlockPos relativePos = activatedBlockPos.relative(activatedBlockState.getValue(AntlineTimerBlock.NORMAL).getOpposite());
			level.updateNeighborsAt(relativePos, activatedBlockState.getBlock());
		}

	}
}
