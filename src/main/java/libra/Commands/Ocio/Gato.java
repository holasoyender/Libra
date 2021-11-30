package libra.Commands.Ocio;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@SuppressWarnings("StringConcatenationInLoop")
public class Gato implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        try {
            URL Api = new URL("https://api.thecatapi.com/v1/images/search");
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
            JSONArray rawData = (JSONArray) parse.parse(inline);
            JSONObject data = (JSONObject) rawData.get(0);

            EmbedBuilder Embed = new EmbedBuilder()
                    .setColor(config.getEmbedColor())
                    .setTitle("Aquí tienes una foto de un gato!")
                    .setImage(data.get("url").toString());

            context.replyEmbeds(Embed.build()).queue();

            http.disconnect();

        } catch (IOException | ParseException e) {
            context.reply("Ha ocurrido un error con la API!").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "gato";
    }

    @Override
    public String getDescription() {
        return "Manda una foto aleatorio de un gato";
    }

    @Override
    public String getUsage() {
        return "gato";
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
