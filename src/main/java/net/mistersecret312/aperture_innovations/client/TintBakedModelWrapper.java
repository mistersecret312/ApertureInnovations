package net.mistersecret312.aperture_innovations.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TintBakedModelWrapper extends BakedModelWrapper<BakedModel>
{
	public TintBakedModelWrapper(BakedModel originalModel)
	{
		super(originalModel);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
		return tintQuads(super.getQuads(state, side, rand));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
		return tintQuads(super.getQuads(state, side, rand, data, renderType));
	}

	private List<BakedQuad> tintQuads(List<BakedQuad> originalQuads) {
		List<BakedQuad> newQuads = new ArrayList<>(originalQuads.size());

		for (BakedQuad quad : originalQuads) {
			if (quad.getTintIndex() == -1) {
				newQuads.add(new BakedQuad(
						quad.getVertices(),
						100,
						quad.getDirection(),
						quad.getSprite(),
						quad.isShade()
				));
			} else {
				newQuads.add(quad);
			}
		}
		return newQuads;
	}
}
