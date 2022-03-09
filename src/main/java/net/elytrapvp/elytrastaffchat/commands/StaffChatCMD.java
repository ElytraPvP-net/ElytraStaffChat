package net.elytrapvp.elytrastaffchat.commands;

import io.netty.util.internal.StringUtil;
import net.elytrapvp.elytrastaffchat.ElytraStaffChat;
import net.elytrapvp.elytrastaffchat.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.Arrays;

/**
 * This class runs the staff chat command, which can toggle staff chat visibility and usability, or send a message.
 */
public class StaffChatCMD extends Command {
    private final ElytraStaffChat plugin;

    /**
     * Creates the command /staffchat with the permission staff.chat and alias /sc.
     * @param plugin Instance of the plugin.
     */
    public StaffChatCMD(ElytraStaffChat plugin) {
        super("staffchat", "staff.chat", "sc");
        this.plugin = plugin;
    }

    /**
     * This is the code that runs when the command is sent.
     * @param sender The player (or console) that sent the command.
     * @param args The arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Make sure the Command Sender is a player.
        if(!(sender instanceof ProxiedPlayer)) {
            return;
        }

        // Gets the player who sent the command.
        ProxiedPlayer player = (ProxiedPlayer) sender;

        // If there are no arguments, toggle if the player is using the channel.
        if(args.length == 0) {
            plugin.getChannelManager().toggleUsingChannel(player);

            if(plugin.getChannelManager().isUsingChannel(player)) {
                ChatUtils.chat(player, "&a&lChat &8» &aYou are now using the staff channel.");
            }
            else {
                ChatUtils.chat(player, "&a&lChat &8» &aYou are no longer using the staff channel.");
            }

            return;
        }

        // If the command is equal to /staffchat toggle (or /sc toggle), toggle channel visibility.
        if(args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            plugin.getChannelManager().toggleVisibility(player);

            // Send the player a message telling them what they did.
            if(plugin.getChannelManager().canSeeChannel(player)) {
                ChatUtils.chat(player, "&a&lChat &8» &aYou can now see the staff channel.");
            }
            else {
                ChatUtils.chat(player, "&a&lChat &8» &aYou can no longer see the staff channel.");
            }
            return;
        }

        // Combines the arguments with a space between each to form the message being sent.
        String message = StringUtil.join(" ", Arrays.asList(args)).toString();

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

        // Creates the formatted message to be sent.
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
                .replace("%message%", message);

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

        // Logs the message to BungeeCord.
        System.out.println(newMessage);
    }
}
