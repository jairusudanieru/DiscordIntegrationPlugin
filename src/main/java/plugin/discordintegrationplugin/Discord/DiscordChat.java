package plugin.discordintegrationplugin.Discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static net.md_5.bungee.api.ChatColor.of;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class DiscordChat extends ListenerAdapter {

    JavaPlugin plugin;
    public DiscordChat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        //Event variables
        boolean haveAttachment = event.getMessage().getAttachments().size() > 0;
        boolean authorIsBot = event.getAuthor().isBot();
        boolean authorIsSys = event.getAuthor().isSystem();
        String authorName = event.getAuthor().getName();
        String currentChannel = event.getChannel().getId();
        String roleName = plugin.getConfig().getString("roleName");
        String roleColor = plugin.getConfig().getString("roleHex");

        //Checking if the author is a bot or system
        if (authorIsBot || authorIsSys) return;

        //Checking if the role Color is null
        if (roleName == null || roleName.isEmpty()) roleName = "User";
        if (roleColor == null || roleColor.isEmpty()) roleColor = "#FFFFFF";

        //Message that will be sent on the Minecraft Server
        String messageContent = event.getMessage().getContentRaw();
        String messageAuthorTag = authorName + " >> ";
        String messagePrefix = "§r[§bDiscord§b | " + of(roleColor) + roleName + "§r] ";
        String fullMessage = messagePrefix + messageAuthorTag + messageContent;

        //Checking if the message have attachment/s, if yes get the attachment/s name
        if (haveAttachment) {
            List<Message.Attachment> attachmentsList = event.getMessage().getAttachments();
            for (Message.Attachment attachment : attachmentsList) {
                String attachmentName = attachment.getFileName();
                if (messageContent.isEmpty()) fullMessage = messagePrefix + messageAuthorTag + attachmentName;
                else fullMessage = messagePrefix + messageAuthorTag + messageContent + " " + attachmentName;
            }
        }

        //Getting the worlds, webhookUrl and channelId in the configuration
        ConfigurationSection worldGroups = plugin.getConfig().getConfigurationSection("worldGroups");
        if (worldGroups == null) return;
        Set<String> groupNames = worldGroups.getKeys(false);
        for (String groupName : groupNames) {
            String channelId = plugin.getConfig().getString("worldGroups." + groupName + ".channelId");

            //Checking which group the player's current world is
            if (!currentChannel.equals(channelId)) continue;
            List<String> worldNames = worldGroups.getStringList(groupName + ".worlds");
            for (Player player : Bukkit.getOnlinePlayers()) {
                String world = player.getWorld().getName();
                if (worldNames.contains(world)) {
                    player.sendMessage(fullMessage);
                    Bukkit.getLogger().info(fullMessage);
                }
            }
        }
    }
}
