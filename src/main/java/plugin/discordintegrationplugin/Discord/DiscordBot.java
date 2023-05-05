package plugin.discordintegrationplugin.Discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import plugin.discordintegrationplugin.Configurations.PluginConfig;

public class DiscordBot {

    public JDA jda;
    private final PluginConfig config;
    public DiscordBot(PluginConfig config) {
        this.config = config;
    }

    public void enableBot() {
        //DiscordBot variables
        String botToken = this.config.getString("botToken");
        if (botToken == null || botToken.equals("botToken")) return;
        String activityName = this.config.getString("activityName");
        if (activityName == null) activityName = "Minecraft";

        //Enabling the bot
        try {
            this.jda = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.playing(activityName))
                    .build();
            this.jda.awaitReady();
            this.setJDA(jda);
            Bukkit.getLogger().info("Discord bot successfully enabled!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableBot() {
        //DiscordBot variables
        String botToken = this.config.getString("botToken");
        if (botToken == null || botToken.equals("botToken")) return;

        //Disabling the bot
        try {
            this.jda.shutdown();
            Bukkit.getLogger().info("Discord bot successfully disabled!");
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

}
