package libra.Commands.Config;

import libra.Config.Config;
import libra.Database.Database;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class Habilitar implements Command {
    @SuppressWarnings("unchecked")
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getGuild() == null || context.getMember() == null) return;

        if (!context.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping toEnableRaw = context.getOption("comando");
        if(toEnableRaw == null) {
            context.reply(config.getEmojis().Error + "Debes especificar el comando a habilitar").setEphemeral(true).queue();
            return;
        }
        String toEnable = toEnableRaw.getAsString();

        Document GuildDocument = Database.getGuildDocument(context.getGuild().getId());

        if(GuildDocument == null) {
            context.reply(config.getEmojis().Error+"Parece que tu servidor no está en nuestra base de datos!\nAcciona el comando otra vez, y si el error persiste, contacta con el soporte.").setEphemeral(true).queue();
            Database.createGuildDocument(context.getGuild().getId());
            return;
        }

        ArrayList<String> disabledCommands = (ArrayList<String>) GuildDocument.get("DisabledCommands");
        if(!disabledCommands.contains(toEnable)) {
            context.reply(config.getEmojis().Error+"Ese comando no está deshabilitado!").setEphemeral(true).queue();
            return;
        }

        disabledCommands.remove(toEnable);

        Database.getDatabase().getCollection("Guilds")
                .updateOne(eq("GuildID", context.getGuild().getId()),
                        new Document("$set", new Document("DisabledCommands", disabledCommands)));

        context.reply(config.getEmojis().Success+"Comando `"+toEnable+"` habilitado!").setEphemeral(false).queue();

    }

    @Override
    public String getName() {
        return "habilitar";
    }

    @Override
    public String getDescription() {
        return "Habilita el uso de un comando";
    }

    @Override
    public String getUsage() {
        return "habilitar <Comando>";
    }

    @Override
    public String getPermissions() {
        return "Gestionar el servidor";
    }

    @Override
    public String getCategory() {
        return "Configuración";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.STRING, "comando", "Nombre del comando a habilitar", true));
    }
}
