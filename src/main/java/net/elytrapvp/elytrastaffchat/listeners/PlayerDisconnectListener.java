package net.elytrapvp.elytrastaffchat.listeners;

import net.elytrapvp.elytrastaffchat.ElytraStaffChat;
import net.elytrapvp.elytrastaffchat.utils.ChatUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * This listens to the PlayerDisconnectEvent event, which is called every time a player leaves the server.
 * We use this to announce when a staff member leaves to other staff members.
 */
public class PlayerDisconnectListener implements Listener {
    private final ElytraStaffChat plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * This is known as Dependency Injection.
     * @param plugin Instance of the plugin.
     */
    public PlayerDisconnectListener(ElytraStaffChat plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerDisconnectEvent.
     */
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        // Make sure the player has permission to use staff chat first.
        if(!player.hasPermission("staff.chat")) {
            return;
        }

        // Load the quit message format from config.yml
        String quitMessage = plugin.getSettingsManager().getConfig().getString("quitMessage");

        // Replace %player_name% with the player's username.
        quitMessage = quitMessage.replace("%player_name%", player.getName());

        // Loop through all online players to find staff.
        for(ProxiedPlayer viewer : plugin.getProxy().getPlayers()) {
            // Skip the player if they do not have permission.
            if(!viewer.hasPermission("staff.chat")) {
                continue;
            }

            // Make sure the player has visibility toggled on.
            if(!plugin.getChannelManager().canSeeChannel(viewer)) {
                return;
            }

            // Otherwise, send them the quit message.
            ChatUtils.chat(viewer, quitMessage);
        }

        // If the player can see the staff channel, toggle it off.
        if(plugin.getChannelManager().canSeeChannel(player)) {
            plugin.getChannelManager().toggleVisibility(player);
        }

        // If the player is still using the staff channel, toggle it off.
        if(plugin.getChannelManager().isUsingChannel(player)) {
            plugin.getChannelManager().toggleUsingChannel(player);
        }
    }
}
