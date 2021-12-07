package libra.Commands.Moderation;

import com.mongodb.client.FindIterable;
import libra.Config.Config;
import libra.Database.Database;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Infracciones implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        if (context.getMember() == null || context.getGuild() == null) return;

        if (!context.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping MemberOption = context.getOption("usuario");

        if (MemberOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar un usuario!").setEphemeral(true).queue();
            return;
        }

        User User = MemberOption.getAsUser();

        List<Document> allInfractions = Database.getInfractionsByID(User.getId(), context.getGuild().getId()).into(new ArrayList<>());

        if (allInfractions.isEmpty()) {
            context.reply(config.getEmojis().Error + "El usuario no tiene infracciones").setEphemeral(true).queue();
            return;
        }

        List<List<Document>> allInfractionsSplit = new ArrayList<>();
        int i = 0;
        while (i < allInfractions.size()) {
            allInfractionsSplit.add(allInfractions.subList(i, Math.min(i + 5, allInfractions.size())));
            i += 5;
        }

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .setAuthor("Infracciones de " + User.getName(), null, context.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(User.getAvatarUrl())
                .setDescription("Para borrar una infracción, usa el comando `/delinfracción <Usuario> <ID>`\n**"+allInfractions.size()+"** infracciones totales")
                .setFooter("Página 1 de " + allInfractionsSplit.size(), null);

        for (Document infraction : allInfractionsSplit.get(0)) {
            Embed.addField(" - "+infraction.get("Type")+" #"+infraction.get("ID"), "```"+infraction.get("Reason")+"```\n**Fecha**: "+ TimeFormat.DEFAULT.format(infraction.getLong("Date"))+"\n**Duración**: "+infraction.get("Duration")+"\n**Moderador**: `"+infraction.get("Moderator")+"`", true);
        }

        context.replyEmbeds(Embed.build()).setEphemeral(false).addActionRow(
                Button.primary("cmd:infracciones:" + User.getId() + ":0:back:"+context.getUser().getId(), "◀"),
                Button.primary("cmd:infracciones:" + User.getId() + ":0:next:"+context.getUser().getId(), "▶")
        ).queue();

    }

    @Override
    public String getName() {
        return "infracciones";
    }

    @Override
    public String getDescription() {
        return "Muestra las infracciones de un usuario";
    }

    @Override
    public String getUsage() {
        return "infracciones <Usuario>";
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
                .addOptions(new OptionData(OptionType.USER, "usuario", "Usuario para mirar sus infracciones", true));
    }
}
