package libra.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import libra.Config.Config;
import libra.Lavaplayer.GuildMusicManager;
import libra.Lavaplayer.TrackScheduler;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import static libra.Lavaplayer.Player.getMusicManager;

public class Seek implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getGuild() == null) return;

        net.dv8tion.jda.api.entities.Guild guild = context.getGuild();
        GuildMusicManager mng = getMusicManager(guild, null);
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;
        OptionMapping raw = context.getOption("segundos");

        Member Member = context.getMember();

        if (Member == null || Member.getVoiceState() == null || context.getGuild().getSelfMember().getVoiceState() == null) {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        if (!Member.getVoiceState().inVoiceChannel()) {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel = Member.getVoiceState().getChannel();

        if (context.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            if (context.getGuild().getSelfMember().getVoiceState().getChannel() != VoiceChannel) {
                context.reply(config.getEmojis().Error + " Debes de estar en el mismo canal de voz que yo!").setEphemeral(true).queue();
                return;
            }
        } else {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        if (player.getPlayingTrack() == null) {
            context.reply(config.getEmojis().Error + " No hay ninguna canción sonando!").setEphemeral(true).queue();
            return;
        }

        if (raw == null) {
            context.reply(config.getEmojis().Error + "Debes de especificar un segundo!").setEphemeral(true).queue();
            return;
        }

        long pos = raw.getAsLong() * 1000;

        if(pos > player.getPlayingTrack().getDuration()) {
            context.reply(config.getEmojis().Error + " El segundo especificado es mayor que la duración de la canción!").setEphemeral(true).queue();
            return;
        }
        if(pos < 0) {
            context.reply(config.getEmojis().Error + " El segundo especificado es menor que 0!").setEphemeral(true).queue();
            return;
        }

        player.getPlayingTrack().setPosition(pos);
        context.reply(config.getEmojis().Success + "La canción se ha avanzado hasta el segundo **"+pos/1000+"**!").queue();
    }

    @Override
    public String getName() {
        return "seek";
    }

    @Override
    public String getDescription() {
        return "Ir al segundo exacto de la canción";
    }

    @Override
    public String getUsage() {
        return "seek";
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
        return new CommandData(this.getName(), this.getDescription()).addOption(OptionType.INTEGER, "segundos", "Segundo al que avanzar", true);
    }
}