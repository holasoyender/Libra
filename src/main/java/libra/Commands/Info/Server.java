package libra.Commands.Info;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.bson.Document;

import java.time.Instant;

public class Server implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if(context.getGuild() == null) return;

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .setAuthor(context.getGuild().getName(), null, context.getGuild().getIconUrl())
                .addField("ID del servidor", String.format("```yaml\nID: %s```",context.getGuild().getId()), false)
                .setDescription(
                        String.format(
                                "**Propietario**:  <@!%s> (%s)\n"+
                                "**Creado el**:   %s (%s)",
                                context.getGuild().getOwnerId(), context.getGuild().getOwnerId(),
                                TimeFormat.DEFAULT.format(context.getGuild().getTimeCreated()), TimeFormat.RELATIVE.format(context.getGuild().getTimeCreated())
                                ))
                .addField("Nivel de seguridad", getLevelName(context.getGuild()), true)
                .addField("Miembros totales", context.getGuild().getMemberCount()+" miembros", true)
                .addField("Número de canales", context.getGuild().getChannels().size()+" canales", true)
                .addField("Número de Roles", context.getGuild().getRoles().size()+ " roles", true)

                .setFooter("Pedido por "+context.getUser().getAsTag(), context.getUser().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setThumbnail(context.getGuild().getIconUrl());

        context.replyEmbeds(Embed.build()).queue();
    }

    private String getLevelName(Guild guild){
        Guild.VerificationLevel level = guild.getVerificationLevel();

        return switch (level) {
            case VERY_HIGH -> "Muy alto";
            case HIGH -> "Alto";
            case MEDIUM -> "Medio";
            case LOW -> "Bajo";
            case NONE -> "Ninguno";
            case UNKNOWN -> "Desconocido";
        };
    }

    @Override
    public String getName() {
        return "server";
    }

    @Override
    public String getDescription() {
        return "Muestra la información del servidor";
    }

    @Override
    public String getUsage() {
        return "server";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Información";
    }

    @Override
    public CommandData getSlashData() {
        return null;
    }
}
