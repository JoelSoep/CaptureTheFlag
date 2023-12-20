package me.joelsoep.ctf.game;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.game.event.PlayerJoinGameEvent;
import me.joelsoep.ctf.game.event.PlayerLeaveGameEvent;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.game.team.TeamColor;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class Game {

    private UUID uuid = UUID.randomUUID();
    private GameMap map;
    private Integer playerCount = 0;
    private Integer maxPlayers = 20;
    private Integer minPlayers = 2;
    private Integer maxPoints = 3;
    private Integer minKillStreak = 1;
    private Team red;
    private Team blue;
    private Audience audience;
    private GameState state;
    private ArrayList<CTFPlayer> players = new ArrayList<>();
    private ArrayList<CTFPlayer> spectators = new ArrayList<>();
    private Team winner;
    private boolean isStarting = false;
    private boolean isSpedUp = false;

    public Game() {
        this.audience = Audience.audience(concatAudience(players, spectators));
        this.state = GameState.WAITING;
    }

    public void reset() {
        this.setUuid(UUID.randomUUID());
        this.setPlayerCount(0);
        this.setMaxPlayers(20);
        this.setMinPlayers(2);
        this.setMaxPoints(3);
        this.setMinKillStreak(1);
        this.setState(GameState.WAITING);
        this.setWinner(null);
        this.setStarting(false);
        this.setSpedUp(false);
        this.setAudience(Audience.audience(concatAudience(players, spectators)));
    }

    public void addPlayer(CTFPlayer player) {
        if (players.size() >= this.getMaxPlayers()) {
            player.sendMessage(MM.deserialize("""
                    <red>Couldn't add you to the game.
                    <red>Reason: The game has reached the maximum amount of players."""));
            player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1, (float) 0.5));
            return;
        }
        players.add(player);
        player.setGame(this);
        playerCount++;
        this.audience = Audience.audience(concatAudience(players, spectators));
        PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void removePlayer(CTFPlayer player) {
        players.remove(player);
        player.setGame(null);
        playerCount--;
        this.audience = Audience.audience(concatAudience(players, spectators));
        PlayerLeaveGameEvent event = new PlayerLeaveGameEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void addSpectator(CTFPlayer player) {
        spectators.add(player);
        player.setGame(null);
        this.audience = Audience.audience(concatAudience(players, spectators));
    }

    public void removeSpectator(CTFPlayer player) {
        spectators.remove(player);
        player.setGame(null);
        this.audience = Audience.audience(concatAudience(players, spectators));
    }

    public void createTeams(GameMap map) {
        Bukkit.getWorlds().forEach(world -> Bukkit.getLogger().info(world.getName()));
        this.red = new Team(
                TeamColor.RED,
                map.getRedSpawnLocation(),
                map.getRedFlagLocation()
        );
        this.red.getFlag().setBlockFace(BlockFace.NORTH);
        this.red.getFlag().setMaterial(Material.RED_BANNER);

        this.blue = new Team(
                TeamColor.BLUE,
                map.getBlueSpawnLocation(),
                map.getBlueFlagLocation()
        );
        this.blue.getFlag().setBlockFace(BlockFace.SOUTH);
        this.blue.getFlag().setMaterial(Material.BLUE_BANNER);
    }

    public List<CTFPlayer> concatAudience(List<CTFPlayer> arr1, List<CTFPlayer> arr2) {
        return Stream.concat(arr1.stream(), arr2.stream())
                .collect(Collectors.toList());
    }
}
