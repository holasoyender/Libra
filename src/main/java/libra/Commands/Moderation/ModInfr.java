package libra.Commands.Moderation;

import libra.Config.Config;
import libra.Database.Database;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ModInfr implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getMember() == null || context.getGuild() == null) return;

        if (!context.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping UserOption = context.getOption("usuario");
        OptionMapping IDOption = context.getOption("id");
        OptionMapping ReasonOption = context.getOption("razón");

        if (UserOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar un usuario!").setEphemeral(true).queue();
            return;
        }
        if (IDOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar una ID!").setEphemeral(true).queue();
            return;
        }
        if (ReasonOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar una nueva razón!").setEphemeral(true).queue();
            return;
        }


        User User = UserOption.getAsUser();
        long ID = IDOption.getAsLong();
        String newRes = ReasonOption.getAsString();

        List<Document> allInfractions = Database.getInfractionsByID(User.getId(), context.getGuild().getId()).into(new ArrayList<>());

        if (allInfractions.isEmpty()) {
            context.reply(config.getEmojis().Error + "El usuario no tiene infracciones").setEphemeral(true).queue();
            return;
        }

        Document Infraction = Database.getDatabase().getCollection("Infracciones").find(new Document("GuildID", context.getGuild().getId()).append("UserID", User.getId()).append("ID", ID)).first();

        if (Infraction == null) {
            context.reply(config.getEmojis().Error + "No se ha encontrado la infracción con ID `"+ID+"` del usuario "+User.getAsMention()).setEphemeral(true).queue();
            return;
        }

        Database.getDatabase().getCollection("Infracciones").updateOne(new Document("GuildID", context.getGuild().getId()).append("UserID", User.getId()).append("ID", ID), new Document("$set", new Document("Reason", newRes)));

        context.reply(config.getEmojis().Success + "Se ha modificado la razón de la infracción con ID `"+ID+"` del usuario "+User.getAsMention()+" a `"+newRes+"`").setEphemeral(false).queue();

    }

    @Override
    public String getName() {
        return "modinf";
    }

    @Override
    public String getDescription() {
        return "Modificar la razón de una infracción";
    }

    @Override
    public String getUsage() {
        return "modinfr <Usuario> <ID Infracción> <Razón>";
    }

    @Override
    public String getPermissions() {
        return "Gestionar servidor";
    }

    @Override
    public String getCategory() {
        return "Moderación";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.USER, "usuario", "Usuario para para su infracción", true),
                        new OptionData(OptionType.INTEGER, "id", "ID de la infracción", true),
                        new OptionData(OptionType.STRING, "razón", "Nueva razón de la infracción", true));
    }
}
