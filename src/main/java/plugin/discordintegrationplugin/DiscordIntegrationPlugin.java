package plugin.discordintegrationplugin;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.discordintegrationplugin.Configurations.PluginConfig;
import plugin.discordintegrationplugin.Discord.DiscordBot;
import plugin.discordintegrationplugin.Events.PlayerAuthMeEvent;
import plugin.discordintegrationplugin.Events.PlayerChangeWorldEvent;
import plugin.discordintegrationplugin.Events.PlayerChatEvent;
import plugin.discordintegrationplugin.Events.PlayerJoinLeaveEvent;

public final class DiscordIntegrationPlugin extends JavaPlugin {

    private DiscordBot discordBot;
    private PluginConfig config;

    //The events to register
    public void registerEvents() {
        JDA jda = discordBot.jda;
        Bukkit.getPluginManager().registerEvents(new PlayerChatEvent(this, jda), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangeWorldEvent(this, jda), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinLeaveEvent(this, jda), this);
        Plugin authMe = Bukkit.getServer().getPluginManager().getPlugin("AuthMe");
        if (authMe != null) Bukkit.getPluginManager().registerEvents(new PlayerAuthMeEvent(this, jda), this);
    }

    //The status start message to send
    public void statusStart() {
        try {
            JDA jda = discordBot.jda;
            boolean enabled = config.getBoolean("serverStatus.enabled");
            String channelId = config.getString("serverStatus.channelId");
            String startMessage = config.getString("serverStatus.startMessage");
            if (channelId == null || !enabled) return;
            TextChannel textChannel = jda.getTextChannelById(channelId);
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
            boolean enabled = config.getBoolean("serverStatus.enabled");
            String channelId = config.getString("serverStatus.channelId");
            String stopMessage = config.getString("serverStatus.stopMessage");
            if (channelId == null || !enabled) return;
            TextChannel textChannel = jda.getTextChannelById(channelId);
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
        this.config = new PluginConfig(this);
        this.discordBot = new DiscordBot(this.config);
        String botToken = this.config.getString("botToken");
        if (botToken == null || botToken.equals("botToken")) return;
        discordBot.enableBot();
        registerEvents();
        statusStart();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        String botToken = config.getString("botToken");
        if (botToken == null || botToken.equals("botToken")) return;
        statusStop();
        discordBot.disableBot();
    }
}
