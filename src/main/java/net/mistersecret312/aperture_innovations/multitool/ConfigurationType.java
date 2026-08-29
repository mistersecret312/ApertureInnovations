package net.mistersecret312.aperture_innovations.multitool;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConfigurationType<T>(StreamCodec<ByteBuf, T> codec)
{}
