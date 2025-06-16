package me.joelsoep.ctf.server.listener;

import lombok.Data;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.server.packet.CTFPacket;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class ServerListenerService {

    private final HashMap<Class<? extends CTFPacket>, List<MethodHandler>> firingMethods;

    public ServerListenerService(String packageName) {
        firingMethods = new HashMap<>();
        loadListeners(packageName);
    }

    public void loadListeners(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes);
        Set<Class<? extends ServerListener>> classes = reflections.getSubTypesOf(ServerListener.class);
        LogModule.CORE.log("Found %s listeners to check in server-listener service.".formatted(classes.size()));

        classes.forEach(listenerClass -> {
            try {
                Constructor<? extends ServerListener> constructor;
                ServerListener listener;

                    constructor = listenerClass.getDeclaredConstructor();
                    listener = constructor.newInstance();

                registerListener(listener);
                LogModule.CORE.log("Registered server-listener %s".formatted(listener.getClass().getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void registerListener(ServerListener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PacketHandler.class)) {
                LogModule.CORE.log("fail annotation");
                continue;
            }

            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length != 1) {
                LogModule.CORE.log("param length fail");
                return;
            }
            if (!CTFPacket.class.isAssignableFrom(parameters[0])) {
                LogModule.CORE.log("fail check " + method.getName());
                return;
            }

            Class<? extends CTFPacket> packetClass = parameters[0].asSubclass(CTFPacket.class);
            method.setAccessible(true);

            LogModule.CORE.log("Found applicable method %s#%s. Registering.".formatted(packetClass.getSimpleName(), method.getName()));
            firingMethods.computeIfAbsent(packetClass, k -> new ArrayList<>())
                    .add(new MethodHandler(method, listener));
        }
    }

    public void dispatchPacket(CTFPacket packet) {
        List<MethodHandler> handlers = firingMethods.get(packet.getClass());

        if (handlers == null || handlers.isEmpty()) {
            LogModule.CORE.warning("No handler for packet %s".formatted(packet.getClass().getSimpleName()));
            return;
        }

        for (MethodHandler handler : handlers) {
            try {
                handler.method().invoke(handler.instance(), packet);
            } catch (Exception e) {
                LogModule.CORE.warning("Failed to invoke handler for " + packet.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
    }


}
