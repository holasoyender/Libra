package libra.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import libra.Config.Config;
import libra.Lavaplayer.GuildMusicManager;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import static libra.Lavaplayer.Player.getMusicManager;

public class Resume implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getGuild() == null) return;

        net.dv8tion.jda.api.entities.Guild guild = context.getGuild();
        GuildMusicManager mng = getMusicManager(guild, null);
        AudioPlayer player = mng.player;

        net.dv8tion.jda.api.entities.Member Member = context.getMember();

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
            context.reply(config.getEmojis().Error + " No hay ninguna canci??n sonando!").setEphemeral(true).queue();
            return;
        }

        if (!player.isPaused()) {
            context.reply(config.getEmojis().Error + "La canci??n actual no est?? pausada!").setEphemeral(true).queue();
            return;
        }

        player.setPaused(!player.isPaused());
        context.reply(config.getEmojis().Success + "La canci??n actual se ha resumido!").queue();

    }

    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getDescription() {
        return "Reanudar la canci??n pausada";
    }

    @Override
    public String getUsage() {
        return "resume";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "M??sica";
    }

    @Override
    public CommandData getSlashData() {
        return null;
    }
}