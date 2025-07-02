package me.joelsoep.ctf.animation;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Setter
@Getter

public abstract class Animation {

    private Location location;
    private int taskId = 0;

    private Long delay;
    private Long interval;
    private Long duration;

    /**
     * only use this if you are sure that you will set the location;
     */
    public Animation() {
        this.delay = 0L;
        this.interval = 2L;
        this.duration = 0L;
    }

    public Animation(Location location) {
        this.location = location;
        this.delay = 0L;
        this.interval = 2L;
        this.duration = 0L;
    }

    public Animation(Location location, Long delay, Long interval) {
        this.location = location;
        this.delay = delay;
        this.interval = interval;
        this.duration = 0L;
    }

    protected abstract int animate(Location location);

    /**
     * Plays the animation. Please note that per animation instance, the animation can only be played via a runnable at one location.
     * Using the {@code Animation#play()} method again will cancel the existing one and replace it with the new one.
     *
     * @param delay The initial delay of the first cycle
     * @param interval The interval at which the animation plays
     * @return the BukkitTask UUID
     */

    public int play(Long delay, Long interval) {
        if (location == null) {
            throw new NullPointerException("Tried to play animation while location is null");
        }

        if (taskId == 0) {
            cancel();
        }

        this.setDelay(delay);
        this.setInterval(interval);

        taskId = animate(location);
        if (duration != null) {
         scheduleCancellation(duration);
        }

        return taskId;
    }

    public void play() {
        if (location == null) {
            throw new NullPointerException("Tried to play animation while location is null");
        }

        animate(getLocation());
    }

    public void cancel() {
        if (taskId == 0) {
            return;
        }

        Bukkit.getScheduler().cancelTask(taskId);
        taskId = 0;
    }

    public void scheduleCancellation(Long ticks) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                cancel();
            }
        }, ticks);
    }
}
