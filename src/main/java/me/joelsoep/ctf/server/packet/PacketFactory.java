package me.joelsoep.ctf.server.packet;

import me.joelsoep.ctf.logger.LogModule;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanners;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PacketFactory {

    private static final Map<String, Method> registeredBuilders = new HashMap<>();

    public static void init() {
        System.out.println("Booting PacketFactory...");
        Reflections reflections = new Reflections("me.joelsoep.ctf.server.packet.packets", Scanners.MethodsAnnotated);

        Set<Method> methods = reflections.getMethodsAnnotatedWith(PacketConstructor.class);
        System.out.printf("Found %s methods to register to factory!%n", methods.size());

        for (Method method : methods) {
            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            if (method.getParameterCount() != 1 || !method.getParameterTypes()[0].equals(JSONObject.class)) {
                continue;
            }

            PacketConstructor annotation = method.getAnnotation(PacketConstructor.class);
            if (annotation == null) { // silence IDE
                continue;
            }

            registeredBuilders.put(annotation.action().toUpperCase(Locale.ROOT), method);
            System.out.printf("Registered packet builder for action %s%n", annotation.action());
        }
    }

    public static @Nullable CTFPacket fromJSON(String action, JSONObject data) {
        Method method = registeredBuilders.get(action.toUpperCase(Locale.ROOT));
        if (method == null) {
            LogModule.CORE.warning("No builder implementation found for packet with action %s".formatted(action));
            return null;
        }

        try {
            return (CTFPacket) method.invoke(null, data);
        } catch (Exception e) {
            LogModule.CORE.warning("Incorrect builder implementation for packet with action %s".formatted(action));
            e.printStackTrace();
            return null;
        }
    }
}
