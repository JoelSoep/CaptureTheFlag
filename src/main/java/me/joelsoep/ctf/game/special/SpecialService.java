package me.joelsoep.ctf.game.special;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.RandomUtil;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.*;

public class SpecialService {
    private final Map<String, BaseSpecial> specials;

    public SpecialService() {
        this.specials = new HashMap<>();
    }

    public void loadSpecials() {
        Reflections reflections = new Reflections(FileManager.SPECIAL_PATH);
        Set<Class<? extends BaseSpecial>> specialClasses = reflections.getSubTypesOf(BaseSpecial.class);

        for (Class<? extends BaseSpecial> specialClass : specialClasses) {
            try {
                BaseSpecial special = specialClass.getDeclaredConstructor().newInstance();
                Bukkit.getPluginManager().registerEvents(special, Main.getInstance());

                specials.put(special.getName().toLowerCase(), special);
                LogModule.SPECIAL.log("Registered special item " + special.getName());
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
        int randomIndex = RandomUtil.get().nextInt(list.size());
        BaseSpecial special = list.get(randomIndex);
        if (special == null) {
            LogModule.SPECIAL.log("Special is null");
            return null;
        }
        return special;
    }


}
