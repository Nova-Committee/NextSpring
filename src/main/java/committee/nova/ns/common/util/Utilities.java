package committee.nova.ns.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utilities {
    public static boolean tryCatalyze(World world, BlockPos pos) {
        final Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof IGrowable)) return false;
        try {
            final IGrowable growable = (IGrowable) block;
            if (!growable.canGrow(world, pos, world.getBlockState(pos), world.isRemote)) return false;
            if (!world.isRemote && growable.canUseBonemeal(world, world.rand, pos, world.getBlockState(pos)))
                growable.grow(world, world.rand, pos, world.getBlockState(pos));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
