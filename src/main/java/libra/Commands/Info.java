package libra.Commands;

import com.mongodb.DBObject;
import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.format.DateTimeFormatter;

public class Info implements Command {

    private final Config config = new Config().getConfig();

    @Override
    public void run(SlashCommandEvent context, DBObject Guild) {

        OptionMapping UserOption = context.getOption("usuario");
        User User;
        if (UserOption == null) {
            User = context.getUser();
        } else {
            User = UserOption.getAsUser();
        }

        String URL = User.getAvatarUrl() + "?size=512";
        if (User.getAvatarUrl() == null) URL = "https://cdn.discordapp.com/embed/avatars/0.png";

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.EmbedColor)
                .setAuthor("Usuario " + User.getAsTag(), null, URL)
                .setThumbnail(URL)
                .addField("ID de Usuario", "`" + User.getId() + "`", true)
                .addField("Nombre / Nick", User.getAsTag(), true)
                .addField("Creado el ", "`" + User.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "`", true);

        StringBuilder Roles = new StringBuilder();
        if(context.getGuild() == null) return;
        Member Member = context.getGuild().getMember(User);
        if(Member == null) {
            Embed.addField("Roles", "El usuario no está en el servidor", true);
        }else {
            for(Role role : Member.getRoles()) {
                Roles.append(role.getAsMention()).append("\n");
            }
            if (Roles.length() <= 0)
                Roles = new StringBuilder("Sin roles.");
            Embed.addField("Roles", Roles.toString(),true);
        }
        /*
        * TODO:
        *   - Dice que no está en el servidor
        * */


        context.replyEmbeds(Embed.build()).queue();

    }

    @Override
    public String getDescription() {
        return "Muestra información sobre un usuario";
    }

    @Override
    public String getUsage() {
        return "info [Usuario]";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getCategory() {
        return "Información";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription()).addOption(OptionType.USER, "usuario", "Usuario a sacar información", false);
    }

}