package net.mistersecret312.aperture_innovations.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class NearPortalDeathCriterion extends SimpleCriterionTrigger<NearPortalDeathCriterion.NearPortalDeathTriggerInstance>
{

	public void trigger(ServerPlayer player, double distanceToPortal, boolean isFloor)
	{
		this.trigger(player, (trigger -> trigger.matches(distanceToPortal, isFloor)));
	}

	@Override
	public Codec<NearPortalDeathTriggerInstance> codec()
	{
		return NearPortalDeathTriggerInstance.CODEC;
	}

	public static record NearPortalDeathTriggerInstance(Optional<ContextAwarePredicate> player,
														Optional<Double> distance,
														Optional<Boolean> isFloor) implements SimpleInstance
	{
		public static final Codec<NearPortalDeathTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(
						NearPortalDeathTriggerInstance::player),
				Codec.DOUBLE.optionalFieldOf("distance").forGetter(
						NearPortalDeathTriggerInstance::distance),
				Codec.BOOL.optionalFieldOf("isFloor").forGetter(
						NearPortalDeathTriggerInstance::isFloor)
		).apply(instance, NearPortalDeathTriggerInstance::new));

		public boolean matches(double distanceToPortal, boolean isFloor)
		{
			if(this.distance.isPresent())
			{
				if(this.distance.get() < distanceToPortal)
					return false;
			}

			if(this.isFloor.isPresent())
				if(this.isFloor.get() != isFloor)
					return false;

			return true;
		}
	}
}
