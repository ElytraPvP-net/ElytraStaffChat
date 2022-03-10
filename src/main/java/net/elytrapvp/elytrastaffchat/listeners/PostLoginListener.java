package net.elytrapvp.elytrastaffchat.listeners;

import net.elytrapvp.elytrastaffchat.ElytraStaffChat;
import net.elytrapvp.elytrastaffchat.utils.ChatUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * This listens to the PostLoginEvent event, which is called every time a player successfully joins the server.
 * We use this to announce when a staff member joins to other staff members.
 */
public class PostLoginListener implements Listener {
    private final ElytraStaffChat plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * This is known as Dependency Injection.
     * @param plugin Instance of the plugin.
     */
    public PostLoginListener(ElytraStaffChat plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PostLoginEvent.
     */
    @EventHandler
    public void onLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        // Make sure the player has permission to use staff chat first.
        if(!player.hasPermission("staff.chat")) {
            return;
        }

        // Toggle's on the visibility of the staff chat channel.
        plugin.getChannelManager().toggleVisibility(player);

        // Load the join message format from config.yml
        String joinMessage = plugin.getSettingsManager().getConfig().getString("joinMessage");

        // Replace %player_name% with the player's username.
        joinMessage = joinMessage.replace("%player_name%", player.getName());

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

            // Otherwise, send them the join message.
            ChatUtils.chat(viewer, joinMessage);
        }
    }
}
