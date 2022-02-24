package libra.Events;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import libra.Lavaplayer.GuildMusicManager;
import libra.Lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static libra.Lavaplayer.Player.getMusicManager;

public class Voice extends ListenerAdapter {

    @Override
    public void onGuildVoiceGuildMute(@NotNull GuildVoiceGuildMuteEvent event) {

        if (!event.getMember().getUser().equals(event.getJDA().getSelfUser())) return;

        if (event.getMember().getVoiceState() == null) return;
        if (event.getMember().getVoiceState().isGuildMuted() || event.getMember().getVoiceState().isMuted()) {

            GuildMusicManager mng = getMusicManager(event.getGuild(), null);
            AudioPlayer player = mng.player;

            player.setPaused(true);
        }
        if (!event.getMember().getVoiceState().isGuildMuted() || !event.getMember().getVoiceState().isMuted()) {

            GuildMusicManager mng = getMusicManager(event.getGuild(), null);
            AudioPlayer player = mng.player;

            player.setPaused(false);
        }

    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {

        GuildMusicManager mng = getMusicManager(event.getGuild(), null);
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;

        if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            scheduler.queue.clear();
            player.stopTrack();
            player.setPaused(false);
        }

        if(event.getChannelLeft().getMembers().size() == 1) {
            scheduler.queue.clear();
            player.stopTrack();
            player.setPaused(false);

            if(event.getChannelLeft().equals(event.getGuild().getAudioManager().getConnectedChannel())) {
                event.getGuild().getAudioManager().closeAudioConnection();
            }

        }

    }
}
