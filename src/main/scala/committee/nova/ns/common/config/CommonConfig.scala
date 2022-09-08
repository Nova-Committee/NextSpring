package committee.nova.ns.common.config

import committee.nova.ns.common.config.CommonConfig._
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.common.Configuration

object CommonConfig {
  var refreshInterval: Int = _
  var expireTime: Int = _
  var influencedByBiome: Boolean = _
  var possibilityMultiplier: Double = _
  private var config: Configuration = _
}

class CommonConfig(event: FMLPreInitializationEvent) {
  config = new Configuration(event.getSuggestedConfigurationFile)
  config.load()
  refreshInterval = config.get(Configuration.CATEGORY_GENERAL, "refresh_interval", 100, "Refresh interval for rotten flesh catalysis. A smaller value will make it faster for a plant to be tried to catalyze if possible. Default is 100 ticks(5 sec).").getInt(100)
  expireTime = config.get(Configuration.CATEGORY_GENERAL, "expireTime", 6000, "Expire time of a rotten flesh itemEntity. If smaller than the refreshInterval, the itemEntity will disappear when it refreshes for the first time.").getInt(6000)
  influencedByBiome = config.get(Configuration.CATEGORY_GENERAL, "biomeInfluence", true, "Should the possibility of catalysis be influenced by biome properties (temperature & rainfall)?").getBoolean(true)
  possibilityMultiplier = config.get(Configuration.CATEGORY_GENERAL, "possibilityMultiplier", 1D, "The possibility multiplier. The greater the value is, the more the possibility the plant is catalyzed.").getDouble(1D)
  config.save()
}
