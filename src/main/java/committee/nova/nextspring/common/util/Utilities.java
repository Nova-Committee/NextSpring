package committee.nova.nextspring.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.Growable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utilities {
    public static boolean tryCatalyze(World world, BlockPos pos) {
        final Block block = world.getBlockAt(pos);
        if (!(block instanceof Growable)) return false;
        final Growable growable = (Growable) block;
        if (!growable.canGrow(world, pos, world.getBlockState(pos), world.isClient)) return false;
        if (!world.isClient && growable.canBeFertilized(world, world.random, pos, world.getBlockState(pos)))
            growable.grow(world, world.random, pos, world.getBlockState(pos));
        return true;
    }
}
