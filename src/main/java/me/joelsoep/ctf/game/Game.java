package me.joelsoep.ctf.game;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.game.event.PlayerJoinGameEvent;
import me.joelsoep.ctf.game.event.PlayerLeaveGameEvent;
import me.joelsoep.ctf.game.task.CountdownTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.game.team.TeamColor;
import me.joelsoep.ctf.game.team.TeamService;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.player.experience.Level;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class Game {

    private UUID uuid = UUID.randomUUID();
    private GameMap map;
    private Integer playerCount = 0;

    /**
     * max and minplayers are deprecated and scheduled for removal, as this should be map-specific
     */

    @Deprecated
    private Integer maxPlayers = 20;

    @Deprecated
    private Integer minPlayers = 2;


    private Integer maxPoints = 3;
    private Integer minKillStreak = 1;
    private Long gameTime = 10L * 60L * 1000L;
    private Long startTime;
    private Team red;
    private Team blue;
    private Audience audience;
    private GameState state;
    private ArrayList<CTFPlayer> players = new ArrayList<>();
    private ArrayList<CTFPlayer> spectators = new ArrayList<>();
    private MatchExperienceCollector XPCollector = new MatchExperienceCollector();
    private @Nullable Team winner;
    private CountdownTask countdownTask;
    private boolean isStarting = false;
    private boolean isSpedUp = false;

    public Game() {
        this.audience = Audience.audience(concatAudience(players, spectators));
        this.state = GameState.WAITING;
    }

    /**
     * This class exists to collect all experience gained in a match.
     * After the match, we deliver the experience to the player via sendAll()
     * and clear the collector.
     */

    public static class MatchExperienceCollector {
        private final Map<@NotNull String, @NotNull Integer> collector;

        public MatchExperienceCollector() {
            this.collector = new HashMap<>();
        }

        public void write(CTFPlayer player, int xp) {
            if (player == null) {
                return;
            }

            Integer potentialXP = collector.get(player.getPlayer().getName());
            if (potentialXP == null) {
                potentialXP = 0;
            }
            collector.put(player.getPlayer().getName(), potentialXP + xp);
        }

        public int read(CTFPlayer player) {
            if (player == null) {
                return 0;
            }

            Integer xp = this.collector.get(player.getPlayer().getName());
            return xp != null ? xp : 0;
        }

        public void sendAll() {
            this.collector.forEach((string, xp) -> {
                CTFPlayer player = PlayerService.getPlayers().get(string);
                if (player == null) {
                    LogModule.GAME.log("player %s is null while sending xp".formatted(string));
                    return;
                }

                Level.gainExperience(player, xp);
            });
        }

        public void empty() {
            this.collector.clear();
        }
    }

    public void reset() {
        this.setUuid(UUID.randomUUID());
        this.setPlayerCount(0);
        this.setMinPlayers(2);
        this.setMaxPoints(3);
        this.setMinKillStreak(1);
        this.setState(GameState.WAITING);
        this.setWinner(null);
        this.setCountdownTask(null);
        this.setStarting(false);
        this.setSpedUp(false);
        this.setMap(MapManager.getRandomGameMap());
        this.setGameTime(10L * 60L * 1000L);
        TeamService.getTeams().clear();
        this.createTeams(this.getMap());
        this.setAudience(Audience.audience(concatAudience(players, spectators)));
        MapManager.registerMapToGame(this.getMap(), this);
    }

    public void addPlayer(CTFPlayer player) {
        if (players.size() >= this.getMap().getMaxPlayers()) {
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
        Bukkit.getWorlds().forEach(world -> LogModule.GAME.log(world.getName()));
        this.red = TeamService.createTeam(
                TeamColor.RED,
                map.getRedSpawnLocation(),
                map.getRedFlagLocation()
        );
        this.red.getFlag().setMaterial(Material.RED_BANNER);

        this.blue = TeamService.createTeam(
                TeamColor.BLUE,
                map.getBlueSpawnLocation(),
                map.getBlueFlagLocation()
        );
        this.blue.getFlag().setMaterial(Material.BLUE_BANNER);
    }

    private List<CTFPlayer> concatAudience(List<CTFPlayer> arr1, List<CTFPlayer> arr2) {
        return Stream.concat(arr1.stream(), arr2.stream())
                .collect(Collectors.toList());
    }
}
