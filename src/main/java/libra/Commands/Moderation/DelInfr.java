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

public class DelInfr implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getMember() == null || context.getGuild() == null) return;

        if (!context.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping UserOption = context.getOption("usuario");
        OptionMapping IDOption = context.getOption("id");

        if (UserOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar un usuario!").setEphemeral(true).queue();
            return;
        }
        if (IDOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar una ID!").setEphemeral(true).queue();
            return;
        }


        User User = UserOption.getAsUser();
        long ID = IDOption.getAsLong();

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

        Database.getDatabase().getCollection("Infracciones").deleteOne(new Document("GuildID", context.getGuild().getId()).append("UserID", User.getId()).append("ID", ID));

        context.reply(config.getEmojis().Success + "Se ha borrado la infracción con ID `"+ID+"` del usuario "+User.getAsMention()).setEphemeral(false).queue();
    }

    @Override
    public String getName() {
        return "delinfr";
    }

    @Override
    public String getDescription() {
        return "Borra una infracción de un usuario";
    }

    @Override
    public String getUsage() {
        return "delinfr <Usuario> <ID Infraccion>";
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
                        new OptionData(OptionType.INTEGER, "id", "ID de la infracción", true));
    }
}
