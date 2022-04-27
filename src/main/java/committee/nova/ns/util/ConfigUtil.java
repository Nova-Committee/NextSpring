package committee.nova.ns.util;

import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;

public class ConfigUtil {
    public static int getInt(WritableConfig config, String identifier, int orElse) {
        final Integer i = config.getIntegerValue(identifier);
        return i != null ? i : orElse;
    }

    public static float getFloat(WritableConfig config, String identifier, float orElse) {
        final Float f = config.getFloatValue(identifier);
        return f != null ? f : orElse;
    }

    public static boolean getBoolean(WritableConfig config, String identifier, boolean orElse) {
        final Boolean b = config.getBooleanValue(identifier);
        return b != null ? b : orElse;
    }
}
