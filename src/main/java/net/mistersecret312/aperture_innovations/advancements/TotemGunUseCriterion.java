package net.mistersecret312.aperture_innovations.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class TotemGunUseCriterion extends SimpleCriterionTrigger<TotemGunUseCriterion.TotemGunUseTriggerInstance>
{

	public void trigger(ServerPlayer player, ItemStack stack)
	{
		this.trigger(player, (trigger -> trigger.matches(stack)));
	}

	@Override
	public Codec<TotemGunUseTriggerInstance> codec()
	{
		return TotemGunUseTriggerInstance.CODEC;
	}

	public static record TotemGunUseTriggerInstance(Optional<ContextAwarePredicate> player,
														Optional<ItemPredicate> item) implements SimpleInstance
	{

		public static final Codec<TotemGunUseTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TotemGunUseTriggerInstance::player),
				ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TotemGunUseTriggerInstance::item)
				).apply(instance, TotemGunUseTriggerInstance::new));

		public boolean matches(ItemStack stack)
		{
			if(this.item.isPresent() && !this.item.get().test(stack))
				return false;

			return true;
		}
	}
}
