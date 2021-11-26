package libra.Commands;

import com.mongodb.DBObject;
import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.TimeFormat;

public class Server implements Command {

    private final Config config = new Config().getConfig();

    @Override
    public void run(SlashCommandEvent context, DBObject Guild) {
        if(context.getGuild() == null) return;

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.EmbedColor)
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

                .setThumbnail(context.getGuild().getIconUrl());

        context.replyEmbeds(Embed.build()).queue();
    }

    private String getLevelName(Guild guild){
        Guild.VerificationLevel level = guild.getVerificationLevel();

        switch(level){
            case VERY_HIGH:
                return "Muy alto";

            case HIGH:
                return "Alto";

            case MEDIUM:
                return "Medio";

            case LOW:
                return "Bajo";

            case NONE:
                return "Ninguno";

            default:
            case UNKNOWN:
                return "Desconocido";
        }
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
        return new CommandData(this.getName(), this.getDescription());
    }
}
