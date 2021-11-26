package libra.Commands;

import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bson.Document;

import java.time.Instant;

public class Bot implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild,  Config config) {

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.EmbedColor)
                .setAuthor("Información sobre Libra", null, context.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
                .addField("Propietario", context.getJDA().retrieveUserById(config.OwnerID).complete().getAsTag(), true)
                .addField("Página web", "[Libra](https://libra.kirobot.cc)", true)
                .addField("Servidor de soporte", "[Soporte](https://discord.gg/Rwy8J35)", true)
                .addField("Añádeme a tu servidor", "[Añadir](https://discord.com/api/oauth2/authorize?client_id=" + context.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands)", true)
                .addField("Servidores actuales", context.getJDA().getGuilds().size()+" Servidores", true)
                .setFooter("Pedido por "+context.getUser().getAsTag(), context.getUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        context.replyEmbeds(Embed.build()).addActionRow(
                Button.primary("cmd:bot:Main:"+context.getUser().getId(), "\uD83D\uDCC3  Lista de comandos"),
                Button.link("https://libra.kirobot.cc", "\uD83D\uDCBB  Página web"),
                Button.link("https://docs.libra.kirobot.cc", "\uD83D\uDCC3  Documentación")
        ).queue();
    }

    @Override
    public String getName() {
        return "bot";
    }

    @Override
    public String getDescription() {
        return "Información sobre el bot";
    }

    @Override
    public String getUsage() {
        return "bot";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Bot";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription());
    }
}
