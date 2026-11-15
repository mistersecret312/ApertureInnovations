package net.mistersecret312.aperture_innovations.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GunSound<T extends Player> extends AbstractTickableSoundInstance
{
	protected T player;
	protected Vec3 pos;
	protected Minecraft minecraft = Minecraft.getInstance();
	protected int fullDistance;
	protected int maxDistance;

	public GunSound(T player, SoundEvent soundEvent, SoundSource source, int fullDistance, int maxDistance) {
		super(soundEvent, source, SoundInstance.createUnseededRandom());

		this.player = player;
		this.pos = player.position();
		this.relative = true;
		this.fullDistance = fullDistance;
		this.maxDistance = maxDistance;
	}

	@Override
	public void tick()
	{
		if(player == null)
			this.stop();

		this.pos = player.position();
	}

	@Override
	public boolean canStartSilent()
	{
		return true;
	}

	public void stopSound()
	{
		this.stop();
	}

	public double getDistanceFromSource()
	{
		LocalPlayer player = minecraft.player;
		Vec3 playerPos = player.position();
		return pos.distanceTo(playerPos);
	}

	public float getVolume()
	{
		float localVolume = 0.0F;
		double distanceFromSource = getDistanceFromSource();

		float fullDistance = this.fullDistance;
		float maxDistance = this.maxDistance;

		if(fullDistance >= maxDistance)
			maxDistance = fullDistance + 1;

		if(fullDistance >= maxDistance)
			maxDistance = fullDistance + 1;

		if(distanceFromSource <= fullDistance)
			localVolume = getMaxVolume();
		else if(distanceFromSource <= maxDistance)
			localVolume = (float) (getMaxVolume() - (distanceFromSource - fullDistance) / (maxDistance - fullDistance));
		else
			localVolume = getMinVolume();

		return super.getVolume() * localVolume;
	}

	public float getMaxVolume()
	{
		return 1.0F;
	}

	public float getMinVolume()
	{
		return 0.0F;
	}
}