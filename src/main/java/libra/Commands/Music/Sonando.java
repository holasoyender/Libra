package libra.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import libra.Config.Config;
import libra.Lavaplayer.GuildMusicManager;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import static libra.Lavaplayer.Player.*;

public class Sonando implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if(context.getGuild() == null) return;

        net.dv8tion.jda.api.entities.Guild guild = context.getGuild();
        GuildMusicManager mng = getMusicManager(guild, null);
        AudioPlayer player = mng.player;

        if (player.getPlayingTrack() == null)
        {
            context.reply(config.getEmojis().Error + " No hay nada sonando!").setEphemeral(true).queue();
            return;
        }

        String title = player.getPlayingTrack().getInfo().title;
        String position = getTimestamp(player.getPlayingTrack().getPosition());
        String duration = getTimestamp(player.getPlayingTrack().getDuration());
        String author = player.getPlayingTrack().getInfo().author;
        String url = player.getPlayingTrack().getInfo().uri;
        String identifier = player.getPlayingTrack().getInfo().identifier;

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .setAuthor("Sonando ahora: " + formatTitle(title), url, context.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail("https://i.ytimg.com/vi/" + identifier + "/mqdefault.jpg")
                .addField("Autor", author, true)
                .addField("Duración", duration, true)
                .addField("Posición", position, true);

        context.replyEmbeds(Embed.build()).queue();

    }

    @Override
    public String getName() {
        return "sonando";
    }

    @Override
    public String getDescription() {
        return "Información de la canción que esta sonando";
    }

    @Override
    public String getUsage() {
        return "sonando";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Música";
    }

    @Override
    public CommandData getSlashData() {
        return null;
    }
}
