package libra.Database;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

public class Infractions {

    public Infractions(String GuildID, String UserID, String UserName, String Moderator, String Reason, String Type, String Duration, long Date, int ID) {

        MongoDatabase DB = Database.getDatabase();
        MongoCollection<Document> Collection = DB.getCollection("Infracciones");
        Document Infraction = new Document()
                .append("GuildID", GuildID)
                .append("UserID", UserID)
                .append("UserName", UserName)
                .append("Moderator", Moderator)
                .append("Reason", Reason)
                .append("Type", Type)
                .append("Duration", Duration)
                .append("Date", Date)
                .append("ID", ID);
        Collection.insertOne(Infraction);
    }

    public static boolean idExists(String GuildID, int ID) {
        MongoDatabase DB = Database.getDatabase();
        MongoCollection<Document> Collection = DB.getCollection("Infracciones");
        Document Infraction = Collection.find(new Document("GuildID", GuildID).append("ID", ID)).first();
        return Infraction != null;
    }

    public static int generateID(String GuildID) {
        int ID = (int) (Math.random() * 1000000);
        while (idExists(GuildID, ID)) {
            ID = (int) (Math.random() * 1000000);
        }
        return ID;
    }
}

