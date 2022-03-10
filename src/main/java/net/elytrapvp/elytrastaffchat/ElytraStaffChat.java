package net.elytrapvp.elytrastaffchat;

import net.elytrapvp.elytrastaffchat.commands.StaffChatCMD;
import net.elytrapvp.elytrastaffchat.listeners.ChatListener;
import net.elytrapvp.elytrastaffchat.listeners.PlayerDisconnectListener;
import net.elytrapvp.elytrastaffchat.listeners.PostLoginListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * This plugin manages all aspects of the staff chat channel through BungeeCord.
 */
public final class ElytraStaffChat extends Plugin {
    private ChannelManager channelManager;
    private SettingsManager settingsManager;
    private MySQL mySQL;

    /**
     * This is called when BungeeCord first loads the plugin.
     */
    @Override
    public void onEnable() {
        // Sets up the Channel Manager, which manages who can see and use the Staff Chat Channel.
        channelManager = new ChannelManager();

        // Creates or Loads the configuration file.
        settingsManager = new SettingsManager(this);

        // Connects to the mysql database.
        mySQL = new MySQL(this);
        // Connection is opened async.
        getProxy().getScheduler().runAsync(this, () -> mySQL.openConnection());

        // We need to tell BungeeCord that our listeners exist for them to work.
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerDisconnectListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PostLoginListener(this));

        // We must also tell BungeeCord that our commands exist.
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new StaffChatCMD(this));
    }

    /**
     * Get the Channel Manager, which allows us to enable or disable staff chat visibility and usability.
     * @return Chat Manager.
     */
    public ChannelManager getChannelManager() {
        return channelManager;
    }

    /**
     * Be able to connect to MySQL.
     * @return MySQL.
     */
    public MySQL getMySQL() {
        return mySQL;
    }

    /**
     * Get the Settings Manager, which gives us access to the plugin Configuration.
     * @return Settings Manager.
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
}