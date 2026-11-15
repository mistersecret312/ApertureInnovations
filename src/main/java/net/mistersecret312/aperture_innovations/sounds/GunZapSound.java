package net.mistersecret312.aperture_innovations.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class GunZapSound extends GunSound<Player>
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 1F;

	public GunZapSound(Player player, SoundEvent soundEvent) {
		super(player, soundEvent, SoundSource.AMBIENT, 2, 4);
		this.looping = true;
		this.volume = VOLUME_MIN;
	}

	@Override
	public void tick()
	{
		if(getDistanceFromSource() <= 4)
			fadeIn();
		else
			fadeOut();

		super.tick();
	}

	@Override
	public boolean canStartSilent()
	{
		return true;
	}

	private void fadeIn()
	{
		if(this.volume < VOLUME_MAX)
			this.volume += 0.1F;
	}

	private void fadeOut()
	{
		if(this.volume > VOLUME_MIN)
			this.volume -= 0.1F;
	}
}
