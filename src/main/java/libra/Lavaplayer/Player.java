package libra.Lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import libra.Config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {

    public static final Config config = new Config();
    public static final int DEFAULT_VOLUME = 75;
    private static AudioPlayerManager playerManager;
    private static Map<String, GuildMusicManager> musicManagers;

    public Player() {

        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        musicManagers = new HashMap<>();

    }

    public static void loadAndPlay(GuildMusicManager mng, final SlashCommandEvent event, String url, boolean isSearch)
    {

        playerManager.loadItemOrdered(mng, url, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                EmbedBuilder Embed = new EmbedBuilder()
                        .setColor(config.getEmbedColor())
                        .setAuthor("Canción añadida la cola ", null, event.getJDA().getSelfUser().getAvatarUrl())
                        .setDescription("**[" + formatTitle(track.getInfo().title) + "](https://www.youtube.com/watch?v=" + track.getInfo().identifier+")**");

                if (mng.player.getPlayingTrack() == null) {
                    Embed.setAuthor("Reproduciendo ahora", null, event.getJDA().getSelfUser().getAvatarUrl())
                            .setThumbnail("https://i.ytimg.com/vi/" + track.getInfo().identifier + "/mqdefault.jpg")
                            .setDescription(null)
                            .setTitle(formatTitle(track.getInfo().title), "https://www.youtube.com/watch?v=" + track.getInfo().identifier)
                            .addField("Autor", track.getInfo().author, true)
                            .addField("Duración", getTimestamp(track.getDuration()), true);
                }


                mng.scheduler.queue(track);
                event.replyEmbeds(Embed.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();


                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (isSearch)
                {
                    EmbedBuilder Embed = new EmbedBuilder()
                            .setColor(config.getEmbedColor())
                            .setAuthor("Canción añadida la cola ", null, event.getJDA().getSelfUser().getAvatarUrl())
                            .setDescription("**[" + formatTitle(firstTrack.getInfo().title) + "](https://www.youtube.com/watch?v=" + firstTrack.getInfo().identifier+")**");

                    if (mng.player.getPlayingTrack() == null) {
                        Embed.setAuthor("Reproduciendo ahora", null, event.getJDA().getSelfUser().getAvatarUrl())
                                .setThumbnail("https://i.ytimg.com/vi/" + firstTrack.getInfo().identifier + "/mqdefault.jpg")
                                .setDescription(null)
                                .setTitle(formatTitle(firstTrack.getInfo().title), "https://www.youtube.com/watch?v=" + firstTrack.getInfo().identifier)
                                .addField("Autor", firstTrack.getInfo().author, true)
                                .addField("Duración", getTimestamp(firstTrack.getDuration()), true);
                    }


                    event.replyEmbeds(Embed.build()).queue();
                    mng.scheduler.queue(firstTrack);
                }
                else
                {
                    EmbedBuilder Embed = new EmbedBuilder()
                            .setColor(config.getEmbedColor())
                            .setDescription("Se han añadido **" + playlist.getTracks().size() + "** canciones a la cola");
                    event.replyEmbeds(Embed.build()).queue();
                    tracks.forEach(mng.scheduler::queue);
                }
            }

            @Override
            public void noMatches()
            {
                event.reply(config.getEmojis().Error+"No he podido encontrar ninguna canción con ese nombre.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                event.reply(config.getEmojis().Error+"No he podido encontrar ninguna canción con ese nombre.").queue();
                exception.printStackTrace();
            }
        });
    }

    public static GuildMusicManager getMusicManager(Guild guild)
    {
        String guildId = guild.getId();
        GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null) {

            mng = new GuildMusicManager(playerManager);
            mng.player.setVolume(DEFAULT_VOLUME);
            musicManagers.put(guildId, mng);
        }
        return mng;
    }

    public static String getTimestamp(long milliseconds)
    {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatTitle(String title) {
        return title
                .replaceAll("\\(.*?\\)", "")
                .replaceAll("\\[.*?]", "");
    }

}
