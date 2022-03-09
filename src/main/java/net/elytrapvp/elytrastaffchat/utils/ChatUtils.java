package net.elytrapvp.elytrastaffchat.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Some methods to make sending chat messages easier.
 */
public class ChatUtils {

    /**
     * A quick way to send a player a colored message.
     * @param player Player to send message to.
     * @param message The message being sent.
     */
    public static void chat(ProxiedPlayer player, String message) {
        player.sendMessage(translate(message));
    }

    /**
     * Translates a String to a colorful String using methods in the BungeeCord API.
     * @param message Message to translate.
     * @return Translated Message.
     */
    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}