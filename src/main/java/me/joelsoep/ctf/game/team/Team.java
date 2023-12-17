package me.joelsoep.ctf.game.team;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.player.CTFPlayer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

@Getter
@Setter
public class Team {

    private ArrayList<CTFPlayer> players = new ArrayList<>();
    private Integer points = 0;
    private TeamColor teamColor;
    private Audience audience;
    private Location spawn;
    private Location flagLocation;
    private Material flagMaterial;
    private BlockFace flagBlockFace;
    private CTFPlayer flagThief;

    public Team(TeamColor teamColor, Location spawn, Location flag) {
        this.teamColor = teamColor;
        this.spawn = spawn;
        this.flagLocation = flag;
        this.audience = Audience.audience(players);
    }

    public void addPlayer(CTFPlayer player) {
        players.add(player);
        this.audience = Audience.audience(players);
    }

    public void removePlayer(CTFPlayer player) {
        players.remove(player);
        this.audience = Audience.audience(players);
    }

}