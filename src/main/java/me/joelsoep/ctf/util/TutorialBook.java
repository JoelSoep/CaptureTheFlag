package me.joelsoep.ctf.util;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class TutorialBook {

    public TutorialBook(Player player) {
        Book book = Book.builder()
                .title(getTitle())
                .author(getAuthor())
                .addPage(getFrontPage())
                .build();

        player.openBook(book);
    }

    public Component getTitle() {
        return MM.deserialize("<aqua><bold>CAPTURE THE FLAG");
    }

    public Component getAuthor() {
        return MM.deserialize("<##FFD700>The developer of Capture the Flag");
    }

    public Component getFrontPage() {
        return MM.deserialize("""
                <#FFD700><bold>CAPTURE THE FLAG</bold>
                
                <aqua>Welcome to Capture the Flag!
                
                <gray>In this tutorial, we will explain how to play Capture the Flag
                
                <red>WORK IN PROGRESS
                
                <#FFD700>Signed by yours truly
                """);
    }
}
