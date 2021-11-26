package libra.Config;

import java.awt.*;

public class Config {

    public String Token;
    public String OwnerID;
    public String Prefix;
    public String SentryDNS;
    public String MongoUrl;
    public Color EmbedColor;
    public Emojis Emojis;
    public String LogWebhookURL;

    public Config getConfig() {
        Token = "T0K3N";
        OwnerID = "396683727868264449";
        Prefix = "-";
        SentryDNS = "Sentry DNS";
        MongoUrl = "mongodb://localhost:27017/Libra";
        EmbedColor = Color.decode("#5b6cec");
        Emojis = new Emojis().getEmojis();
        LogWebhookURL= "";

        return this;
    }
}