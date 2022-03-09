package net.elytrapvp.elytrastaffchat;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Manages the configurable settings in the plugin.
 */
public class SettingsManager {
    private Configuration config;

    /**
     * Loads or Creates configuration files.
     * @param plugin Instance of the plugin.
     */
    public SettingsManager(ElytraStaffChat plugin) {
        // Create the plugin data folder if it isn't already there.
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        // Get the config file if it exists.
        File file = new File(plugin.getDataFolder(), "config.yml");
        try {
            // Create the config file if it doesn't exist yet.
            if (!file.exists()) {
                Files.copy(plugin.getResourceAsStream("config.yml"), file.toPath());
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        }
        catch (IOException exception) {
            // If there is an error, print it to the console.
            exception.printStackTrace();
        }
    }

    /**
     * Get the configuration values for the plugin.
     * @return Plugin configuration.
     */
    public Configuration getConfig() {
        return config;
    }
}