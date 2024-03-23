package eu.darkcode.sluxrecruitment;

import eu.darkcode.sluxrecruitment.config.IPluginConfig;
import eu.darkcode.sluxrecruitment.playerdata.PlayerDataManager;
import eu.darkcode.sluxrecruitment.playerdata.player_data_entry.DatabaseNotEnabledException;
import eu.darkcode.sluxrecruitment.utils.ResponseMessage;
import eu.darkcode.sluxrecruitment.utils.ResponseMessageManager;
import eu.darkcode.sluxrecruitment.worldborder.WorldBorderManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public final class Core extends JavaPlugin {

    public static final String PLUGIN_NAME = "Slux-Recruitment";
    private WorldBorderManager worldBorderManager;
    private ResponseMessageManager responseMessageManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        // I don't want to disable saving by default cause of security reasons
        if(!Bukkit.spigot().getSpigotConfig().getBoolean("players.disable-saving")){
            Bukkit.getLogger().log(Level.WARNING, "File player data saving: enabled!");
            Bukkit.getLogger().log(Level.WARNING, "Make sure to disable player data saving in spigot.yml (players.disable-saving)! Otherwise player data will still be saved in the server's storage!");
        }

        if (!IPluginConfig.initConfigDir()) {
            Bukkit.getLogger().severe("Failed to create config dir!"); // This should never happen but just in case if it does (permission issues, etc...)
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.responseMessageManager = new ResponseMessageManager(this);
        this.worldBorderManager = new WorldBorderManager(this);
        try {
            this.playerDataManager = new PlayerDataManager(this);
        } catch (DatabaseNotEnabledException e) {
            Bukkit.getLogger().severe("Failed to create player data manager! (Database not enabled and setup in config!)");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getLogger().info("Slux-Recruitment Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if(playerDataManager != null){
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.kick(getResponseMessageManager().get(ResponseMessage.SERVER_SHUTDOWN), PlayerKickEvent.Cause.RESTART_COMMAND);
                playerDataManager.savePlayerData(player.getName(), player.getUniqueId(), playerDataManager.fetch(player));
            });
            playerDataManager.close();
        }
        Bukkit.getLogger().info("Slux-Recruitment Plugin has been disabled!");
    }
}
