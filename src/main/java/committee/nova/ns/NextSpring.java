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
			config = Configs.loadOrCreate(new Id("nextspring", "NextSpring-Common"),
					ConfigTemplate.builder()
							.addContainer("common", container -> {
								container.addDataEntry("refreshInterval", 100);
								container.addDataEntry("influencedByBiome", true);
							})
							.build());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
