package net.elytrapvp.elytrastaffchat.listeners;

import net.elytrapvp.elytrastaffchat.ElytraStaffChat;
import net.elytrapvp.elytrastaffchat.utils.ChatUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This listens to the ChatEvent event, which is called every time a player send a chat message.
 * We use this to automatically send a message to the staff chat channel if the player has the channel toggled.
 */
public class ChatListener implements Listener {
    private final ElytraStaffChat plugin;

    /**
     * To be able to access the configuration files, we need to pass an instance of the plugin to our listener.
     * This is known as Dependency Injection.
     * @param plugin Instance of the plugin.
     */
    public ChatListener(ElytraStaffChat plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event ChatEvent.
     */
    @EventHandler
    public void onChat(ChatEvent event) {
        // Make sure the message is being sent by a player.
        if(!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        // Make sure the message is not null or empty before we continue.
        if(event.getMessage() == null || event.getMessage().length() == 0) {
            return;
        }

        // Make sure the message is not a command.
        if(event.getMessage().substring(0, 1).equalsIgnoreCase("/")) {
            return;
        }

        // Get the player who is sending the message.
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        // We want to make sure they are currently using the channel before we continue.
        if(!plugin.getChannelManager().isUsingChannel(player)) {
            return;
        }

        // Cancels the event so that the message does not get sent to general chat.
        event.setCancelled(true);

        // Sets the default format to "default".
        String format = "default";

        // Grabs the list of available formats from config.yml
        Configuration formats = plugin.getSettingsManager().getConfig().getSection("formats");

        // Loops through all the available formats.
        for(String str : formats.getKeys()) {
            // Checks if the player has permission for that format.
            if(player.hasPermission("format." + str)) {
                // If so, sets the format to that.
                format = str;

                // Breaks out of the loop to prevent it from reassigning a format.
                break;
            }
        }

        // Creates the message to be sent.
        String newMessage = "";

        // Gets the selected format from the list of formats.
        Configuration formatText = formats.getSection(format);

        // Loop through the format to add the text together.
        for(String str : formatText.getKeys()) {
            // Add the result to the new message.
            newMessage += formatText.getString(str);
        }

        // Replace the placeholders with their intended text.
        newMessage = newMessage
                .replace("%player_name%", player.getName())
                .replace("%message%", event.getMessage());

        // Loop through all online players to find staff to send message to.
        for(ProxiedPlayer viewer : plugin.getProxy().getPlayers()) {
            // Make sure the player has permission to see the message.
            if(!viewer.hasPermission("staff.chat")) {
                continue;
            }

            // Make sure the player has visibility toggled on.
            if(!plugin.getChannelManager().canSeeChannel(viewer)) {
                return;
            }

            // Sends the message with the correct format.
            ChatUtils.chat(viewer, newMessage);
        }

        // Logs the chat message to BungeeCord
        System.out.println(ChatUtils.translate(newMessage));

        // Logs the message to MySQL.
        String name = player.getName();
        String uuid = player.getUniqueId().toString();
        String server = player.getServer().getInfo().getName();
        String channel = "staff";

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try {
                PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement("INSERT INTO chat_logs (server,channel,uuid,username,message) VALUES (?,?,?,?,?)");
                statement.setString(1, server);
                statement.setString(2, channel);
                statement.setString(3, uuid);
                statement.setString(4, name);
                statement.setString(5, event.getMessage());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}