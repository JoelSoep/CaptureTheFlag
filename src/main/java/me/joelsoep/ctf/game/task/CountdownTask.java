package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.GameStartEvent;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;

import java.time.Duration;

public class CountdownTask {

    private final Component message;
    private Audience audience;
    private final boolean isGameStartCountdown;
    private int seconds;
    private Game game;
    private int id;

    public CountdownTask(Component message, Audience audience, int seconds) {
        this.message = message;
        this.audience = audience;
        this.seconds = seconds;
        this.isGameStartCountdown = false;
        countdown();
    }

    public CountdownTask(Component message, Audience audience, int seconds, boolean isGameStartCountdown, Game game) {
        this.message = message;
        this.audience = audience;
        this.seconds = seconds;
        this.isGameStartCountdown = isGameStartCountdown;
        this.game = game;
        countdown();
    }

    public void countdown() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (seconds == 45) {
                    message(45);
                }
                if (seconds == 30)  {
                    message(30);
                }
                if (seconds == 15) {
                    message(15);
                }

                if (seconds < 6 && seconds > 0) {
                    message(seconds);
                }

                if (seconds == 0) {
                    LogModule.GAME.log("SECONDS = 0 @ Countdowntask");
                    if (isGameStartCountdown && game.getState() == GameState.READY) {
                        game.setStartTime(System.currentTimeMillis());
                        game.setGameTime(System.currentTimeMillis() + ((Long) game.getGameSettings().getSetting(GameSetting.TIME) * 60L * 1000L));
                        game.setState(GameState.RUNNING);
                        new GameTimerTask(game);
                        LogModule.GAME.log("Updated game state to RUNNING");
                        if (game.getPlayers().size() == 0) {
                            LogModule.GAME.log("CountdownTask: Activated 0-player-function.");
                            game.setState(GameState.ENDED);
                            game.getMap().placeFlag(game.getBlue());
                            game.getMap().placeFlag(game.getRed());
                            game.getBlue().getPlayers().clear();
                            game.getRed().getPlayers().clear();
                            new ResetGameTask(game);
                        }
                    }

                    audience.sendMessage(message);
                    audience.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1, 2));
                    if (isGameStartCountdown && game.getState() == GameState.WAITING) {
                        game.getGameSettings().calculateVotesAndSet();
                        GameStartEvent event = new GameStartEvent(game);
                        Bukkit.getPluginManager().callEvent(event);
                        LogModule.GAME.log("GameStartEvent trigger @Countdowntask");
                    }
                    cancel();
                }
                seconds--;
            }
        }, 20L, 20L);
    }

    public void updateAudience(Audience audience) {
        this.audience = audience;
    }
    public void message(int seconds) {
        if (this.isGameStartCountdown && game.getState() == GameState.READY) {
            Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ofMillis(500L));
            Title title = Title.title(MM.deserialize("<green><bold>%s".formatted(seconds)), MM.deserialize(""), times);
            audience.showTitle(title);
        } else {
            audience.sendMessage(MM.announce("<gray>Starting in <green>%s<gray> seconds.".formatted(seconds)));
        }
        audience.playSound(Sound.sound(Key.key("block.note_block.hat"), Sound.Source.MASTER, 1, 1));
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }

    public int getSeconds() {
        return this.seconds;
    }

}
