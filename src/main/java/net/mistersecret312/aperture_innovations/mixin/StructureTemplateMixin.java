package net.mistersecret312.aperture_innovations.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.mistersecret312.aperture_innovations.init.BlockInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin
{
	@WrapOperation(method = "fillFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
	public boolean dontSaveDummyBlocks(BlockState instance, Block block, Operation<Boolean> original)
	{
		if(instance.is(BlockInit.DUMMY_BLOCK))
			return true;

		return original.call(instance, block);
	}
}
