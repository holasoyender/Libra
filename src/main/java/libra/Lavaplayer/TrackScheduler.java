package libra.Lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import libra.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.*;

import static libra.Lavaplayer.Player.getMusicManager;

public class TrackScheduler extends AudioEventAdapter {
    private boolean repeating = false;
    final AudioPlayer player;
    public final Queue<AudioTrack> queue;
    AudioTrack lastTrack;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true))
        {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        AudioTrack track = queue.poll();
        player.startTrack(track, false);

        if (track != null) {
            Member member = (Member) track.getUserData();
            GuildMusicManager mng = getMusicManager(member.getGuild(), null);


            MessageAction msg = mng.channel.sendMessageEmbeds(Embeds.getTrackEmbed(track, Main.getJDA()).build()).setActionRow(
                    Button.success("cmd:pause:" + member.getGuild().getId(), "Pausar / Continuar"),
                    Button.primary("cmd:skip:" + member.getGuild().getId(), "Saltar"),
                    Button.danger("cmd:stop:" + member.getGuild().getId(), "Parar")
            );
            msg.queue(message -> track.setUserData(member));
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;

        if (endReason.mayStartNext)
        {
            if (repeating) {
                player.startTrack(lastTrack.makeClone(), false);
            }
            else {
                nextTrack();
            }
        }
    }
    public boolean isRepeating()
    {
        return repeating;
    }

    public void setRepeating(boolean repeating)
    {
        this.repeating = repeating;
    }

    public void shuffle()
    {
        Collections.shuffle((List<?>) queue);
    }
}