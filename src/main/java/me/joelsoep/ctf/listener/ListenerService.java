package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.game.kit.BaseKit;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class ListenerService {

    private final Plugin plugin;

    public ListenerService(Plugin plugin) {
        this.plugin = plugin;
        loadAndRegisterListeners("me.joelsoep.ctf.listener");
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
                    listener = constructor.newInstance(plugin);
                } catch (NoSuchMethodException e) {
                    constructor = listenerClass.getDeclaredConstructor();
                    listener = constructor.newInstance();
                }

                Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
                Bukkit.getLogger().info("Registered listener " + listener.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
