package committee.nova.ns

import committee.nova.ns.proxies.CommonProxy
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.{Mod, SidedProxy}

@Mod(modid = NextSpring.MODID, useMetadata = true, acceptedMinecraftVersions = "[1.6,)", modLanguage = "scala")
object NextSpring {
  final val MODID = "nextspring"
  final val proxyPrefix = "committee.nova.ns.proxies."

  @SidedProxy(serverSide = proxyPrefix + "CommonProxy", clientSide = proxyPrefix + "ClientProxy")
  var proxy: CommonProxy = _

  @EventHandler def preInit(event: FMLPreInitializationEvent): Unit = proxy.preInit(event)

  @EventHandler def init(event: FMLInitializationEvent): Unit = proxy.init(event)
}
