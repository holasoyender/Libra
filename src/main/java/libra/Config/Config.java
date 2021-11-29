package libra.Config;

import org.yaml.snakeyaml.Yaml;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class Config {

    private final Map<String, Object> config;

    public Config() {
        Yaml yaml = new Yaml();
        File file = new File("config.yml");

        if (!(file.exists())) {
            try {
                //Cuando se compila a un JAR, el archivo de ejemplo no existe (hacerlo remoto/string)
                Files.copy(new File("src/main/resources/config.example.yml").toPath(), file.toPath());
                System.out.println("No se ha encontrado el archivo de configuración, por favor, rellena config.yml y ejecuta este programa de nuevo.");
                System.exit(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (InputStream is = new FileInputStream(file)) {
            this.config = yaml.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String getToken(){
        return config.get("BotToken").toString();
    }

    public String getSentry() {
        return config.get("SentryDNS").toString();
    }

    public String getLogWebhookURL() {
        return config.get("LogWebhookURL").toString();
    }

    public String getOwnerID() {
        return config.get("OwnerID").toString();
    }

    public String getDefaultPrefix() {
        return config.get("DefaultPrefix").toString();
    }

    public Emojis getEmojis() {
        return new Emojis().getEmojis();
    }

    public Color getEmbedColor() {
        return Color.decode(config.get("EmbedColor").toString());
    }

    public String getMongoUrl() { return config.get("MongoURL").toString(); }
}
