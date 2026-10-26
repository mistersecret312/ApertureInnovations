package net.mistersecret312.aperture_innovations.init;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.advancements.NearPortalDeathCriterion;
import net.mistersecret312.aperture_innovations.advancements.PortalTravelCriterion;
import net.mistersecret312.aperture_innovations.advancements.ThrownIntoFluidCriterion;
import net.mistersecret312.aperture_innovations.advancements.TotemGunUseCriterion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AdvancementInit
{
	public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(Registries.TRIGGER_TYPE, ApertureInnovations.MODID);

	public static final Supplier<NearPortalDeathCriterion> NEAR_PORTAL_DEATH = TRIGGER_TYPES.register("near_portal_death", NearPortalDeathCriterion::new);
	public static final Supplier<PortalTravelCriterion> PORTAL_TRAVEL = TRIGGER_TYPES.register("portal_travel", PortalTravelCriterion::new);
	public static final Supplier<ThrownIntoFluidCriterion> THROWN_INTO_FLUID = TRIGGER_TYPES.register("thrown_into_fluid", ThrownIntoFluidCriterion::new);
	public static final Supplier<TotemGunUseCriterion> TOTEM_GUN_USE = TRIGGER_TYPES.register("totem_gun_use", TotemGunUseCriterion::new);

	public static void register(IEventBus eventBus)
	{
		TRIGGER_TYPES.register(eventBus);
	}
}
