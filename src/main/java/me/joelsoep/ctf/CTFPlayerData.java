package me.joelsoep.ctf;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.CTFPlayer;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;

@Data
public class CTFPlayerData {

    private final CTFPlayer player;
    private final String name;

    private Integer kills;
    private Integer deaths;
    private Double kd;

    public CTFPlayerData(CTFPlayer player) {
        this.player = player;
        this.name = player.getPlayer().getName();

        if (Main.mongo.findOneDocumentAsync(Main.mongo.getUserCollection(), new Document("_id", player.getPlayer().getUniqueId().toString())).join() == null) {
            Main.mongo.insertDocumentAsync(Main.mongo.getUserCollection(), new Document(
               "_id", player.getPlayer().getUniqueId().toString())
                    .append("player_name", name)
                    .append("player_kills", 0)
                    .append("player_deaths", 0)
                    .append("player_kd", (double) 1)
            );
            this.kills = 0;
            return;
        }

        CompletableFuture<Document> future = Main.mongo.findOneDocumentAsync(Main.mongo.getUserCollection(), new Document("_id", player.getPlayer().getUniqueId().toString()));

        future.thenAccept(result -> {
          if (result != null) {
              if (this.kills == null) {
                  this.kills = 0;
              }

              if (this.deaths == null) {
                  this.deaths = 0;
              }

              if (this.kd == null) {
                  this.kd = (double) 1;
              }

              this.kills = result.getInteger("player_kills");
              this.deaths = result.getInteger("player_deaths");
              this.kd = result.getDouble("player_kd");
          } else {
              System.out.println("Document not found for player " + player.getPlayer().getName());
          }
        }).exceptionally(exception -> {
            System.err.println("Error occurred: " + exception.getMessage());
            return null;
        });
    }
}
