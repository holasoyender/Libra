package libra.Lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {


    public static final int DEFAULT_VOLUME = 50;
    private static AudioPlayerManager playerManager;
    private static Map<String, GuildMusicManager> musicManagers;

    public Player() {

        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        //playerManager.registerSourceManager(new SoundCloudAudioSourceManager(true, "", "", "", ""));
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        musicManagers = new HashMap<>();

    }

    public static void loadAndPlay(GuildMusicManager mng, final MessageChannel channel, String url, final boolean addPlaylist)
    {

        playerManager.loadItemOrdered(mng, url, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                String msg = "Adding to queue: " + track.getInfo().title;
                if (mng.player.getPlayingTrack() == null)
                    msg += "\nand the Player has started playing;";

                mng.scheduler.queue(track);
                channel.sendMessage(msg).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();


                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (addPlaylist)
                {
                    channel.sendMessage("Adding **" + playlist.getTracks().size() +"** tracks to queue from playlist: " + playlist.getName()).queue();
                    tracks.forEach(mng.scheduler::queue);
                }
                else
                {
                    channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();
                    mng.scheduler.queue(firstTrack);
                }
            }

            @Override
            public void noMatches()
            {
                channel.sendMessage("Nothing found by " + url).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
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

    private static String getTimestamp(long milliseconds)
    {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

}
