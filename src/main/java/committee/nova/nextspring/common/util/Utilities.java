package committee.nova.nextspring.common.util;

import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utilities {
    public static boolean tryCatalyze(World world, BlockPos pos) {
        final var block = world.getBlockState(pos).getBlock();
        if (!(block instanceof final Fertilizable growable)) return false;
        if (!growable.isFertilizable(world, pos, world.getBlockState(pos), world.isClient)) return false;
        if (!world.isClient && growable.canGrow(world, world.random, pos, world.getBlockState(pos)))
            growable.grow((ServerWorld) world, world.random, pos, world.getBlockState(pos));
        return true;
    }
}
