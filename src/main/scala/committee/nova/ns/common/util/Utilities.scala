package committee.nova.ns.common.util

import committee.nova.ns.common.config.CommonConfig._
import net.minecraft.block._
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks

import scala.util.Random
import scala.util.control.Breaks._

object Utilities {
  def catalyze(entity: EntityItem): Boolean = {
    val world = entity.worldObj
    val x = Math.floor(entity.posX).intValue()
    val y = Math.floor(entity.posY + 0.2).intValue()
    val z = Math.floor(entity.posZ).intValue()
    val blockIn = world.getBlockId(x, y, z)
    if (influencedByBiome && !willTryCatalyze(world, x, z)) return false
    if (tryCatalyze(world, blockIn, x, y, z)) return true
    val dirt = world.getBlockId(x, y - 1, z)
    tryCatalyze(world, dirt, x, y - 1, z)
  }

  def tryCatalyze(world: World, block: Int, x: Int, y: Int, z: Int): Boolean = {
    if (block == Block.sapling.blockID) {
      if (!world.isRemote) {
        if (world.rand.nextFloat.toDouble < 0.45D) Block.sapling.asInstanceOf[BlockSapling].markOrGrowMarked(world, x, y, z, world.rand)
      }
      true
    }
    else if (block != Block.mushroomBrown.blockID && block != Block.mushroomRed.blockID) if (block != Block.melonStem.blockID && block != Block.pumpkinStem.blockID) if (block > 0 && Block.blocksList(block).isInstanceOf[BlockCrops]) if (world.getBlockMetadata(x, y, z) == 7) return false
    else {
      if (!world.isRemote) Block.blocksList(block).asInstanceOf[BlockCrops].fertilize(world, x, y, z)
      true
    }
    else {
      var i1 = 0
      var j1 = 0
      var k1 = 0
      if (block == Block.cocoaPlant.blockID) {
        i1 = world.getBlockMetadata(x, y, z)
        j1 = BlockDirectional.getDirection(i1)
        k1 = BlockCocoa.func_72219_c(i1)
        if (k1 >= 2) false
        else {
          if (!world.isRemote) {
            k1 += 1
            world.setBlockMetadataWithNotify(x, y, z, k1 << 2 | j1, 2)
          }
          true
        }
      }
      else if (block != Block.grass.blockID) false
      else {
        if (!world.isRemote) {
          for (i <- 0 until 128) {
            breakable {
              j1 = x
              k1 = y + 1
              var l1 = z
              val random = new Random()
              for (j <- 0 until (i / 16)) {
                j1 += random.nextInt(3) - 1
                k1 += (random.nextInt(3) - 1) * random.nextInt(3) / 2
                l1 += random.nextInt(3) - 1
                if (world.getBlockId(j1, k1 - 1, l1) != Block.grass.blockID || world.isBlockNormalCube(j1, k1, l1)) break()
              }
              if (world.getBlockId(j1, k1, l1) == 0) if (random.nextInt(10) != 0) if (Block.tallGrass.canBlockStay(world, j1, k1, l1)) world.setBlock(j1, k1, l1, Block.tallGrass.blockID, 1, 3)
              else ForgeHooks.plantGrass(world, j1, k1, l1)
            }
          }
        }
        true
      }
    }
    else if (world.getBlockMetadata(x, y, z) == 7) false
    else {
      if (!world.isRemote) Block.blocksList(block).asInstanceOf[BlockStem].fertilizeStem(world, x, y, z)
      true
    }
    else {
      if (!world.isRemote && world.rand.nextFloat.toDouble < 0.4D) Block.blocksList(block).asInstanceOf[BlockMushroom].fertilizeMushroom(world, x, y, z, world.rand)
      true
    }
  }

  def willTryCatalyze(world: World, x: Int, z: Int): Boolean = {
    val biome = world.getBiomeGenForCoords(x, z)
    val r = world.rand
    val temperature = biome.temperature
    val humidity = biome.rainfall
    possibilityMultiplier * (temperature * Math.abs(temperature) * (r.nextDouble - 0.3D) + humidity * Math.abs(humidity) * (r.nextDouble - 0.3D)) > 0.25D
  }
}
