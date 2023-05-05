package plugin.discordintegrationplugin.Events;

import net.dv8tion.jda.api.JDA;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import plugin.discordintegrationplugin.Configurations.PluginConfig;

public class PlayerChangeWorldEvent implements Listener {

    public JDA jda;
    private final JavaPlugin plugin;
    private final PluginConfig config;
    public PlayerChangeWorldEvent(JavaPlugin plugin, JDA jda) {
        this.config = new PluginConfig(plugin);
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
        String joinMessage = config.getString("joinMessage");
        String leaveMessage = config.getString("leaveMessage");
        String joinHexColor = "#00FF00";
        String leaveHexColor = "#FF0000";
    }

}
