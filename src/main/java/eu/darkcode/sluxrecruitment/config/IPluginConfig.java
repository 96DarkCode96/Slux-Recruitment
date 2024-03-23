package eu.darkcode.sluxrecruitment.config;

import eu.darkcode.sluxrecruitment.Core;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface IPluginConfig {

    /**
     * Create config dir if it doesn't exist
     * @return true if config dir was created or already exists
     */
    static boolean initConfigDir() {
        File pluginDir = new File(Bukkit.getPluginsFolder(), Core.PLUGIN_NAME);
        return pluginDir.exists() || pluginDir.mkdirs();
    }

    /**
     * Works also for reloading
     * @return true if config was loaded/reloaded
     */
    boolean loadConfig();

    /**
     * Save config
     * @return true if config was saved
     */
    boolean saveConfig();

    /**
     * Reload config
     * @return config or null if was not loaded
     */
    @Nullable YamlConfiguration getConfig();
}