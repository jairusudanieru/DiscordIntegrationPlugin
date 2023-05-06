package plugin.discordintegrationplugin.Events;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class PlayerJoinLeaveEvent implements Listener {

    public JDA jda;
    private final JavaPlugin plugin;
    public PlayerJoinLeaveEvent(JavaPlugin plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        //Event variables
        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerWorld = player.getWorld().getName();
        String joinMessage = plugin.getConfig().getString("joinMessage");
        String hexColor = "#00FF00";

        //Checking if the authMe plugin is present
        Plugin authMe = Bukkit.getServer().getPluginManager().getPlugin("AuthMe");
        if (authMe != null) return;

        //Checking if the player name contains a floodgate prefix and checking the message in the config
        playerName = playerName.replace("*","").replace(".","");
        if (joinMessage != null) joinMessage = joinMessage.replace("%player%",playerName);
        String playerAvatar = "https://cravatar.eu/helmavatar/"+playerName+"/64.png";

        //Getting the worlds and channelId in the configuration
        ConfigurationSection worldGroups = plugin.getConfig().getConfigurationSection("worldGroups");
        if (worldGroups == null) return;
        Set<String> groupNames = worldGroups.getKeys(false);
        for (String groupName : groupNames) {
            String channelId = plugin.getConfig().getString("worldGroups." + groupName + ".channelId");
            List<String> worldNames = worldGroups.getStringList(groupName + ".worlds");

            //Checking which group the player's current world is
            if (worldNames.contains(playerWorld)) {
                try {
                    if (channelId == null) return;
                    TextChannel textChannel = jda.getTextChannelById(channelId);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(joinMessage, null, playerAvatar);
                    embed.setColor(Color.decode(hexColor));
                    if (textChannel == null) return;
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event) {
        //Event variables
        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerWorld = player.getWorld().getName();
        String leaveMessage = plugin.getConfig().getString("leaveMessage");
        String hexColor = "#FF0000";

        //Checking if the player is not logged in
        Plugin authMe = Bukkit.getServer().getPluginManager().getPlugin("AuthMe");
        if (authMe != null) if (!AuthMeApi.getInstance().isAuthenticated(player)) return;

        //Checking if the player name contains a floodgate prefix and checking the message in the config
        if (playerName.contains("*")) playerName = playerName.replace("*","");
        if (playerName.contains(".")) playerName = playerName.replace(".","");
        if (leaveMessage != null) leaveMessage = leaveMessage.replace("%player%",playerName);
        String playerAvatar = "https://cravatar.eu/helmavatar/"+playerName+"/64.png";

        //Getting the worlds, webhookUrl and channelId in the configuration
        ConfigurationSection worldGroups = plugin.getConfig().getConfigurationSection("worldGroups");
        if (worldGroups == null) return;
        Set<String> groupNames = worldGroups.getKeys(false);
        for (String groupName : groupNames) {
            String channelId = plugin.getConfig().getString("worldGroups." + groupName + ".channelId");
            List<String> worldNames = worldGroups.getStringList(groupName + ".worlds");

            //Checking which group the player's current world is
            if (worldNames.contains(playerWorld)) {
                try {
                    if (channelId == null) return;
                    TextChannel textChannel = jda.getTextChannelById(channelId);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(leaveMessage, null, playerAvatar);
                    embed.setColor(Color.decode(hexColor));
                    if (textChannel == null) return;
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }
    }

}
