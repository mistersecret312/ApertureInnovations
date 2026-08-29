package net.mistersecret312.aperture_innovations.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.block_entities.VitalApparatusVentBlockEntity;
import net.mistersecret312.aperture_innovations.blocks.multiblock.OrientedMasterBlock;
import net.mistersecret312.aperture_innovations.init.BlockEntityInit;
import net.mistersecret312.aperture_innovations.init.EntityInit;
import net.mistersecret312.aperture_innovations.init.ItemInit;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class VitalApparatusVentBlock extends OrientedMasterBlock
{
	public static final MapCodec<VitalApparatusVentBlock> CODEC = simpleCodec(VitalApparatusVentBlock::new);

	public VitalApparatusVentBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components,
								TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, components, tooltipFlag);

		components.add(Component.translatable("tooltip.aperture_innovations.vital_apparatus_vent.dispensing").withStyle(ChatFormatting.DARK_PURPLE));

		Level level = context.level();
		if(level != null)
		{
			Color hsbColor = Color.getHSBColor(level.getTimeOfDay(1f)*50, 1f, 1f);
			components.add(Component.translatable("tooltip.aperture_innovations.is_configurable").withStyle((style -> style.withColor(
					hsbColor.getRGB()))));
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
											  Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(!level.isClientSide() && blockEntity instanceof VitalApparatusVentBlockEntity vent)
		{
			if(stack.is(ItemInit.CUBE))
			{
				vent.setTrackingCube(stack);
				return ItemInteractionResult.SUCCESS;
			}
			if(stack.getItem() instanceof SpawnEggItem spawnEggItem)
			{
				vent.setTrackingType(spawnEggItem.getType(stack));
				return ItemInteractionResult.SUCCESS;
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public AABB getDefaultMultiblockVolume()
	{
		return new AABB(0, 0, 0, 2, 2, 2);
	}

	@Override
	public VoxelShape getDefaultVoxelShape(Level level, BlockPos pos)
	{
		if(level.getBlockEntity(pos) instanceof VitalApparatusVentBlockEntity vent)
		{
			if(vent.isOpen() && vent.getOpeningTick() >= 16)
			{
				VoxelShape shape = Shapes.empty();
				shape = Shapes.join(shape, Shapes.box(-0.5, -0.5, -0.5, 1.5, 2.125, 0), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(-0.5, -0.5, 1, 1.5, 2.125, 1.5), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(-0.5, -0.5, 0, 0, 2.125, 1), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(1, -0.5, 0, 1.5, 2.125, 1), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(0, 1, 0, 1, 2.125, 1), BooleanOp.OR);

				return shape.move(0, -1.125, 0);
			}
			else
			{
				VoxelShape shape = Shapes.empty();
				shape = Shapes.join(shape, Shapes.box(-0.5, -0.5, -0.5, 1.5, 2.125, 0), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(-0.5, -0.5, 1, 1.5, 2.125, 1.5), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(-0.5, -0.5, 0, 0, 2.125, 1), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(1, -0.5, 0, 1.5, 2.125, 1), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(0, 1, 0, 1, 2.125, 1), BooleanOp.OR);
				shape = Shapes.join(shape, Shapes.box(0, -0.5, 0, 1, -0.3125, 1), BooleanOp.OR);

				return shape.move(0, -1.125, 0);
			}
		}

		return Shapes.box(-0.5, -0.5, -0.5, 1.5, 2, 1.5).move(0, -1.125, 0);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
	{
		return createTickerHelper(blockEntityType, BlockEntityInit.VITAL_APPARATUS_VENT.get(), VitalApparatusVentBlockEntity::tick);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker) {
		return typeB == typeA ? (BlockEntityTicker<A>)ticker : null;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return BlockEntityInit.VITAL_APPARATUS_VENT.get().create(pos, state);
	}
}
