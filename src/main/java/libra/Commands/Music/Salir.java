package libra.Commands.Music;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

public class Salir implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if(context.getGuild() == null) return;

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
            context.reply(config.getEmojis().Error + " Debo de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        context.getGuild().getAudioManager().closeAudioConnection();
        context.reply(config.getEmojis().Success + " He salido del canal de voz!").setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "salir";
    }

    @Override
    public String getDescription() {
        return "Hace que el bot salga del canal de voz";
    }

    @Override
    public String getUsage() {
        return "salir";
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
