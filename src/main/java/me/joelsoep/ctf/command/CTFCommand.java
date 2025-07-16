package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.game.gui.PlusMenuGui;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.server.CTFServer;
import me.joelsoep.ctf.server.packet.packets.AlertPacket;
import me.joelsoep.ctf.server.packet.packets.ScheduleRestartPacket;
import me.joelsoep.ctf.server.packet.packets.StoreReloadPacket;
import me.joelsoep.ctf.server.socket.SocketSender;
import me.joelsoep.ctf.store.StoreCosmeticItem;
import me.joelsoep.ctf.store.StoreManager;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.Version;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

@CommandAlias("ctf")
public final class CTFCommand extends BaseCommand {

    @Default
    @CatchUnknown
    @Description("Get basic information about the plugin.")
    @Subcommand("version")
    public void version(Player player) {
        String isElevated = player.hasPermission("ctf.admin") || player.isOp() ? "<gray>You are an <yellow>administrative <gray>user." : "<gray>You are a <yellow>default <gray>user.";
        String hasSubcommands = player.hasPermission("ctf.admin") || player.isOp() ? "" : "<gray>You do not have permission to use any sub commands.";
        player.sendMessage(MM.deserialize("""
                <b><gradient:#5CFF74:#54F3E6>Capture</gradient><gradient:#54F3E6:#5CFF74> the Flag</gradient></b>
                <gray>Running Capture the Flag <yellow>v.%s <gray>by <yellow>JoelSoep<gray>.
                <gray>This server is <yellow>%s <gray>for non-admins.
                %s""".formatted(Version.get(), Main.getSettings().isOpen() ? "joinable" : "not joinable", isElevated)));
    }

    @CommandPermission("ctf.admin|op")
    @Description("Switches elevation state for server operators and administrators.")
    @Subcommand("elevate")
    public void elevate(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage("Couldn't fetch profile!");
            return;
        }

        boolean update = !ctfPlayer.isElevated();
        ctfPlayer.setElevated(update);
        player.sendMessage(MM.deserialize("<gray>Updated elevation state to <yellow>%s<gray>.".formatted(update)));
    }

    @CommandPermission("ctf.admin|op")
    @Description("Opens or closes the server for non-admins.")
    @Subcommand("setJoinable")
    public void setJoinable(Player player, boolean value) {
        Main.getSettings().setOpen(value);
        player.sendMessage(MM.deserialize("<gray>Updated server joinable state to <yellow>%s<gray>.".formatted(value)));
    }

    @CommandPermission("ctf.admin|op")
    @Description("Alert all servers with a message.")
    @Subcommand("alert")
    public void alert(Player player, String message) {
        if (!CTFServer.isEnabled()) {
            PlayerService.getAudience().sendMessage(MM.deserialize("<gray>[<red><bold>CTF-ALERT</bold><gray>] <red>" + message));
            return;
        }

        AlertPacket packet = new AlertPacket.Builder()
                .withDefaultValues()
                .withMessage(message)
                .build();

        SocketSender.sendAll(packet, true);
    }

    @CommandPermission("ctf.serveradmin")
    @Description("Lists all running ctf-servers.")
    @Subcommand("servers")
    public void servers(Player player) {
        if (!CTFServer.isEnabled()) {
            player.sendMessage(MM.deserialize("<gray>Server mode is disabled. There are no registered servers."));
            return;
        }

        player.sendMessage(MM.deserialize("<gray>There are currently %s registered servers:".formatted(Main.getGameServer().getOnlineServers().size() + 1)));
        AtomicReference<Component> message = new AtomicReference<>(MM.deserialize(""));
        Main.getGameServer().getOnlineServers().forEach((name,server) -> {
            message.set(message.get().append(MM.deserialize("<gray>" + name + ": %s:%s\n".formatted(server.ip(), server.port()))));
        });
        message.set(message.get().append(MM.deserialize("<gray>%s: %s:%s".formatted(
                Main.getGameServer().getId(), Main.getGameServer().getIp(), Main.getGameServer().getPort()
        ))));
        message.set(
                message.get().replaceText(TextReplacementConfig.builder()
                .match("\\n")
                .replacement("\n")
                .build())
        );
        player.sendMessage(message.get());
    }

    @CommandPermission("ctf.serveradmin")
    @Description("Reloads all ctf stores network-wide")
    @Subcommand("reload-store")
    public void reloadStore(Player player) {
        if (!CTFServer.isEnabled()) {
            player.sendMessage(MM.deserialize("<gray>Server mode is disabled. Cannot reload stores."));
            return;
        }

        StoreReloadPacket packet = new StoreReloadPacket.Builder().withDefaultValues().build();
        SocketSender.sendAll(packet, true);
    }

    @CommandPermission("ctf.serveradmin")
    @Description("Schedules a global server restart as soon as the next game ends, or instantly if none is ongoing.")
    @Subcommand("restart-global")
    public void restartGlobal(Player player) {
        if (!CTFServer.isEnabled()) {
            player.sendMessage(MM.deserialize("<gray>Server mode is disabled. Cannot perform a global restart."));
            return;
        }

        ScheduleRestartPacket packet = new ScheduleRestartPacket.Builder().withDefaultValues().build();
        SocketSender.sendAll(packet, true);
    }

    @CommandPermission("ctf.plus|ctf.admin")
    @Description("Opens the Plus Menu")
    @Subcommand("plusmenu")
    public void openPlusMenu(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
        if (ctfPlayer == null) {
            return;
        }

        new PlusMenuGui(ctfPlayer);
    }

    @CommandPermission("ctf.serveradmin")
    @Description("Changes the availability of a cosmetic in the store.")
    @Subcommand("set-cosmetic-availability")
    public void setCosmeticAvailability(Player player, @Single String name, boolean value) {
        @Nullable StoreCosmeticItem item = StoreManager.getCosmeticItem(name);
        if (item == null) {
            player.sendMessage(MM.deserialize("<red>Could not find item."));
            return;
        }

        if (value) {
            item.setNotExpire();
        } else {
            item.setExpire();
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                StoreManager.getCloud().pushItem(item).thenAccept(action -> {
                    Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            player.sendMessage(MM.deserialize(action ? "<green>Done!" : "<red>An error occurred."));
                        }
                    });
                });
            }
        });
    }


}
