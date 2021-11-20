package libra.Database;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import libra.Utils.Config;
import libra.Utils.Logger;

import java.net.UnknownHostException;

public class MongoDB {

    private static final libra.Utils.Logger Logger = new Logger().getLogger();

    public static void ConnectToMongoDB() {

        Config config = new Config().getConfig();

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(new MongoClientURI(config.MongoUrl));
            Logger.MongoDBLogger.info("Conectado con MongoDB");
        } catch (UnknownHostException e) {
            Logger.MongoDBLogger.error("No se ha podido conectar con MongoDB");
            e.printStackTrace();
        }

        if (mongoClient == null) return;
        DB database = mongoClient.getDB("Libra");
        DBCollection collection = database.getCollection("Testing");

    }
}
