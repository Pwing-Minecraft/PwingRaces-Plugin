package net.pwing.races.module;

import net.pwing.races.PwingRaces;

import java.util.HashMap;
import java.util.Map;

public class RaceModuleManager {

    private RaceModuleLoader loader;

    public RaceModuleManager(RaceModuleLoader loader) {
        this.loader = loader;
    }

    public void enableModule(RaceModule module) {
        loader.enableModule(module);
    }

    public void disableModule(RaceModule module) {
        loader.disableModule(module);
    }

    public Map<String, RaceModule> getModules() {
        return loader.modules;
    }
}
