package net.elytrapvp.elytrastaffchat;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to manage who can see and use the staff chat channel.
 */
public class ChannelManager {
    private final Set<ProxiedPlayer> seeChannel;
    private final Set<ProxiedPlayer> useChannel;

    /**
     * Sets up the Channel Manager.
     */
    public ChannelManager() {
        // Stores the players who can see the channel.
        seeChannel = new HashSet<>();

        // Stores the players who have the channel override their normal chat.
        useChannel = new HashSet<>();
    }

    /**
     * Get if a player can see the staff chat channel.
     * @param player The player to check.
     * @return Whether they are using the channel.
     */
    public boolean canSeeChannel(ProxiedPlayer player) {
        return seeChannel.contains(player);
    }

    /**
     * Get if a player is currently using the staff chat channel.
     * @param player The player to check.
     * @return Whether they are using the channel.
     */
    public boolean isUsingChannel(ProxiedPlayer player) {
        return useChannel.contains(player);
    }

    /**
     * Toggle if a player is using the staff chat channel.
     * @param player The player to toggle.
     */
    public void toggleUsingChannel(ProxiedPlayer player) {
        // Check if the player is already using the staff channel.
        if(useChannel.contains(player)) {
            // If so, disable it.
            useChannel.remove(player);
        }
        else {
            // If not, enable it.
            useChannel.add(player);
        }
    }

    /**
     * Toggle if a player can see staff chat.
     * @param player The player to toggle.
     */
    public void toggleVisibility(ProxiedPlayer player) {
        // Check if the player can already see the staff channel.
        if(seeChannel.contains(player)) {
            // If so, disable it.
            seeChannel.remove(player);
        }
        else {
            // If not, enable it.
            seeChannel.add(player);
        }
    }
}
