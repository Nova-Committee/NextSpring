package committee.nova.ns.util;

import net.minecraft.level.Level;
import net.minecraft.tile.CropsTile;
import net.minecraft.tile.SaplingTile;
import net.minecraft.tile.Tile;

import java.util.Random;

public class WorldUtil {
    public static boolean tryCatalyze(Level world, double[] pos) {
        final int i = (int) Math.floor(pos[0]);
        final int j = (int) Math.floor(pos[1]);
        final int k = (int) Math.floor(pos[2]);
        final int var8 = world.getTileId(i, j, k);
        if (var8 == Tile.SAPLING.id) {
            if (!world.isClient) ((SaplingTile) Tile.SAPLING).method_533(world, i, j, k, world.rand);
            return true;
        }

        if (var8 == Tile.CROPS.id) {
            if (!world.isClient) ((CropsTile) Tile.CROPS).method_996(world, i, j, k);
            return true;
        }

        if (var8 == Tile.GRASS.id) {
            if (world.isClient) return true;
            final Random rand = world.rand;
            label53:
            for (int var9 = 0; var9 < 128; ++var9) {
                int var10 = i;
                int var11 = j + 1;
                int var12 = k;
                for (int var13 = 0; var13 < var9 / 16; ++var13) {
                    var10 += rand.nextInt(3) - 1;
                    var11 += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
                    var12 += rand.nextInt(3) - 1;
                    if (world.getTileId(var10, var11 - 1, var12) != Tile.GRASS.id || world.canSuffocate(var10, var11, var12)) {
                        continue label53;
                    }
                }
                if (world.getTileId(var10, var11, var12) == 0) {
                    if (rand.nextInt(10) != 0) {
                        world.method_201(var10, var11, var12, Tile.TALLGRASS.id, 1);
                    } else if (rand.nextInt(3) != 0) {
                        world.setTile(var10, var11, var12, Tile.DANDELION.id);
                    } else {
                        world.setTile(var10, var11, var12, Tile.ROSE.id);
                    }
                }
            }
            return true;
        }
        return false;
    }
}

