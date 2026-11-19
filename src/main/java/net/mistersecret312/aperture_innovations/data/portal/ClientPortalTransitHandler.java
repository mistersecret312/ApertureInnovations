package net.mistersecret312.aperture_innovations.data.portal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class ClientPortalTransitHandler
{
    private static boolean active = false;
    private static int transitTicks = 0;
    private static final int TOTAL_TRANSITION_TICKS = 4;

    private static Vec3 entrancePos;
    private static Vec3 exitTarget;

    public static void startTransit(Vec3 entrance, Vec3 exit, float targetYaw, float targetPitch)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        active = true;
        transitTicks = 0;
        entrancePos = entrance;
        exitTarget = exit;

        mc.player.setYRot(targetYaw);
        mc.player.setXRot(targetPitch);
        mc.player.yRotO = targetYaw;
        mc.player.xRotO = targetPitch;
    }

    public static boolean isTransitioning()
    {
        return active;
    }

    public static void clientTick(LocalPlayer player) {
        if (!active)
            return;

        transitTicks++;
        float progress = (float) transitTicks / TOTAL_TRANSITION_TICKS;

        if (progress >= 1.0f)
        {
            active = false;
            player.setPos(exitTarget.x, exitTarget.y, exitTarget.z);
            return;
        }

        float smoothProgress = progress * progress * (3.0f - 2.0f * progress);
        Vec3 interpolatedPos = exitTarget.subtract(player.getDeltaMovement().scale(0.5)).lerp(exitTarget, smoothProgress);

        player.setPos(interpolatedPos.x, interpolatedPos.y, interpolatedPos.z);
        player.xo = interpolatedPos.x;
        player.yo = interpolatedPos.y;
        player.zo = interpolatedPos.z;
    }
}