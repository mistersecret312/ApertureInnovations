package net.mistersecret312.aperture_innovations.multitool;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Color(int red, int green, int blue)
{
	public static final StreamCodec<ByteBuf, Color> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, Color::red,
			ByteBufCodecs.INT, Color::green,
			ByteBufCodecs.INT, Color::blue,
			Color::new
	);

	@Override
	public String toString()
	{
		//TODO
		return String.valueOf(packagedInt());
	}

	public static Color fromInt(int packaged)
	{
		java.awt.Color color = new java.awt.Color(packaged ,false);

		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	public int packagedInt()
	{
		int rgb = red;
		rgb = (rgb << 8) + green;
		rgb = (rgb << 8) + blue;

		return rgb;
	}


	public float getRed()
	{
		return red/255f;
	}

	public float getGreen()
	{
		return green/255f;
	}

	public float getBlue()
	{
		return blue/255f;
	}
}
