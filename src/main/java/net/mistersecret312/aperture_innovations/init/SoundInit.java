package net.mistersecret312.aperture_innovations.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundInit
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ApertureInnovations.MODID);

    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_OPEN_PRIMARY = registerSoundEvent("portal_open_primary");
    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_OPEN_SECONDARY = registerSoundEvent("portal_open_secondary");
    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_AMBIENT = registerSoundEvent("portal_ambient");
    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_ENTER = registerSoundEvent("portal_enter");
    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_INVALID_SURFACE = registerSoundEvent("portal_invalid_surface");
    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_FIZZLE = registerSoundEvent("portal_fizzle");

    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_GUN_ACTIVATION = registerSoundEvent("portal_gun_activation");
    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_GUN_FIRE_PRIMARY = registerSoundEvent("portal_gun_fire_primary");
    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_GUN_FIRE_SECONDARY = registerSoundEvent("portal_gun_fire_secondary");
    public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_GUN_RESET = registerSoundEvent("portal_gun_reset");

	public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_GUN_HOLD_START = registerSoundEvent("portal_gun_hold_start");
	public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_GUN_HOLD_FAIL = registerSoundEvent("portal_gun_hold_fail");
	public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_GUN_HOLD_STOP = registerSoundEvent("portal_gun_hold_stop");
	public static DeferredHolder<SoundEvent, SoundEvent> PORTAL_GUN_HOLD_LOOP = registerSoundEvent("portal_gun_hold_loop");

	public static DeferredHolder<SoundEvent, SoundEvent> LONG_FALL_BOOTS_LAND = registerSoundEvent("long_fall_boots_land");

	public static DeferredHolder<SoundEvent, SoundEvent> CUBE_IMPACT = registerSoundEvent("cube_impact");

	public static DeferredHolder<SoundEvent, SoundEvent> PEDESTAL_BUTTON_DOWN = registerSoundEvent("pedestal_button_down");
	public static DeferredHolder<SoundEvent, SoundEvent> PEDESTAL_BUTTON_UP = registerSoundEvent("pedestal_button_up");
	public static DeferredHolder<SoundEvent, SoundEvent> LARGE_BUTTON_DOWN = registerSoundEvent("large_button_down");
	public static DeferredHolder<SoundEvent, SoundEvent> LARGE_BUTTON_UP = registerSoundEvent("large_button_up");

	public static DeferredHolder<SoundEvent, SoundEvent> TIMER_TICK = registerSoundEvent("timer_tick");

	public static DeferredHolder<SoundEvent, SoundEvent> VITAL_APPARATUS_VENT_OPEN = registerSoundEvent("vital_apparatus_vent_open");
	public static DeferredHolder<SoundEvent, SoundEvent> VITAL_APPARATUS_VENT_CLOSE = registerSoundEvent("vital_apparatus_vent_close");

	public static DeferredHolder<SoundEvent, SoundEvent> FIZZLE = registerSoundEvent("fizzle");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String sound)
    {
        return SOUNDS.register(sound, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ApertureInnovations.MODID, sound)));
    }

    public static void register(IEventBus bus)
    {
        SOUNDS.register(bus);
    }
}
