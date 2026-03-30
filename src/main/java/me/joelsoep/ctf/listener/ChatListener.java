package me.joelsoep.ctf.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.joelsoep.ctf.InstanceSettings;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component message = event.originalMessage();
        if (Main.getSettings().isMuted() && !player.hasPermission("ctf.admin")) {
            player.sendMessage(MM.deserialize("<red>This server is currently muted."));
            event.setCancelled(true);
            return;
        }

        if (!LocalConfig.OVERRIDE_MCMANHUNT_CHAT_HANDLER) {
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("mcmanhunt-core");
        if (plugin == null) {
            LogModule.CORE.warning("Could not disable chat listener from MCManhunt-Core as the plugin is null.");
            return;
        }

        for (RegisteredListener listener : event.getHandlers().getRegisteredListeners()) {
            if (listener.getPlugin().getName().equals("MCManhunt-Core") && Main.getMcmhChatListener() == null) {
                Main.setMcmhChatListener(listener);
                event.getHandlers().unregister(plugin);
            }
        }

        if (event.isCancelled()) {
            event.setCancelled(false);
        }

        String prefix = player.hasPermission("ctf.admin") ? null : "<gray>%s</gray> <white>» ".formatted(player.getName());
        if (prefix == null) {
            try {
                Main.getMcmhChatListener().callEvent(event);
                event.setCancelled(true);
            } catch (EventException e) {
                e.printStackTrace();
            }
            return;
        }
        // <b><gradient:#DCE71F:#ECDE19>ᴘʟᴜѕ</gradient></b>
        if (player.hasPermission("ctf.plus")) {
            prefix = "<gradient:#DCE71F:#ECDE19>%s</gradient> <white>» ".formatted(player.getName());
        }

        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            return;
        }

        if (ctfPlayer.getTeam() != null) {
            prefix = ctfPlayer.getTeam().getTeamColor().getPrefix() + " " + prefix;
        }

        PlayerService.getAudience().sendMessage(MM.deserialize(prefix).append(message));
        event.setCancelled(true);
    }
}
