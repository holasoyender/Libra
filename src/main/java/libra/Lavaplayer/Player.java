package libra.Lavaplayer;

import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifyConfig;
import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifySourceManager;
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
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Player {

    public static final Config config = new Config();
    public static final int DEFAULT_VOLUME = 75;
    private static AudioPlayerManager playerManager;
    private static Map<String, GuildMusicManager> musicManagers;

    public Player() {

        playerManager = new DefaultAudioPlayerManager();

        SpotifyConfig spotifyConfig = new SpotifyConfig();
        spotifyConfig.setClientId(config.getSpotifyClientID());
        spotifyConfig.setClientSecret(config.getSpotifyClientSecret());
        spotifyConfig.setCountryCode("ES");

        playerManager.registerSourceManager(new SpotifySourceManager(null, spotifyConfig, playerManager));
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
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder Embed = Embeds.getAddEmbed(track);

                mng.scheduler.queue(track);
                track.setUserData(event.getMember());

                if (mng.player.getPlayingTrack() != null && mng.player.getPlayingTrack() != track) {
                    event.replyEmbeds(Embed.build()).setEphemeral(true).queue();
                }
                else {
                    event.replyEmbeds(Embed.build()).setEphemeral(true).queue();
                    EmbedBuilder newEmbed = Embeds.getTrackEmbed(track, event.getJDA());

                    event.getChannel().sendMessageEmbeds(newEmbed.build()).setActionRow(
                            Button.success("cmd:pause:" + track.getIdentifier(), "Pausar / Continuar"),
                            Button.primary("cmd:skip:" + track.getIdentifier(), "Saltar"),
                            Button.danger("cmd:stop:" + track.getIdentifier(), "Parar")
                    ).queue();
                }
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
                    EmbedBuilder Embed = Embeds.getAddEmbed(firstTrack);
                    boolean ephemeral = true;

                    if (mng.player.getPlayingTrack() == null) {
                        ephemeral = false;
                        Embed = Embeds.getTrackEmbed(firstTrack, event.getJDA());
                    }

                    firstTrack.setUserData(event.getMember());

                    if(ephemeral)
                        event.replyEmbeds(Embed.build()).setEphemeral(ephemeral).queue();
                    else
                        event.replyEmbeds(Embed.build()).addActionRow(
                                Button.success("cmd:pause:"+ firstTrack.getIdentifier(), "Pausar / Continuar") ,
                                Button.primary("cmd:skip:"+firstTrack.getIdentifier(), "Saltar"),
                                Button.danger("cmd:stop:"+firstTrack.getIdentifier(), "Parar")
                        ).queue();
                    mng.scheduler.queue(firstTrack);
                }
                else
                {
                    EmbedBuilder Embed = new EmbedBuilder()
                            .setColor(config.getEmbedColor())
                            .setDescription("Se han añadido **" + playlist.getTracks().size() + "** canciones a la cola");
                    event.replyEmbeds(Embed.build()).setEphemeral(true).queue();

                    for (AudioTrack track : tracks) {
                        track.setUserData(event.getMember());
                    }

                    tracks.forEach(mng.scheduler::queue);
                }
            }

            @Override
            public void noMatches()
            {
                event.reply(config.getEmojis().Error+"No he podido encontrar ninguna canción con ese nombre.").setEphemeral(true).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                event.reply(config.getEmojis().Error+"No he podido encontrar ninguna canción con ese nombre.").setEphemeral(true).queue();
                exception.printStackTrace();
            }
        });
    }

    public static @NotNull GuildMusicManager getMusicManager(Guild guild, MessageChannel channel)
    {
        String guildId = guild.getId();
        GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null) {

            mng = new GuildMusicManager(playerManager, channel);
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
