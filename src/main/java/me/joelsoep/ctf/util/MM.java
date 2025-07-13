package me.joelsoep.ctf.util;

import me.joelsoep.ctf.util.test.CC;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MM {

    public static Component deserialize(String input) {
        return MiniMessage.miniMessage().deserialize("<!i>" + input);
    }

    public static Component deserializeRaw(String input) {
        return MiniMessage.miniMessage().deserialize(input);
    }

    public static Component deserializeGame(String input) {
        Component component = MiniMessage.miniMessage().deserialize(input);
        component.font(Key.key("game"));
        return component;
    }

    public static Component announce(String input) {
        return MiniMessage.miniMessage().deserialize("<!i><b><gradient:#5CFF74:#54F3E6>CTF</gradient></b> <b><gray>»<reset> " + input);
    }

    public static Component alert(String input) {
        return MiniMessage.miniMessage().deserialize("<gray>[<red><bold>CTF-ALERT</bold><gray>] <gray>" + input);
    }

    public static Component getStoreMessage() {
        return MiniMessage.miniMessage().deserialize("<gray>Buy <b>%sPLUS</b> <gray>in the store: <yellow><u><click:open_url:'https://store.mcmanhunt.com/'>store.mcmanhunt.com".formatted(CC.PLUS));
    }

}
