package plugin.discordintegrationplugin;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.discordintegrationplugin.Discord.DiscordBot;
import plugin.discordintegrationplugin.Events.*;

public final class DiscordIntegrationPlugin extends JavaPlugin {

    private DiscordBot discordBot;

    //The events to register
    public void registerEvents() {
        JDA jda = discordBot.jda;
        Bukkit.getPluginManager().registerEvents(new PlayerAdvancementEvent(this, jda), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangeWorldEvent(this, jda), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatEvent(this, jda), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathsEvent(this, jda), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinLeaveEvent(this, jda), this);
        Plugin authMe = Bukkit.getServer().getPluginManager().getPlugin("AuthMe");
        if (authMe != null) Bukkit.getPluginManager().registerEvents(new PlayerAuthMeEvent(this, jda), this);
    }

    //The status start message to send
    public void statusStart() {
        try {
            JDA jda = discordBot.jda;
            boolean enabled = getConfig().getBoolean("serverStatus.enabled");
            String channelId = getConfig().getString("serverStatus.channelId");
            String startMessage = getConfig().getString("serverStatus.startMessage");
            if (channelId == null || channelId.isEmpty() || !enabled) return;
            TextChannel textChannel = jda.getTextChannelById(channelId);
            if (textChannel == null || startMessage == null) return;
            textChannel.sendMessage(startMessage).queue();
            Bukkit.getLogger().info("[Discord-Integration] Plugin has successfully enabled!");
        } catch (Exception error) {
            Bukkit.getLogger().severe("[Discord-Integration] Plugin has failed to enable!");
        }
    }

    //The status stop message to send
    public void statusStop() {
        try {
            JDA jda = discordBot.jda;
            boolean enabled = getConfig().getBoolean("serverStatus.enabled");
            String channelId = getConfig().getString("serverStatus.channelId");
            String stopMessage = getConfig().getString("serverStatus.stopMessage");
            if (channelId == null || channelId.isEmpty() || !enabled) return;
            TextChannel textChannel = jda.getTextChannelById(channelId);
            if (textChannel == null || stopMessage == null) return;
            textChannel.sendMessage(stopMessage).queue();
            Bukkit.getLogger().info("[Discord-Integration] Plugin has successfully disabled!");
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        this.discordBot = new DiscordBot(this);
        String botToken = getConfig().getString("botToken");
        if (botToken == null || botToken.equals("botToken") || botToken.isEmpty()) return;
        discordBot.enableBot();
        registerEvents();
        statusStart();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        String botToken = getConfig().getString("botToken");
        if (botToken == null || botToken.equals("botToken") || botToken.isEmpty()) return;
        statusStop();
        discordBot.disableBot();
    }
}
