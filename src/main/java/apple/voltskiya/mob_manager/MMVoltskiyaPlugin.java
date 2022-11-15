package apple.voltskiya.mob_manager;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.AbstractVoltPlugin;
import java.util.Collection;
import java.util.List;

public class MMVoltskiyaPlugin extends AbstractVoltPlugin {

    private static MMVoltskiyaPlugin instance;

    public MMVoltskiyaPlugin() {
        instance = this;
    }

    public static MMVoltskiyaPlugin get() {
        return instance;
    }

    @Override
    public Collection<AbstractModule> getModules() {
        return List.of(new MobManager());
    }
}
