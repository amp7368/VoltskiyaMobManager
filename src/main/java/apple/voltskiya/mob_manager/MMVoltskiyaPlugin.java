package apple.voltskiya.mob_manager;

import apple.lib.pmc.AppleModule;
import apple.lib.pmc.ApplePlugin;
import java.util.Collection;
import java.util.List;

public class MMVoltskiyaPlugin extends ApplePlugin {

    private static MMVoltskiyaPlugin instance;

    public MMVoltskiyaPlugin() {
        instance = this;
    }

    public static MMVoltskiyaPlugin get() {
        return instance;
    }

    @Override
    public Collection<AppleModule> getModules() {
        return List.of(new MobManager());
    }
}
