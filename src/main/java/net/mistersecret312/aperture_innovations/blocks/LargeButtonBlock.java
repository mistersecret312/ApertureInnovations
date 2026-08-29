package net.mistersecret312.aperture_innovations.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.block_entities.LargeButtonBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.multiblock.OrientedMasterBlock;
import net.mistersecret312.aperture_innovations.init.BlockEntityInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import net.mistersecret312.aperture_innovations.init.SoundInit;
import net.mistersecret312.aperture_innovations.items.ColorfulGelItem;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class LargeButtonBlock extends OrientedMasterBlock
{
	public static final BooleanProperty PRESSED = BooleanProperty.create("pressed");

	public static final MapCodec<LargeButtonBlock> CODEC = simpleCodec(LargeButtonBlock::new);

	public LargeButtonBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
									  .setValue(PRESSED, false));
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components,
								TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, components, tooltipFlag);

		Level level = context.level();
		if(level != null)
		{
			Color hsbColor = Color.getHSBColor(level.getTimeOfDay(1f)*50, 1f, 1f);
			components.add(Component.translatable("tooltip.aperture_innovations.is_configurable").withStyle((style -> style.withColor(
					hsbColor.getRGB()))));
		}
	}

	@Override
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return getSignal(state, level, pos, state.getValue(NORMAL).getOpposite());
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		if (state.getValue(PRESSED))
			return 15;

		return super.getSignal(state, level, pos, direction);
	}

	@Override
	public AABB getDefaultMultiblockVolume()
	{
		return new AABB(0, 0, 0, 1, 0, 1);
	}

	@Override
	public AABB getMultiblockVolume(Level level, BlockPos pos)
	{
		if(true)
			return super.getMultiblockVolume(level, pos);

		AABB box = new AABB(0, 0, 0, 1, 0, 1);

		BlockState state = level.getBlockState(pos);
		if(state.hasProperty(OrientedMasterBlock.NORMAL) && state.hasProperty(OrientedMasterBlock.FACING))
		{
			Direction normal = state.getValue(OrientedMasterBlock.NORMAL);
			Direction facing = state.getValue(OrientedMasterBlock.FACING);

			if(facing.equals(Direction.WEST))
				box.move(0, 0, 1);
		}

		return box;
	}

	@Override
	public VoxelShape getDefaultVoxelShape(Level level, BlockPos pos)
	{
		return Shapes.empty();
	}

	@Override
	public VoxelShape getFullShape(Level level, BlockPos pos, BlockState state)
	{
		if(!state.hasProperty(OrientedMasterBlock.NORMAL) || !state.hasProperty(OrientedMasterBlock.FACING))
			return Shapes.empty();

		Direction normal = state.getValue(OrientedMasterBlock.NORMAL);
		Direction facing = state.getValue(OrientedMasterBlock.FACING);

		VoxelShape shape = Shapes.box(-0.6875, 0, 0.3125, 0.6875, 0.1875, 1.6875);
		if(normal.equals(Direction.UP))
		{
			if(facing.equals(Direction.NORTH))
				shape = shape.move(1, 0, -1);
			if(facing.equals(Direction.EAST))
				shape = shape.move(1, 0, 0);
			if(facing.equals(Direction.WEST))
				shape = shape.move(0, 0, -1);
		}
		if (normal.equals(Direction.DOWN))
		{
			shape = Shapes.box(-0.6875, 0.8125, 0.3125, 0.6875, 0.99, 1.6875).move(1, 0, 0);
		}

		if (normal.equals(Direction.EAST))
			shape = Shapes.box(0, -0.6875, 0.3125, 0.1875, 0.6875, 1.6875).move(0, 1, 0);

		if (normal.equals(Direction.WEST))
			shape = Shapes.box(0.8125, -0.6875, 0.3125, 0.99, 0.6875, 1.6875).move(0, 1, 0);
		if (normal.equals(Direction.NORTH))
			shape = Shapes.box(-0.6875, -0.6875, 0.8125, 0.6875, 0.6875, 0.99).move(1, 1, 0);
		if (normal.equals(Direction.SOUTH))
			shape = Shapes.box(-0.6875, -0.6875, 0, 0.6875, 0.6875, 0.1875).move(1, 1, 0);
		return shape;
	}

	@Override
	protected boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(PRESSED);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return BlockEntityInit.LARGE_BUTTON.get().create(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
	{
		return createTickerHelper(blockEntityType, BlockEntityInit.LARGE_BUTTON.get(), LargeButtonBlockEntity::tick);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction)
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker)
	{
		return typeB == typeA ? (BlockEntityTicker<A>)ticker : null;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return CODEC;
	}
}