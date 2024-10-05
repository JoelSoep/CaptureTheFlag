package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.Version;
import org.bson.Document;
import org.bukkit.entity.Player;

@CommandAlias("ctf")
public final class CTFCommand extends BaseCommand {

    @Default
    @CatchUnknown
    @Description("Get basic information about the plugin.")
    @Subcommand("version")
    public void version(Player player) {
        String isElevated = player.hasPermission("ctf.admin") || player.isOp() ? "<gray>You are an <yellow>administrative <gray>user." : "<gray>You are a <yellow>default <gray>user.";
        player.sendMessage(MM.deserialize("""
                <gray>Running Capture the Flag <yellow>v.%s <gray>by <yellow>JoelSoep<gray>.
                %s""".formatted(Version.get(), isElevated)));
    }

    @CommandPermission("ctf.admin|op")
    @Description("Switches elevation state for server operators and administrators.")
    @Subcommand("elevate")
    public void elevate(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage("Couldn't fetch profile!");
            return;
        }

        boolean update = !ctfPlayer.isElevated();
        ctfPlayer.setElevated(update);
        player.sendMessage(MM.deserialize("<gray>Updated elevation state to <yellow>%s<gray>.".formatted(update)));
    }

    @CommandPermission("ctf.admin|op")
    @Description("Fetches information about the database connection")
    @Subcommand("dbinfo")
    public void dbInfo(Player player) {
        MongoClient client = Main.getMongoManager().getClient();
        MongoDatabase database = Main.getMongoManager().getDatabase();
        //database.runCommand(new Document("ping", 0));
        String connectionString = LocalConfig.MONGO_CONNECTION_STRING;

    }
}
