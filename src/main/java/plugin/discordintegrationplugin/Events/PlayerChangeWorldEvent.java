package plugin.discordintegrationplugin.Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class PlayerChangeWorldEvent implements Listener {

    public JDA jda;
    private final JavaPlugin plugin;
    public PlayerChangeWorldEvent(JavaPlugin plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    @EventHandler
    public void onChangeWorld(@NotNull PlayerChangedWorldEvent event) {
        //Event variables
        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerWorld = player.getWorld().getName();
        String playerOldWorld = event.getFrom().getName();
        String joinMessage = plugin.getConfig().getString("joinMessage");
        String leaveMessage = plugin.getConfig().getString("leaveMessage");
        String joinHexColor = "#00FF00";
        String leaveHexColor = "#FF0000";

        //Checking if the player name contains a floodgate prefix
        playerName = playerName.replace("*","").replace(".","");
        if (joinMessage != null) joinMessage = joinMessage.replace("%player%",playerName);
        if (leaveMessage != null) leaveMessage = leaveMessage.replace("%player%",playerName);
        String playerAvatar = "https://cravatar.eu/helmavatar/"+playerName+"/64.png";

        //Getting the worlds and channelId in the configuration
        ConfigurationSection worldGroups = plugin.getConfig().getConfigurationSection("worldGroups");
        if (worldGroups == null) return;
        Set<String> groupNames = worldGroups.getKeys(false);
        for (String groupName : groupNames) {
            String channelId = plugin.getConfig().getString("worldGroups." + groupName + ".channelId");
            List<String> worldNames = worldGroups.getStringList(groupName + ".worlds");

            //Sending the leave message to the player's old World
            if (worldNames.contains(playerOldWorld) && !worldNames.contains(playerWorld)) {
                try {
                    if (channelId == null) return;
                    TextChannel textChannel = jda.getTextChannelById(channelId);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(leaveMessage, null, playerAvatar);
                    embed.setColor(Color.decode(leaveHexColor));
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }

            //Sending the join message to the player's new World
            if (worldNames.contains(playerWorld) && !worldNames.contains(playerOldWorld)) {
                try {
                    if (channelId == null) return;
                    TextChannel textChannel = jda.getTextChannelById(channelId);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(joinMessage, null, playerAvatar);
                    embed.setColor(Color.decode(joinHexColor));
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }

        }
    }
}
