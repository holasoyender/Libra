package libra.Database;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import libra.Config.Config;
import org.bson.Document;

import java.util.ArrayList;

public class Database {

    private static final Config config = new Config();

    private static final MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(config.getMongoUrl())).build();
    private static final MongoClient mongoClient = MongoClients.create(settings);


    public static Document getGuildDocument(String guildID) {

        MongoDatabase Database = mongoClient.getDatabase("Libra");
        MongoCollection<Document> Guilds = Database.getCollection("Guilds");


        return Guilds.find(new Document("GuildID", guildID)).first();
    }

    public static void createGuildDocument(String guildID) {

        MongoDatabase Database = mongoClient.getDatabase("Libra");
        MongoCollection<Document> Guilds = Database.getCollection("Guilds");

        Document Guild = Guilds.find(new Document("guildID", guildID)).first();

        if (Guild != null) return;

        Document newDocument = new Document("GuildID", guildID).append("Logs", new Document("Enabled", false).append("ChannelID", "")).append("DisabledCommands", new ArrayList<String>());
        Guilds.insertOne(newDocument);

    }

    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase("Libra");
    }

    public static String getLogChannelIDByGuildID(String GuildID) {
        Document _Guild = Database.getGuildDocument(GuildID);
        if(_Guild == null) return null;
        Object _Logs = _Guild.get("Logs");

        Document Logs = (Document) _Logs;
        return Logs.get("ChannelID").toString();

    }
}