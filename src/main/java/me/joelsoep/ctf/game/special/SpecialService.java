package me.joelsoep.ctf.game.special;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.*;
import java.util.logging.Level;

public class SpecialService {
    private final Map<String, BaseSpecial> specials;
    private final Plugin plugin;
    private Random random = new Random();

    public SpecialService() {
        this.plugin = Bukkit.getPluginManager().getPlugin("CapturetheFlag");
        this.specials = new HashMap<>();
    }

    public void loadSpecials(String packageName, Plugin plugin) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends BaseSpecial>> specialClasses = reflections.getSubTypesOf(BaseSpecial.class);

        for (Class<? extends BaseSpecial> specialClass : specialClasses) {
            try {
                BaseSpecial special = specialClass.getDeclaredConstructor().newInstance();
                if (plugin == null) {
                    Bukkit.getLogger().log(Level.WARNING, "Couldn't register special item " + special.getName() + " because plugin is null.");
                    return;
                }
                Bukkit.getPluginManager().registerEvents(special, plugin);

                specials.put(special.getName().toLowerCase(), special);
                Bukkit.getLogger().log(Level.INFO, "Registered special item " + special.getName());
            } catch (ReflectiveOperationException e) {
                // ReflectiveOperationException is the parent class of various reflective exceptions
                e.printStackTrace();
            }
        }
    }

    public List<BaseSpecial> getAllSpecials() {
        return new ArrayList<>(specials.values());
    }

    public BaseSpecial<?> getSpecial(String special) {
        return specials.get(special.toLowerCase());
    }

    public void giveSpecial(CTFPlayer player, String specialName) {
        BaseSpecial<?> special = getSpecial(specialName);
        if (special != null) {
            special.giveSpecialToPlayer(player);
            return;
        }
        MM.deserialize("<#f2180c>This special doesn't exist. Please report this to an administrator.");
    }

    public BaseSpecial<?> getRandomSpecial() {
        ArrayList<BaseSpecial> list = new ArrayList<>(specials.values());
        if (list.isEmpty()) {
            return null;
        }
        int randomIndex = random.nextInt(list.size());
        BaseSpecial special = list.get(randomIndex);
        if (special == null) {
            Bukkit.getLogger().log(Level.INFO, "Special is null");
            return null;
        }
        return special;
    }


}
