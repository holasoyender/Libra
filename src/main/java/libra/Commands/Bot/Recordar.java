package libra.Commands.Bot;

import libra.Config.Config;
import libra.Database.Database;
import libra.Utils.Command.Command;
import libra.Utils.Time;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

public class Recordar implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        OptionMapping timeRaw = context.getOption("tiempo");
        OptionMapping recRaw = context.getOption("recordatorio");

        if(timeRaw == null) {
            context.reply(config.getEmojis().Error+"Tienes que especificar un tiempo").setEphemeral(true).queue();
            return;
        }
        if(recRaw == null) {
            context.reply(config.getEmojis().Error+"Tienes que especificar un recordatorio").setEphemeral(true).queue();
            return;
        }
        long time = Time.ms(timeRaw.getAsString());
        String rec = recRaw.getAsString();

        if(time == 0) {
            context.reply(config.getEmojis().Error+"Ese tiempo es invalido").setEphemeral(true).queue();
            return;
        }

        Document doc = new Document("UserID", context.getUser().getId())
                .append("Recordatorio", rec)
                .append("Tiempo", Time.getTime()+time)
                .append("ChannelID", context.getChannel().getId())
                .append("GuildID", context.getGuild().getId())
                .append("TiempoRaw", timeRaw.getAsString())
                .append("Acabado", false);

        try {
            Database.getDatabase().getCollection("Recordatorios").insertOne(doc);
            context.reply(config.getEmojis().Success+"Recordatorio guardado para dentro de **"+timeRaw.getAsString()+"**:```\n"+rec+"```").setEphemeral(false).queue();
        } catch (Exception e) {
            e.printStackTrace();
            context.reply(config.getEmojis().Error+"No se pudo guardar el recordatorio").setEphemeral(true).queue();
        }

    }

    @Override
    public String getName() {
        return "recordar";
    }

    @Override
    public String getDescription() {
        return "Poner un recordatorio para el futuro";
    }

    @Override
    public String getUsage() {
        return "recordar <Tiempo> <Recordatorio>";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Bot";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.STRING, "tiempo", "Tiempo a esperar para recordar", true))
                .addOptions(new OptionData(OptionType.STRING, "recordatorio", "Que recordar pasado el tiempo", true));
    }
}
