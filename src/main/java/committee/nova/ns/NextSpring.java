package committee.nova.ns;

import io.github.minecraftcursedlegacy.api.config.Configs;
import io.github.minecraftcursedlegacy.api.registry.Id;
import net.fabricmc.api.ModInitializer;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;
import tk.valoeghese.zoesteriaconfig.api.template.ConfigTemplate;

import java.io.IOException;

public class NextSpring implements ModInitializer {
	public static WritableConfig config;

	@Override
	public void onInitialize() {
		try {
			config = Configs.loadOrCreate(new Id("novaCommittee", "NextSpring"),
					ConfigTemplate.builder()
							.addContainer("common", container -> {
								container.addDataEntry("refreshInterval", 100);
								container.addDataEntry("expireTime", 6000);
								container.addDataEntry("influencedByBiome", true);
								container.addDataEntry("possibilityMultiplier", 1.0F);
								container.addDataEntry("respectVanillaCriteria", true);
							})
							.build());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
