package libra.Commands.Music;

import libra.Config.Config;
import libra.Lavaplayer.Player;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

public class Play implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getGuild() == null) return;

        OptionMapping raw = context.getOption("canción");

        if (raw == null) {
            context.reply(config.getEmojis().Error + " No has especificado ninguna canción").setEphemeral(true).queue();
            return;
        }

        String Search = raw.getAsString();

        if (Search.length() < 1) {
            context.reply(config.getEmojis().Error + " No has especificado ninguna canción").setEphemeral(true).queue();
            return;
        }

        Member Member = context.getMember();

        if (Member == null || Member.getVoiceState() == null || context.getGuild().getSelfMember().getVoiceState() == null) {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        if(!Member.getVoiceState().inVoiceChannel()) {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        VoiceChannel VoiceChannel = Member.getVoiceState().getChannel();

        if (context.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            if (context.getGuild().getSelfMember().getVoiceState().getChannel() != VoiceChannel) {
                context.reply(config.getEmojis().Error + " Debes de estar en el mismo canal de voz que yo!").setEphemeral(true).queue();
            }
        } else {
            context.getGuild().getAudioManager().openAudioConnection(VoiceChannel);
            context.getGuild().getAudioManager().setSelfDeafened(true);
        }

        Player Player = new Player();
        Player.loadAndPlay(context, Search);

    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Reproduce una canción en el canal de voz";
    }

    @Override
    public String getUsage() {
        return "play <Canción>";
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
        return new CommandData(this.getName(), this.getDescription()).addOption(OptionType.STRING, "canción", "Canción a reproducir en el canal de voz", true);
    }
}