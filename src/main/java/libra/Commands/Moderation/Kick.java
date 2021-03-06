package libra.Commands.Moderation;

import libra.Config.Config;
import libra.Database.Infractions;
import libra.Utils.Command.Command;
import libra.Utils.Time;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

public class Kick implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        if (context.getMember() == null || context.getGuild() == null) return;

        if (!context.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping MemberOption = context.getOption("miembro");
        OptionMapping ReasonOption = context.getOption("razón");
        OptionMapping NOMDOption = context.getOption("md");

        if (MemberOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar un miembro a expulsar").setEphemeral(true).queue();
            return;
        }

        Member Member = MemberOption.getAsMember();
        String Reason = "Sin razón";

        if (ReasonOption != null)
            Reason = ReasonOption.getAsString();

        if (Member == null) {
            context.reply(config.getEmojis().Error + "No se ha encontrado el miembro especificado").setEphemeral(true).queue();
            return;
        }

        if (Member.equals(context.getMember())) {
            context.reply(config.getEmojis().Error + "No puedes expulsarte a ti mismo").setEphemeral(true).queue();
            return;
        }

        if (Member.getId().equals(context.getJDA().getSelfUser().getId())) {
            context.reply(config.getEmojis().Error + "No puedes expulsarme a mi!").setEphemeral(true).queue();
            return;
        }

        if (!context.getMember().canInteract(Member)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para expulsar a este miembro").setEphemeral(true).queue();
            return;
        }

        //noinspection InstantiationOfUtilityClass
        new Infractions(
                context.getGuild().getId(),
                Member.getId(),
                Member.getUser().getName(),
                context.getUser().getName(),
                Reason,
                "Kick",
                "N/A",
                Time.getTime(),
                Infractions.generateID(context.getGuild().getId())
        );

        boolean NoMD = true;
        if (NOMDOption != null)
            NoMD = NOMDOption.getAsBoolean();

        if (NoMD) {

            String finalReason = Reason;
            try {
                Member.getUser().openPrivateChannel().complete().sendMessage("Has sido expulsado del servidor **" + context.getGuild().getName() + "** por " + context.getMember().getAsMention() + " con la razón: `" + Reason + "`")
                        .queue(
                                ok -> context.reply(config.getEmojis().Success + "Has expulsado a " + Member.getAsMention() + " con la razón: `" + finalReason + "`.").setEphemeral(false).queue(),
                                err -> context.reply(config.getEmojis().Success + "Has expulsado a " + Member.getAsMention() + " con la razón: `" + finalReason + "`, pero no he podido notificarle.").setEphemeral(false).queue());

            } catch (Exception e) {
                context.reply(config.getEmojis().Success + "Has expulsado a " + Member.getAsMention() + " con la razón: `" + finalReason + "`, pero no he podido notificarle.").setEphemeral(false).queue();
            }

        } else {
            context.reply(config.getEmojis().Success + "Has expulsado a " + Member.getAsMention() + " con la razón: `" + Reason + "`.").setEphemeral(false).queue();
        }

        try {
            context.getGuild().kick(Member).reason(Reason).queue();
        }catch (Exception e){
            context.reply(config.getEmojis().Error + "No se ha podido expulsar al miembro, comprueba que tengo los permisos necesarios!").setEphemeral(true).queue();
        }

    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Expulsa a un usuario del servidor";
    }

    @Override
    public String getUsage() {
        return "kick <Usuario> <Razón>";
    }

    @Override
    public String getPermissions() {
        return "Expulsar miembros";
    }

    @Override
    public String getCategory() {
        return "Moderación";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.USER, "miembro", "Miembro a expulsar", true),
                        new OptionData(OptionType.STRING, "razón", "Razón del expulsar", false),
                        new OptionData(OptionType.BOOLEAN, "md", "Mandar o no mandar un mensaje directo al usuario sancionado", false));
    }
}
