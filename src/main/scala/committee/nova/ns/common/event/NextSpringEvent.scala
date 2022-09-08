package committee.nova.ns.common.event

import committee.nova.ns.common.config.CommonConfig
import committee.nova.ns.common.util.Utilities.catalyze
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.Item
import net.minecraftforge.event.ForgeSubscribe
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.item.ItemExpireEvent

class NextSpringEvent {
  @ForgeSubscribe
  def onItemDropped(event: EntityJoinWorldEvent): Unit = {
    if (event.isCanceled) return
    if (!event.entity.isInstanceOf[EntityItem]) return
    val itemEntity = event.entity.asInstanceOf[EntityItem]
    val stack = itemEntity.getEntityItem
    if (stack == null) return
    if (stack.getItem != Item.rottenFlesh) return
    itemEntity.lifespan = CommonConfig.refreshInterval
  }

  @ForgeSubscribe
  def onItemExpire(event: ItemExpireEvent): Unit = {
    if (event.isCanceled) return
    val itemEntity = event.entityItem
    val stack = itemEntity.getEntityItem
    if (stack.getItem != Item.rottenFlesh) return
    if (itemEntity.age >= CommonConfig.expireTime) return
    if (!catalyze(itemEntity)) {
      event.extraLife = CommonConfig.refreshInterval
      event.setCanceled(true)
      return
    }
    val newStack = stack.copy()
    newStack.stackSize -= 1
    if (newStack.stackSize <= 0) return
    itemEntity.setEntityItemStack(newStack)
    event.extraLife = CommonConfig.refreshInterval
    event.setCanceled(true)
  }
}
