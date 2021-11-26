package libra.Utils;

import org.slf4j.LoggerFactory;

public class Logger {

    public org.slf4j.Logger EventLogger;
    public org.slf4j.Logger MongoDBLogger;
    public org.slf4j.Logger LoadLogger;

    public Logger getLogger() {

        EventLogger = LoggerFactory.getLogger("Event");
        MongoDBLogger = LoggerFactory.getLogger("MongoDB");
        LoadLogger = LoggerFactory.getLogger("Load");
        return this;
    }

}
