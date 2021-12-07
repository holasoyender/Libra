package libra.Commands.Moderation;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

public class Unban implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getMember() == null || context.getGuild() == null) return;

        if (!context.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping UserOption = context.getOption("usuario");

        if (UserOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar un usuario a desbanear").setEphemeral(true).queue();
            return;
        }

        User User = UserOption.getAsUser();

        try {
            context.getGuild().unban(User).reason(null).queue(
                    success -> context.reply(config.getEmojis().Success + "Usuario desbaneado con éxito").setEphemeral(false).queue(),
                    err -> context.reply(config.getEmojis().Error + "Ese usuario no está baneado!").setEphemeral(true).queue());
        } catch (Exception e) {
            context.reply(config.getEmojis().Error + "Ese usuario no está baneado!").setEphemeral(true).queue();
        }
    }

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return "Desbanear a un usuario";
    }

    @Override
    public String getUsage() {
        return "unban <Usuario>";
    }

    @Override
    public String getPermissions() {
        return "Banear miembros";
    }

    @Override
    public String getCategory() {
        return "Moderación";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.USER, "usuario", "Usuario a desbanear", true));
    }
}
