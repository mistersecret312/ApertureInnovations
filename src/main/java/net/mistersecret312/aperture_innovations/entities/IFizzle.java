package net.mistersecret312.aperture_innovations.entities;

public interface IFizzle
{
	void fizzle();
	int getFizzlingTick();
	void setFizzlingTick(int tick);
	int getMaxFizzleTime();
	void onFinishFizzling();
}
