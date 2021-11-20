package libra.Utils;

import org.slf4j.LoggerFactory;

public class Logger {

    public org.slf4j.Logger EventLogger;
    public org.slf4j.Logger MongoDBLogger;

    public Logger getLogger() {

        EventLogger = LoggerFactory.getLogger("Event");
        MongoDBLogger = LoggerFactory.getLogger("MongoDB");
        return this;
    }

}
