package committee.nova.nextspring.common.util;

import net.minecraft.block.*;
import net.minecraft.world.World;

import java.util.Random;

public class Utilities {
    public static boolean tryCatalyze(World world, int block, int x, int y, int z) {
        final int n = world.getBlock(x, y, z);
        if (n == Block.SAPLING.id) {
            if (!world.isClient && world.random.nextFloat() < 0.45F)
                ((SaplingBlock) Block.SAPLING).method_4785(world, x, y, z, world.random);
            return true;
        }
        if (n == Block.BROWN_MUSHROOM.id || n == Block.RED_MUSHROOM.id) {
            if (!world.isClient && world.random.nextFloat() < 0.4F)
                ((MushroomBlock) Block.BLOCKS[n]).method_345(world, x, y, z, world.random);
            return true;
        }
        if (n == Block.MELON_STEM.id || n == Block.PUMPKIN_STEM.id) {
            if (world.getBlockData(x, y, z) == 7) return false;
            if (!world.isClient) ((AttachedStemBlock) Block.BLOCKS[n]).method_385(world, x, y, z);
            return true;
        }
        if (n > 0 && Block.BLOCKS[n] instanceof CropBlock) {
            if (world.getBlockData(x, y, z) == 7) return false;
            if (!world.isClient) ((CropBlock) Block.BLOCKS[n]).method_293(world, x, y, z);
            return true;
        }
        if (n == Block.COCOA.id) {
            int n2 = world.getBlockData(x, y, z);
            int n3 = HorizontalFacingBlock.method_297(n2);
            int n4 = CocoaBlock.method_292(n2);
            if (n4 >= 2) {
                return false;
            }
            if (!world.isClient) world.method_4718(x, y, z, ++n4 << 2 | n3, 2);
            return true;
        }
        if (n == Block.GRASS_BLOCK.id) {
            final Random rand = world.random;
            if (!world.isClient) {
                block0:
                for (int i2 = 0; i2 < 128; ++i2) {
                    int n5 = x;
                    int n6 = y + 1;
                    int n7 = z;
                    for (int i3 = 0; i3 < i2 / 16; ++i3) {
                        if (world.getBlock(n5 += rand.nextInt(3) - 1, (n6 += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2) - 1, n7 += rand.nextInt(3) - 1) != Block.GRASS_BLOCK.id || world.method_3783(n5, n6, n7))
                            continue block0;
                    }
                    if (world.getBlock(n5, n6, n7) != 0) continue;
                    if (rand.nextInt(10) != 0) {
                        if (!Block.TALLGRASS.method_450(world, n5, n6, n7)) continue;
                        world.method_4721(n5, n6, n7, Block.TALLGRASS.id, 1, 3);
                        continue;
                    }
                    if (rand.nextInt(3) != 0) {
                        if (!Block.YELLOW_FLOWER.method_450(world, n5, n6, n7)) continue;
                        world.method_4719(n5, n6, n7, Block.YELLOW_FLOWER.id);
                        continue;
                    }
                    if (!Block.RED_FLOWER.method_450(world, n5, n6, n7)) continue;
                    world.method_4719(n5, n6, n7, Block.RED_FLOWER.id);
                }
            }
            return true;
        }
        return false;
    }
}
