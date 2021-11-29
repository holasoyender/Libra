package libra.Commands.Ocio;

import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@SuppressWarnings("StringConcatenationInLoop")
public class Perro implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        try {
            URL Api = new URL("https://dog.ceo/api/breeds/image/random");
            HttpURLConnection http = (HttpURLConnection) Api.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Accept", "application/json");

            if (http.getResponseCode() != 200) {
                context.reply("Ha ocurrido un error con la API!").setEphemeral(true).queue();
                return;
            }

            String inline = "";
            Scanner scanner = new Scanner(Api.openStream());

            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            scanner.close();
            JSONParser parse = new JSONParser();
            JSONObject data = (JSONObject) parse.parse(inline);

            EmbedBuilder Embed = new EmbedBuilder()
                    .setColor(config.getEmbedColor())
                    .setTitle("Aquí tienes una foto de un perro!")
                    .setImage(data.get("message").toString());

            context.replyEmbeds(Embed.build()).queue();

            http.disconnect();

        } catch (IOException | ParseException e) {
            context.reply("Ha ocurrido un error con la API!").setEphemeral(true).queue();
            e.printStackTrace();
        }

    }

    @Override
    public String getName() {
        return "perro";
    }

    @Override
    public String getDescription() {
        return "Manda una foto aleatorio de un perro";
    }

    @Override
    public String getUsage() {
        return "perro";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Ocio";
    }

    @Override
    public CommandData getSlashData() {
        return null;
    }
}