package libra.Database;

import com.mongodb.*;
import libra.Utils.Config;

import java.net.UnknownHostException;

public class Database {

    private static final Config config = new Config().getConfig();

    public static DBObject getGuildDocument(String guildID) {

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(new MongoClientURI(config.MongoUrl));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        assert mongoClient != null;
        DB Database = mongoClient.getDB("Libra");

        DBCollection Guilds = Database.getCollection("Guilds");
        DBObject query = new BasicDBObject("guildID", guildID);

        return Guilds.findOne(query);

    }

    public static String getGuildPrefix(String guildID) {

        MongoClient mongoClient;
        try {
            mongoClient = new MongoClient(new MongoClientURI(config.MongoUrl));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return config.Prefix;
        }

        DB Database = mongoClient.getDB("Libra");
        DBCollection Guilds = Database.getCollection("Guilds");
        DBObject query = new BasicDBObject("guildID", guildID);
        DBObject Guild = Guilds.findOne(query);

        if (Guild == null) return config.Prefix;
        return Guild.get("prefix").toString();
    }

    public static void createGuildDocument(String guildID) {

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(new MongoClientURI(config.MongoUrl));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        assert mongoClient != null;
        DB Database = mongoClient.getDB("Libra");
        DBCollection Guilds = Database.getCollection("Guilds");
        DBObject query = new BasicDBObject("guildID", guildID);
        DBObject Guild = Guilds.findOne(query);

        if (Guild != null) return;

        DBObject newDocument = new BasicDBObject("guildID", guildID).append("prefix", config.Prefix);
        Guilds.insert(newDocument);

    }
}
