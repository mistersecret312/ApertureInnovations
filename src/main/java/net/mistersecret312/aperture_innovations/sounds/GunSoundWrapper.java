package net.mistersecret312.aperture_innovations.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.mistersecret312.aperture_innovations.init.SoundInit;

public class GunSoundWrapper<T extends Player> extends SoundWrapper
{
	protected static Minecraft minecraft = Minecraft.getInstance();

	protected T player;
	protected GunSound<?> sound;
	protected boolean playingSound = false;

	protected GunSoundWrapper(T player, GunSound sound)
	{
		this.player = player;
		this.sound = sound;
	}

	@Override
	public boolean isPlaying()
	{
		return this.playingSound;
	}

	@Override
	public boolean hasSound()
	{
		return this.sound != null && !this.sound.isStopped();
	}

	@Override
	public void playSound()
	{
		if(!this.playingSound)
		{
			minecraft.getSoundManager().queueTickingSound(sound);
			this.playingSound = true;
		}
	}

	@Override
	public void stopSound()
	{
		if(this.playingSound)
		{
			this.sound.stopSound();
			this.playingSound = false;
		}
	}

	public static class ZapSound extends GunSoundWrapper<Player>
	{
		public ZapSound(Player player)
		{
			super(player, new GunZapSound(player, SoundInit.PORTAL_GUN_HOLD_LOOP.get()));
		}
	}
}