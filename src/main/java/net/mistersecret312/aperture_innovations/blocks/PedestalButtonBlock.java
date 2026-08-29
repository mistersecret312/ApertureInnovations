package net.mistersecret312.aperture_innovations.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.block_entities.PedestalButtonBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.multiblock.OrientedMasterBlock;
import net.mistersecret312.aperture_innovations.init.BlockEntityInit;
import net.mistersecret312.aperture_innovations.init.SoundInit;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class PedestalButtonBlock extends OrientedMasterBlock
{
	public static final BooleanProperty PRESSED = BooleanProperty.create("pressed");

	public static final MapCodec<PedestalButtonBlock> CODEC = simpleCodec(PedestalButtonBlock::new);

	public PedestalButtonBlock(Properties properties)
	{
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
		if(state.getValue(PRESSED))
			return 15;

		return super.getSignal(state, level, pos, direction);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction)
	{
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(PRESSED);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public AABB getDefaultMultiblockVolume()
	{
		return new AABB(0, 0, 0, 0, 1, 0);
	}

	@Override
	public VoxelShape getFullShape(Level level, BlockPos pos, BlockState state)
	{
		if(!state.hasProperty(FACING) || !state.hasProperty(NORMAL))
			return Shapes.empty();

		Direction facing = state.getValue(FACING);
		Direction normal = state.getValue(NORMAL);

		VoxelShape shape = Shapes.empty();
		if(normal.getAxis().isVertical())
		{
			if(normal.equals(Direction.UP))
			{
				shape = Shapes.box(0.25, 0, 0.4375, 0.75, 1.375, 0.9375);
				if(facing.equals(Direction.NORTH)) shape = Shapes.box(0.25, 0, 0.0625, 0.75, 1.375, 0.5625);
				if(facing.equals(Direction.EAST)) shape = Shapes.box(0.4375, 0, 0.25, 0.9375, 1.375, 0.75);
				if(facing.equals(Direction.WEST)) shape = Shapes.box(0.0625, 0, 0.25, 0.5625, 1.375, 0.75);
			}
			else
			{
				shape = Shapes.box(0.25, -0.375, 0.4375, 0.75, 1, 0.9375);
				if(facing.equals(Direction.NORTH)) shape = Shapes.box(0.25, -0.375, 0.0625, 0.75, 1, 0.5625);
				if(facing.equals(Direction.EAST)) shape = Shapes.box(0.4375, -0.375, 0.25, 0.9375, 1, 0.75);
				if(facing.equals(Direction.WEST)) shape = Shapes.box(0.0625, -0.375, 0.25, 0.5625, 1, 0.75);


				return shape.move(0, -0.01, 0);
			}
		}

		if(normal.getAxis().isHorizontal())
		{
			if(facing.equals(Direction.UP))
			{
				if(normal.equals(Direction.NORTH))
					shape = Shapes.box(0.25, 0.0625, -0.375, 0.75, 0.5625, 1).move(0, 0, -0.01);
				if(normal.equals(Direction.SOUTH))
					shape = Shapes.box(0.25, 0.0625, 0.0, 0.75, 0.5625, 1.375);
				if(normal.equals(Direction.EAST))
					shape = Shapes.box(0.0, 0.0625, 0.25, 1.375, 0.5625, 0.75);
				if(normal.equals(Direction.WEST))
					shape = Shapes.box(-0.375, 0.0625, 0.25, 1.0, 0.5625, 0.75).move(-0.01, 0, 0);
			}

			if(facing.equals(Direction.DOWN))
			{
				if(normal.equals(Direction.NORTH))
					shape = Shapes.box(0.25, 0.4375, -0.375, 0.75, 0.9375, 1.0).move(0, 0, -0.01);
				if(normal.equals(Direction.SOUTH))
					shape = Shapes.box(0.25, 0.4375, 0.0, 0.75, 0.9375, 1.375);
				if(normal.equals(Direction.EAST))
					shape = Shapes.box(0.0, 0.4375, 0.25, 1.375, 0.9375, 0.75);
				if(normal.equals(Direction.WEST))
					shape = Shapes.box(-0.375, 0.4375, 0.25, 1.0, 0.9375, 0.75).move(-0.01, 0, 0);
			}
		}

		return shape;
	}

	@Override
	public VoxelShape getDefaultVoxelShape(Level level, BlockPos pos)
	{
		return Shapes.empty();
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
											   BlockHitResult hitResult)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(state.getValue(PRESSED))
			return InteractionResult.PASS;

		if(blockEntity instanceof PedestalButtonBlockEntity pedestalButton)
		{
			pedestalButton.triggerAnim("press", "press");
			level.setBlock(pos, state.setValue(PRESSED, true), 3);
			level.scheduleTick(pos, this, 25);
			if(!level.isClientSide())
				level.playSound(null, pos, SoundInit.PEDESTAL_BUTTON_DOWN.get(), SoundSource.BLOCKS, 0.5f, 1f);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		super.tick(state, level, pos, random);

		level.setBlock(pos, state.setValue(PRESSED, false), 3);
		level.playSound(null, pos, SoundInit.PEDESTAL_BUTTON_UP.get(), SoundSource.BLOCKS, 0.5f, 1f);
		super.tick(state, level, pos, random);
	}

	@Override
	protected boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return BlockEntityInit.PEDESTAL_BUTTON.get().create(pos, state);
	}
}
