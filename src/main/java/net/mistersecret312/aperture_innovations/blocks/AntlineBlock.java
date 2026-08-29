package net.mistersecret312.aperture_innovations.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.block_entities.AntlineBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.enums.ConnectionState;
import net.mistersecret312.aperture_innovations.data.AntlineData;
import net.mistersecret312.aperture_innovations.data.antline.Antline;
import net.mistersecret312.aperture_innovations.init.BlockEntityInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.items.ColorfulGelItem;
import net.mistersecret312.aperture_innovations.neo_events.AntlineActivateEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class AntlineBlock extends BaseEntityBlock
{
	private static final double THICKNESS = 0.0625f;

	protected static final VoxelShape SHAPE_UP = Shapes.box(0.0, 0.0, 0.0, 1.0, THICKNESS, 1.0);
	protected static final VoxelShape SHAPE_DOWN = Shapes.box(0.0, 1.0 - THICKNESS, 0.0, 1.0, 1.0, 1.0);

	protected static final VoxelShape SHAPE_NORTH = Shapes.box(0.0, 0.0, 1.0 - THICKNESS, 1.0, 1.0, 1.0);
	protected static final VoxelShape SHAPE_SOUTH = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, THICKNESS);

	protected static final VoxelShape SHAPE_EAST = Shapes.box(0.0, 0.0, 0.0, THICKNESS, 1.0, 1.0);
	protected static final VoxelShape SHAPE_WEST = Shapes.box(1.0 - THICKNESS, 0.0, 0.0, 1.0, 1.0, 1.0);

	public static final MapCodec<AntlineBlock> CODEC = simpleCodec(AntlineBlock::new);

	public static final DirectionProperty NORMAL = DirectionProperty.create("normal");

	public AntlineBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(NORMAL, Direction.UP));
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components,
								TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, components, tooltipFlag);

		components.add(Component.translatable("tooltip.aperture_innovations.antline").withStyle(ChatFormatting.DARK_PURPLE));

		Level level = context.level();
		if(level != null)
		{
			Color hsbColor = Color.getHSBColor(level.getTimeOfDay(1f)*50, 1f, 1f);
			components.add(Component.translatable("tooltip.aperture_innovations.is_colorable").withStyle((style -> style.withColor(
					hsbColor.getRGB()))));
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		updateAntline(level, pos);
		toggleAntline(level, pos);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
								   BlockPos neighborPos, boolean movedByPiston)
	{
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
		level.scheduleTick(pos, state.getBlock(), 1);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		super.tick(state, level, pos, random);
		updateAntline(level, pos);
		toggleAntline(level, pos);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(NORMAL, rotation.rotate(state.getValue(NORMAL)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(NORMAL)));
	}

	@Override
	protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, int flags,
												 int recursionLeft)
	{
		super.updateIndirectNeighbourShapes(state, level, pos, flags, recursionLeft);
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity != null && blockEntity.getLevel() != null && blockEntity instanceof AntlineBlockEntity)
		{
			updateAntline(blockEntity.getLevel(), pos);
			toggleAntline(blockEntity.getLevel(), pos);
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
	{
		if(!state.getBlock().equals(newState.getBlock()))
		{
			AntlineData.get(level).removeAntline(level, pos);
			this.updateAntline(level, pos);
			this.toggleAntline(level, pos);
			super.onRemove(state, level, pos, newState, movedByPiston);
		}
		else
		{
			this.updateAntline(level, pos);
			this.toggleAntline(level, pos);
			super.onRemove(state, level, pos, newState, movedByPiston);
		}
	}

	public void updateAntline(Level level, BlockPos pos)
	{
		if(!level.isClientSide())
		{
			AntlineData.get(level).update(level, pos);
			if(level.getBlockEntity(pos) instanceof AntlineBlockEntity antline)
			{
				antline.updateConnections();
				antline.trimConnections();
			}
		}
	}

	public void toggleAntline(Level level, BlockPos pos)
	{
		if(!level.isClientSide())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof AntlineBlockEntity antline)
			{
				Antline link = antline.getAntline();
				if(link == null)
					return;

				boolean shouldActive = false;
				int signal = 0;
				for(BlockPos blockPos : link.links)
				{
					if(level.getBlockEntity(blockPos) instanceof AntlineBlockEntity linkBE)
						if(linkBE.outputting)
							continue;

					if(!shouldActive)
					{
						shouldActive = level.getBestNeighborSignal(blockPos) != 0;
						signal = level.getBestNeighborSignal(blockPos);
					}
				}

				for(BlockPos blockPos : link.antlineBlocks)
				{
					BlockEntity outputEntity = level.getBlockEntity(blockPos);
					if(outputEntity instanceof AntlineBlockEntity outputAntline)
					{
						outputAntline.active = shouldActive;
						outputAntline.signal = signal;

						outputAntline.setChanged();
						for(Direction connectedSide : outputAntline.getConnectedSides())
						{
							ConnectionState state = outputAntline.getState(connectedSide);
							if(state.equals(ConnectionState.LINK))
								NeoForge.EVENT_BUS.post(new AntlineActivateEvent(level, blockPos, blockPos.relative(connectedSide), signal));
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest,
									   FluidState fluid)
	{
		if(level.getBlockEntity(pos) instanceof AntlineBlockEntity antline)
		{
			BlockState fakeState = antline.getFakeState();
			if(fakeState != null)
			{
				antline.setFakeState(null);
				if(!player.getAbilities().instabuild)
					Block.popResource(level, pos, new ItemStack(fakeState.getBlock()));
				return false;
			}
		}

		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
											  Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		if(stack.getItem() instanceof BlockItem blockItem)
		{
			BlockState heldState = blockItem.getBlock().defaultBlockState();
			if(!(heldState.getBlock() instanceof EntityBlock)
					   && level.getBlockEntity(pos) instanceof AntlineBlockEntity antline)
			{
				if(antline.getFakeState() == null)
				{
					heldState = heldState.getBlock().getStateForPlacement(new BlockPlaceContext(player, hand, stack, hitResult));
					if(heldState != null && !heldState.getShape(level, pos).bounds().equals(Shapes.block().bounds()))
						return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

					antline.setFakeState(heldState);
					if(!player.getAbilities().instabuild)
						stack.shrink(1);
					level.scheduleTick(pos, this, 1);
					return ItemInteractionResult.sidedSuccess(level.isClientSide());
				}
			}
		}

		if(stack.is(ItemInit.COLORFUL_GEL))
		{
			ColorfulGelItem gel = (ColorfulGelItem) stack.getItem();
			int color = gel.getColor(stack);

			AntlineBlockEntity antline = (AntlineBlockEntity) level.getBlockEntity(pos);
			if(antline == null)
				return ItemInteractionResult.FAIL;

			if(player.isCrouching())
				antline.activeColor = color;
			else antline.color = color;

			level.scheduleTick(pos, state.getBlock(), 0);
			for(Direction connectedSide : antline.getConnectedSides())
			{
				level.scheduleTick(pos.relative(connectedSide),
						level.getBlockState(pos.relative(connectedSide)).getBlock(), 0);
			}
			level.updateNeighborsAt(pos, state.getBlock());

			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction)
	{
		return true;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		Direction normal = state.getValue(NORMAL);

		VoxelShape shape = switch (normal)
		{
			case UP -> SHAPE_UP;
			case DOWN -> SHAPE_DOWN;
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case EAST -> SHAPE_EAST;
			case WEST -> SHAPE_WEST;
		};

		if(level.getBlockEntity(pos) instanceof AntlineBlockEntity antline)
			if(antline.getFakeState() != null)
				return antline.getFakeState().getShape(level, pos);

		return shape;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		if(level.getBlockEntity(pos) instanceof AntlineBlockEntity antline)
			if(antline.getFakeState() != null)
				return antline.getFakeState().getShape(level, pos);
		return Shapes.empty();
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
									 LevelAccessor level, BlockPos pos, BlockPos neighborPos)
	{
		if(!canSurvive(state, level, pos))
		{
			return Blocks.AIR.defaultBlockState();
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos)
	{
		float destroyTime = 0;
		float digSpeed = player.getDigSpeed(state, pos);
		if(level.getBlockEntity(pos) instanceof AntlineBlockEntity antline)
			if(antline.getFakeState() != null)
			{
				destroyTime = antline.getFakeState().getDestroySpeed(level, pos);
				digSpeed = player.getDigSpeed(antline.getFakeState(), pos);
			}

		if (destroyTime == -1.0F) {
			return 0.0F;
		} else {
			int i = net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
			return digSpeed / destroyTime / (float)i;
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos blockpos = pos.relative(state.getValue(NORMAL).getOpposite());
		BlockState blockstate = level.getBlockState(blockpos);
		return this.canSurviveOn(level, blockpos, blockstate, state.getValue(NORMAL));
	}

	private boolean canSurviveOn(BlockGetter level, BlockPos pos, BlockState state, Direction direction) {
		return state.isFaceSturdy(level, pos, direction);
	}
	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockState state = this.defaultBlockState();
		state = state.setValue(NORMAL, context.getClickedFace());
		return state;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(NORMAL);
		super.createBlockStateDefinition(builder);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return BlockEntityInit.ANTLINE.get().create(pos, state);
	}
}
