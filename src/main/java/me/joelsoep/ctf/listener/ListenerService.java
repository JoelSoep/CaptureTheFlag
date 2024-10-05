package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.effect.EffectListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

public class ListenerService {

    public ListenerService(String packageName) {
        loadAndRegisterListeners(packageName);
        registerManually();
    }

    public void loadAndRegisterListeners(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends Listener>> classes = reflections.getSubTypesOf(Listener.class);

        classes.forEach(listenerClass -> {
            try {
                Constructor<? extends Listener> constructor;
                Listener listener;
                try {
                    constructor = listenerClass.getDeclaredConstructor(Plugin.class);
                    listener = constructor.newInstance(Main.getInstance());
                } catch (NoSuchMethodException e) {
                    constructor = listenerClass.getDeclaredConstructor();
                    listener = constructor.newInstance();
                }

                Bukkit.getServer().getPluginManager().registerEvents(listener, Main.getInstance());
                LogModule.CORE.log("Registered listener " + listener.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void registerManually() {
        Bukkit.getServer().getPluginManager().registerEvents(new EffectListener(), Main.getInstance());
    }
}
