package libra.Commands.Moderation;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import java.util.List;

public class Unmute implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        if (context.getMember() == null || context.getGuild() == null) return;

        if (!context.getMember().hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping MemberOption = context.getOption("miembro");

        if (MemberOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar un miembro a silenciar").setEphemeral(true).queue();
            return;
        }

        Member Member = MemberOption.getAsMember();

        if (Member == null) {
            context.reply(config.getEmojis().Error + "No se ha encontrado el miembro especificado").setEphemeral(true).queue();
            return;
        }

        if (!context.getMember().canInteract(Member)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para des-silenciar a este miembro").setEphemeral(true).queue();
            return;
        }

        List<Role> Roles = context.getGuild().getRolesByName("Muteado", true);
        if (Roles.size() == 0) {
            context.reply(config.getEmojis().Error + "No se ha encontrado el rol **Muteado**, por favor crealo y acciona el comando de nuevo").setEphemeral(true).queue();
            return;
        }
        Role MuteRole = Roles.get(0);
        if (MuteRole == null) {
            context.reply(config.getEmojis().Error + "No se ha encontrado el rol **Muteado**, por favor crealo y acciona el comando de nuevo").setEphemeral(true).queue();
            return;
        }

        if (!context.getGuild().getSelfMember().canInteract(MuteRole)) {
            context.reply(config.getEmojis().Error + "No tengo permisos para quitar el rol **Muteado**").setEphemeral(true).queue();
            return;
        }

        if (!Member.getRoles().contains(MuteRole)) {
            context.reply(config.getEmojis().Error + "Ese miembro no está silenciado").setEphemeral(true).queue();
            return;
        }

        try {
            context.getGuild().removeRoleFromMember(Member, MuteRole).queue();
        } catch (Exception e) {
            context.reply(config.getEmojis().Error + "No se ha podido quitar el rol **Muteado**!\nComprueba que mis permisos son correctos y que mi rol está por encima del rol **Muteado**").setEphemeral(true).queue();
            return;
        }

        context.reply(config.getEmojis().Success + "Usuario des-silenciado con éxito!").setEphemeral(false).queue();


    }

    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    public String getDescription() {
        return "Des-silenciar a un usuario";
    }

    @Override
    public String getUsage() {
        return "unmute <Usuario>";
    }

    @Override
    public String getPermissions() {
        return "Silenciar miembros";
    }

    @Override
    public String getCategory() {
        return "Moderación";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.USER, "miembro", "Miembro a des-silenciar", true));
    }
}