package libra.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import libra.Config.Config;
import libra.Lavaplayer.GuildMusicManager;
import libra.Lavaplayer.TrackScheduler;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import static libra.Lavaplayer.Player.getMusicManager;

public class Loop implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getGuild() == null) return;

        GuildMusicManager mng = getMusicManager(context.getGuild(), null);
        OptionMapping raw = context.getOption("activado");
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;

        if (raw == null) {
            context.reply(config.getEmojis().Volume + "Debes de especificar si activar o desactivar el bucle.").setEphemeral(true).queue();
            return;
        }

        boolean loop = raw.getAsBoolean();
        Member Member = context.getMember();

        if (Member == null || Member.getVoiceState() == null || context.getGuild().getSelfMember().getVoiceState() == null) {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        if (!Member.getVoiceState().inVoiceChannel()) {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        VoiceChannel VoiceChannel = Member.getVoiceState().getChannel();

        if (context.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            if (context.getGuild().getSelfMember().getVoiceState().getChannel() != VoiceChannel) {
                context.reply(config.getEmojis().Error + " Debes de estar en el mismo canal de voz que yo!").setEphemeral(true).queue();
                return;
            }
        } else {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        if (player.getPlayingTrack() == null)
        {
            context.reply(config.getEmojis().Error + " No hay ninguna canción sonando!").setEphemeral(true).queue();
            return;
        }

        if(scheduler.isRepeating() && loop) {
            context.reply(config.getEmojis().Error + "El bucle ya está activado!").setEphemeral(true).queue();
            return;
        }
        if(!scheduler.isRepeating() && !loop) {
            context.reply(config.getEmojis().Error + "El bucle no está activado en esta canción!").setEphemeral(true).queue();
            return;
        }

        scheduler.setRepeating(loop);
        context.reply(config.getEmojis().Success + " El bucle de la canción ha sido " + (loop ? "activado" : "desactivado") + "!").setEphemeral(false).queue();

    }

    @Override
    public String getName() {
        return "loop";
    }

    @Override
    public String getDescription() {
        return "Activar o desactivar el bucle de la canción";
    }

    @Override
    public String getUsage() {
        return "loop <Activar/Desactivar>";
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
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.BOOLEAN, "activado", "Activar o desactivar el bucle de la canción sonando", true));
    }
}
