package me.joelsoep.ctf.map.type.lobby;

import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.game.gui.DesertLobbyJukeboxGui;
import me.joelsoep.ctf.map.type.LobbyMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.FAWE;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.CC;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DesertLobby extends LobbyMap {
    public DesertLobby() {
        super("desert", new File(FileManager.getSchematicFolderPath() + "lobby.schem"));
    }

    @Override
    public void load() {
        FAWE.paste(this.schematic, new Location(getWorld(), 0, 50, 0));
        placeJukebox();
    }

    @Override
    protected Location spawnLocation() {
        return new Location(getWorld(), 16, 35, 13, 138, 0);
    }

    @Override
    protected ArrayList<Location> leaderboardLocations() {
        return new ArrayList<>(List.of(
                new Location(getWorld(), 5, 39, 3),
                new Location(getWorld(), 5, 39, -3),
                new Location(getWorld(), 0, 39, 3),
                new Location(getWorld(), 0, 39, -3),
                new Location(getWorld(), 8, 39, 12)
        ));
    }

    public void placeJukebox() {
        Location location = new Location(this.getWorld(), 4, 35, 0);
        this.getWorld().getBlockAt(location).setType(Material.JUKEBOX);
        new Interaction(action -> {

           if (!action.getAction().isRightClick()) {
               return;
           }

           if (action.getClickedBlock() == null) {
               return;
           }

           if (action.getClickedBlock().getType() != Material.JUKEBOX) {
               return;
           }

            action.setCancelled(true);
            CTFPlayer player = PlayerService.getPlayer(action.getPlayer());
            if (player == null) {
                return;
            }

            if (player.hasCooldown()) {
                return;
            }

            player.setCooldown(1000L);
            if (!player.getPlayer().hasPermission("ctf.plus")) {
               action.getPlayer().sendMessage(MM.announce("<red>You need %s<b>PLUS</b> <red>in order to use the lobby jukebox!".formatted(CC.PLUS)));
               return;
           }

           // open gui
            new DesertLobbyJukeboxGui(player);
        });
    }
}
