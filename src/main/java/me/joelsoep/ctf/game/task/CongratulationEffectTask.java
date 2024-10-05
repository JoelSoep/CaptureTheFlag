package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class CongratulationEffectTask {

    private ArrayList<CTFPlayer> winner;
    private final Color color;
    private final int amount;

    private Integer id;
    private int turns = 0;

    public CongratulationEffectTask(ArrayList<CTFPlayer> winner, Color color, Integer amount) {
        this.winner = winner;
        this.color = color;
        this.amount = amount;
        this.winner = new ArrayList<CTFPlayer>(winner);
        run();
    }

    public void run() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (CTFPlayer ctfPlayer : winner) {
                    if (ctfPlayer == null) {
                        return;
                    }

                    Player player = ctfPlayer.getPlayer();
                    Location location = player.getLocation().clone().add(0, 2, 0);
                    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
                    FireworkMeta meta = firework.getFireworkMeta();

                    meta.setPower(2); // Set power to 0 for immediate detonation
                    meta.addEffect(FireworkEffect.builder()
                            .withColor(color)
                            .flicker(true)
                            .trail(true)
                            .with(FireworkEffect.Type.BURST)
                            .withFade(Color.WHITE)
                            .build());

                    firework.setFireworkMeta(meta);
                }
                if (amount == turns) {
                    cancel();
                }
                turns++;
            }
        }, 10L, 30L);
    }

    public void cancel() {
        if (id == null) {
            return;
        }
        Bukkit.getScheduler().cancelTask(id);
    }
}
