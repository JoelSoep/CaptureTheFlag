package me.joelsoep.ctf.game.team;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.player.CTFPlayer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemDisplay;

import javax.annotation.Nullable;
import java.util.ArrayList;

@Getter
@Setter
public class Team {

    private ArrayList<CTFPlayer> players = new ArrayList<>();
    private Integer points = 0;
    private TeamColor teamColor;
    private Audience audience;
    private Location spawn;
    private Flag flag;

    protected Team(TeamColor teamColor, Location spawn, Location flagLocation) {
        this.flag = new Flag();
        this.teamColor = teamColor;
        this.spawn = spawn;
        this.flag.location = flagLocation;
        this.flag.display = null;
        this.audience = Audience.audience(players);
    }

    @Data
    public static class Flag {
        private Location location;
        private Material material;
        private @Nullable ItemDisplay display;
        private @Nullable CTFPlayer thief;
    }

    public void addPlayer(CTFPlayer player) {
        players.add(player);
        player.setTeam(this);
        this.audience = Audience.audience(players);
    }

    public void removePlayer(CTFPlayer player) {
        players.remove(player);
        player.setTeam(null);
        this.audience = Audience.audience(players);
    }

}