package committee.nova.nextspring.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.Growable;
import net.minecraft.world.World;

public class Utilities {
    public static boolean tryCatalyze(World world, int x, int y, int z) {
        final Block block = world.method_3774(x, y, z);
        if (!(block instanceof Growable)) return false;
        final Growable growable = (Growable) block;
        try {
            if (!growable.method_6460(world, x, y, z, world.isClient)) return false;
            if (!world.isClient && growable.method_6461(world, world.random, x, y, z))
                growable.method_6462(world, world.random, x, y, z);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
