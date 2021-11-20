package libra.Utils;

public class ConfigExample {

    //Renombrar archivo y clase a Config

    public String Token;
    public String OwnerID;
    public String Prefix;
    public String SentryDNS;
    public String MongoUrl;

    public libra.Utils.ConfigExample getConfig() {
        Token = "T0K3N";
        OwnerID = "396683727868264449";
        Prefix = ";";
        SentryDNS = "Sentry DNS";
        MongoUrl = "mongodb://localhost:27017/Libra";

        return this;
    }
}


