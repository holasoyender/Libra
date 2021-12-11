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

        String defaultConfig = """

                #  ██╗       ██╗  ██████╗   ██████╗    █████╗\s
                #  ██║       ██║  ██╔══██╗  ██╔══██╗  ██╔══██╗
                #  ██║       ██║  ██████╔╝  ██████╔╝  ███████║
                #  ██║       ██║  ██╔══██╗  ██╔══██╗  ██╔══██║
                #  ███████╗  ██║  ██████╔╝  ██║  ██║  ██║  ██║
                #  ╚══════╝  ╚═╝  ╚═════╝   ╚═╝  ╚═╝  ╚═╝  ╚═╝
                # Este es el archivo de configuración para el bot de Discord Libra. Mas info: https://libra.kirobot.cc

                BotToken: "T0K3N"    # El token de tu bot de Discord (Obligatorio)
                SentryDNS: ""    # La DNS de Sentry.io para tracking de errores (Opcional)
                MongoURL: "mongodb://localhost:27017/Libra"    # La URL de tu base de datos en MongoDB (Obligatorio)

                OwnerID: "396683727868264449"    # La ID de usuario del propietario del bot (Obligatorio)
                DefaultPrefix: "-"    # El prefijo que usará el bot en los comandos de owner (Opcional)

                EmbedColor: "#5b6cec"    # El color de los embeds enviados por el bot (Opcional)
                LogWebhookURL: ""    # La URL del Webhook de logs internos del bot (Opcional)
                """;

        if (!(file.exists())) {
            try {
                Files.writeString(file.toPath(), defaultConfig);
                System.out.println("No se ha encontrado el archivo de config, por favor, rellena config.yml y ejecuta este programa de nuevo.");
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
        if(config.get("DefaultPrefix") == null || config.get("DefaultPrefix") == "") return "-";
        return config.get("DefaultPrefix").toString();
    }

    public Emojis getEmojis() {
        return new Emojis().getEmojis();
    }

    public Color getEmbedColor() {
        if(config.get("EmbedColor") == null || config.get("EmbedColor") == "") return Color.decode("#5b6cec");
        return Color.decode(config.get("EmbedColor").toString());
    }

    public String getMongoUrl() { return config.get("MongoURL").toString(); }

    public String getStatus() {
        if (config.get("Estado") == null || config.get("Estado") == "") return "el mundo";
        return config.get("Estado").toString();
    }
}
