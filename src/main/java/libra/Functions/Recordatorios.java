package libra.Functions;

import com.mongodb.client.MongoCollection;
import libra.Database.Database;
import libra.Utils.Time;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class Recordatorios {

    public Recordatorios(JDA Jda) {
        start(Jda);
    }

    public static void start(JDA Jda) {

        new Thread(() -> {
            while (true) {
                MongoCollection<Document> Recordatorios = Database.getDatabase().getCollection("Recordatorios");
                Document Recordatorio = Recordatorios.find(eq("Acabado", false)).first();
                if (Recordatorio != null) {

                    if (Long.parseLong(Recordatorio.get("Tiempo").toString()) < Time.getTime()) {
                        Guild Guild = Jda.getGuildById(Recordatorio.get("GuildID").toString());
                        if (Guild != null) {

                            TextChannel Channel = Guild.getTextChannelById(Recordatorio.get("ChannelID").toString());
                            if (Channel != null) {
                                Channel.sendMessage("**Hola <@!" + Recordatorio.get("UserID").toString() + "> :wave: !**\nMe pediste hace **" + Recordatorio.get("TiempoRaw").toString() + "** que te recordase: ```\n" + Recordatorio.get("Recordatorio").toString() + "```").queue();
                                Recordatorios.updateOne(eq("_id", Recordatorio.get("_id")), new Document("$set", new Document("Acabado", true)));
                            }
                        }
                    }
                }

                try {
                    //noinspection BusyWait
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }).start();

    }
}
