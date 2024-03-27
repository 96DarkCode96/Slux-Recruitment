package eu.darkcode.sluxrecruitment.config;

import eu.darkcode.sluxrecruitment.Core;
import org.jetbrains.annotations.NotNull;

public final class PluginConfig extends APluginConfig{

    public static @NotNull IPluginConfig of(@NotNull Core core, @NotNull String configName) {
        return new PluginConfig(core, configName);
    }

    private PluginConfig(@NotNull Core core, @NotNull String configName) {
        super(core, configName);
    }
}
