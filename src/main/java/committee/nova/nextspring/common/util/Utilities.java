package committee.nova.nextspring.common.util;

import net.minecraft.block.*;
import net.minecraft.world.World;

import java.util.Random;

public class Utilities {
    public static boolean tryCatalyze(World world, int block, int x, int y, int z) {
        int n2 = world.getBlock(x, y, z);
        if (n2 == Block.SAPLING.id) {
            if (!world.isClient) ((SaplingBlock) Block.SAPLING).method_382(world, x, y, z, world.random);
            return true;
        }
        if (n2 == Block.BROWN_MUSHROOM.id || n2 == Block.RED_MUSHROOM.id) {
            if (!world.isClient) return ((MushroomBlock) Block.BLOCKS[n2]).method_345(world, x, y, z, world.random);
            return true;
        }
        if (n2 == Block.MELON_STEM.id || n2 == Block.PUMPKIN_STEM.id) {
            if (world.getBlockData(x, y, z) == 7) return false;
            if (!world.isClient) ((AttachedStemBlock) Block.BLOCKS[n2]).method_385(world, x, y, z);
            return true;
        }
        if (n2 == Block.WHEAT.id) {
            if (world.getBlockData(x, y, z) == 7) return false;
            if (!world.isClient) ((CropBlock) Block.WHEAT).method_293(world, x, y, z);
            return true;
        }
        if (n2 == Block.COCOA.id) {
            if (!world.isClient)
                world.method_3672(x, y, z, 8 | HorizontalFacingBlock.method_297(world.getBlockData(x, y, z)));
            return true;
        }
        if (n2 == Block.GRASS_BLOCK.id) {
            if (!world.isClient) {
                final Random rand = world.random;
                block0:
                for (int i3 = 0; i3 < 128; ++i3) {
                    int n3 = x;
                    int n4 = y + 1;
                    int n5 = z;
                    for (int i4 = 0; i4 < i3 / 16; ++i4) {
                        if (world.getBlock(n3 += rand.nextInt(3) - 1, (n4 += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2) - 1, n5 += rand.nextInt(3) - 1) != Block.GRASS_BLOCK.id || world.method_3783(n3, n4, n5))
                            continue block0;
                    }
                    if (world.getBlock(n3, n4, n5) != 0) continue;
                    if (rand.nextInt(10) != 0) {
                        if (!Block.TALLGRASS.method_450(world, n3, n4, n5)) continue;
                        world.method_3683(n3, n4, n5, Block.TALLGRASS.id, 1);
                        continue;
                    }
                    if (rand.nextInt(3) != 0) {
                        if (!Block.YELLOW_FLOWER.method_450(world, n3, n4, n5)) continue;
                        world.method_3690(n3, n4, n5, Block.YELLOW_FLOWER.id);
                        continue;
                    }
                    if (!Block.RED_FLOWER.method_450(world, n3, n4, n5)) continue;
                    world.method_3690(n3, n4, n5, Block.RED_FLOWER.id);
                }
            }
            return true;
        }
        return false;
    }
}
