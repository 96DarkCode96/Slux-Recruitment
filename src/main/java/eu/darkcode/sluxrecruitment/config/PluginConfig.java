package eu.darkcode.sluxrecruitment.config;

import org.jetbrains.annotations.NotNull;

public final class PluginConfig extends APluginConfig{

    public static @NotNull IPluginConfig of(@NotNull String configName) {
        return new PluginConfig(configName);
    }

    private PluginConfig(@NotNull String configName) {
        super(configName);
    }
}
