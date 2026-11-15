package net.mistersecret312.aperture_innovations.sounds;

import net.minecraft.client.Minecraft;
import net.mistersecret312.aperture_innovations.data.portal.ClientPortalLink;

public class PortalSoundWrapper<T extends ClientPortalLink> extends SoundWrapper
{
	protected static Minecraft minecraft = Minecraft.getInstance();

	protected T link;
	protected PortalSound<?> sound;
	protected boolean playingSound = false;

	protected PortalSoundWrapper(T link, PortalSound sound)
	{
		this.link = link;
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

	public static class PortalAmbient extends PortalSoundWrapper<ClientPortalLink>
	{
		public PortalAmbient(ClientPortalLink link, boolean isPrimary)
		{
			super(link, new PortalAmbientSound(link, isPrimary, SoundAccess.getPortalAmbient(link.linkID(), isPrimary)));
		}
	}
}