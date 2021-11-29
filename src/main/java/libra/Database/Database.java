package libra.Database;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import libra.Config.Config;
import org.bson.Document;

public class Database {

    private static final Config config = new Config().getConfig();


    private static final MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(config.MongoUrl)).build();
    private static final MongoClient mongoClient = MongoClients.create(settings);


    public static Document getGuildDocument(String guildID) {

        MongoDatabase Database = mongoClient.getDatabase("Libra");
        MongoCollection<Document> Guilds = Database.getCollection("Guilds");


        return Guilds.find(new Document("guildID", guildID)).first();

    }

    public static void createGuildDocument(String guildID) {

        MongoDatabase Database = mongoClient.getDatabase("Libra");
        MongoCollection<Document> Guilds = Database.getCollection("Guilds");

        Document Guild = Guilds.find(new Document("guildID", guildID)).first();

        if (Guild != null) return;

        Document newDocument = new Document("guildID", guildID).append("prefix", config.Prefix);
        Guilds.insertOne(newDocument);

    }

    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase("Libra");
    }

}