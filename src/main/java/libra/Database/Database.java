package libra.Database;

import com.mongodb.*;
import libra.Config.Config;

import java.net.UnknownHostException;

public class Database {

    private static final Config config = new Config().getConfig();
    private static MongoClient mongoClient = null;
    static {
        try {
            mongoClient = new MongoClient(new MongoClientURI(config.MongoUrl));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public static DBObject getGuildDocument(String guildID) {

        assert mongoClient != null;
        DB Database = mongoClient.getDB("Libra");

        DBCollection Guilds = Database.getCollection("Guilds");
        DBObject query = new BasicDBObject("guildID", guildID);

        return Guilds.findOne(query);

    }

    public static void createGuildDocument(String guildID) {

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