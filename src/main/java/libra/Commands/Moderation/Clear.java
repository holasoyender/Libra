package libra.Commands.Moderation;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

public class Clear implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if(context.getMember() == null) return;

        if (!context.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping CommandOption = context.getOption("número");
        if (CommandOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar un número de mensajes a borrar").setEphemeral(true).queue();
            return;
        }

        long num = CommandOption.getAsLong();
        if (num < 1 || num > 100) {
            context.reply(config.getEmojis().Error + "Debes especificar un número entre 1 y 100").setEphemeral(true).queue();
            return;
        }

        int toDelete = Math.toIntExact(num);
        DateTime limit = new DateTime().minusWeeks(2).minusHours(1);

        List<Message> rawMessages = context.getChannel().getHistory().retrievePast(toDelete).complete();
        List<Message> Messages = new ArrayList<>();
        int i = 0;

        for (Message message : rawMessages) {
            if (!new DateTime(message.getTimeCreated().toEpochSecond() * 1000).withZone(DateTimeZone.UTC).isBefore(limit)) {
                Messages.add(message);
                i++;
            }
        }

        try {
            context.getChannel().purgeMessages(Messages);
            context.reply(config.getEmojis().Success + "Se han borrado **" + i + "** mensajes").setEphemeral(false).queue();
        } catch (Exception e) {
            context.reply(config.getEmojis().Error + "No se pudieron borrar los mensajes").setEphemeral(true).queue();
        }

    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Borra un número específico de mensajes";
    }

    @Override
    public String getUsage() {
        return "clear <Número>";
    }

    @Override
    public String getPermissions() {
        return "Gestionar mensajes";
    }

    @Override
    public String getCategory() {
        return "Moderación";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.INTEGER, "número", "Número de mensajes a borrar", true));
    }
}