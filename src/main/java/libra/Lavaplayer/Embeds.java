package libra.Lavaplayer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import libra.Config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import static libra.Lavaplayer.Player.formatTitle;

public class Embeds {

    public static final Config config = new Config();

    public Embeds(AudioTrack track, JDA jda) {
        getAddEmbed(track);
        getTrackEmbed(track, jda);
    }

    public static EmbedBuilder getAddEmbed(AudioTrack track) {
        return new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .setDescription("Canción **" + formatTitle(track.getInfo().title) + "** añadida la cola!");
    }

    public static EmbedBuilder getTrackEmbed(AudioTrack track, JDA jda) {
        return new EmbedBuilder()
                .setAuthor("|   Ahora sonando", null, jda.getSelfUser().getAvatarUrl())
                .setDescription("**[" + formatTitle(track.getInfo().title) + "](" + track.getInfo().uri + ")** by `"+track.getInfo().author+"`")
                .setColor(config.getEmbedColor());
    }
}