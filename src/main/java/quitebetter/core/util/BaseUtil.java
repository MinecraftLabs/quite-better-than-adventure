package quitebetter.core.util;

import net.minecraft.core.world.World;

public class BaseUtil {
	public static void spawnParticle(World world, String particleKey, double x, double y, double z, double motionX, double motionY, double motionZ, int data) {
		world.spawnParticle(particleKey, x, y, z, motionX, motionY, motionZ, data, true);
	}
}
