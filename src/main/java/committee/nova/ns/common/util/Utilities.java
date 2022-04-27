package committee.nova.ns.common.util;

import net.minecraft.World;
import net.minecraft.block.*;

import java.util.Random;

public class Utilities {
    public static boolean tryCatalyze(World world, double[] pos) {
        final int i = (int) Math.floor(pos[0]);
        final int j = (int) Math.floor(pos[1]);
        final int k = (int) Math.floor(pos[2]);
        final int var8 = world.getBlockId(i, j, k);
        if (var8 == Block.SAPLING.id) {
            if (!world.isClient) ((SaplingBlock) Block.SAPLING).method_2221(world, i, j, k, world.random);
            return true;
        } else if (var8 != Block.BROWN_MUSHROOM.id && var8 != Block.RED_MUSHROOM.id) {
            if (var8 != Block.MELON_STEM.id && var8 != Block.PUMPKIN_STEM.id) {
                if (var8 == Block.WHEAT_CROP.id) {
                    if (!world.isClient) ((CropBlock) Block.WHEAT_CROP).method_2389(world, i, j, k);
                    return true;
                } else if (var8 == Block.GRASS.id) {
                    if (!world.isClient) {
                        label73:
                        for (int var9 = 0; var9 < 128; ++var9) {
                            int var10 = i;
                            int var11 = j + 1;
                            int var12 = k;
                            final Random random = world.random;
                            for (int var13 = 0; var13 < var9 / 16; ++var13) {
                                var10 += random.nextInt(3) - 1;
                                var11 += (random.nextInt(3) - 1) * random.nextInt(3) / 2;
                                var12 += random.nextInt(3) - 1;
                                if (world.getBlockId(var10, var11 - 1, var12) != Block.GRASS.id || world.canSuffocate(var10, var11, var12)) {
                                    continue label73;
                                }
                            }

                            if (world.getBlockId(var10, var11, var12) == 0) {
                                if (random.nextInt(10) != 0) {
                                    world.notify(var10, var11, var12, Block.TALL_GRASS.id, 1);
                                } else if (random.nextInt(3) != 0) {
                                    world.setBlock(var10, var11, var12, Block.FLOWER.id);
                                } else {
                                    world.setBlock(var10, var11, var12, Block.ROSE.id);
                                }
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                if (!world.isClient) ((StemBlock) Block.BY_ID[var8]).method_639(world, i, j, k);
                return true;
            }
        } else {
            if (!world.isClient)
                return ((MushroomPlantBlock) Block.BY_ID[var8]).method_827(world, i, j, k, world.random);
            return true;
        }
    }
}
