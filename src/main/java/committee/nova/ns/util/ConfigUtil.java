package committee.nova.ns.util;

import committee.nova.ns.NextSpring;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;

public class ConfigUtil {
    public static int refreshInterval() {
        return getInt(NextSpring.config, "common:refreshInterval", 100);
    }

    public static boolean influencedByBiome() {
        return getBoolean(NextSpring.config, "common:influencedByBiome", true);
    }

    public static int getInt(WritableConfig config, String identifier, int orElse) {
        final Integer i = config.getIntegerValue(identifier);
        return i != null ? i : orElse;
    }

    public static boolean getBoolean(WritableConfig config, String identifier, boolean orElse) {
        final Boolean b = config.getBooleanValue(identifier);
        return b != null ? b : orElse;
    }
}
