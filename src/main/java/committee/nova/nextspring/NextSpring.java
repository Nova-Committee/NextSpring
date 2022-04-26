package committee.nova.nextspring;

import net.fabricmc.api.ModInitializer;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public class NextSpring implements ModInitializer {
    @Override
    public void onInitialize() {
        MixinBootstrap.init();
        Mixins.addConfiguration("nextspring.mixins.json");
    }
}
