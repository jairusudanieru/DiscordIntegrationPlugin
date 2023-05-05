package plugin.discordintegrationplugin.Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class PlayerDeathsEvent implements Listener {

    public JDA jda;
    private final JavaPlugin plugin;
    public PlayerDeathsEvent(JavaPlugin plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        //Event variables
        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerWorld = player.getWorld().getName();
        String deathMessage = event.getDeathMessage();
        String hexColor = "#000000";

        //Checking if the player name contains a floodgate prefix
        playerName = playerName.replace("*","").replace(".","");
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
                    embed.setAuthor(deathMessage, null, playerAvatar);
                    embed.setColor(Color.decode(hexColor));
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }
    }

}
