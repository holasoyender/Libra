package libra.Commands.Music;

import libra.Config.Config;
import libra.Lavaplayer.GuildMusicManager;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import java.net.MalformedURLException;
import java.net.URL;

import static libra.Lavaplayer.Player.getMusicManager;
import static libra.Lavaplayer.Player.loadAndPlay;

public class Play implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getGuild() == null) return;

        GuildMusicManager mng = getMusicManager(context.getGuild());
        OptionMapping raw = context.getOption("canción");
        boolean isSearch = false;

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

        if (!Member.getVoiceState().inVoiceChannel()) {
            context.reply(config.getEmojis().Error + " Debes de estar en un canal de voz!").setEphemeral(true).queue();
            return;
        }

        VoiceChannel VoiceChannel = Member.getVoiceState().getChannel();

        if (context.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            if (context.getGuild().getSelfMember().getVoiceState().getChannel() != VoiceChannel) {
                context.reply(config.getEmojis().Error + " Debes de estar en el mismo canal de voz que yo!").setEphemeral(true).queue();
            }
        } else {
            context.getGuild().getAudioManager().setSendingHandler(mng.sendHandler);
            try {

                context.getGuild().getAudioManager().openAudioConnection(VoiceChannel);
                context.getGuild().getAudioManager().setSelfDeafened(true);

            } catch (PermissionException e) {
                if (e.getPermission() == Permission.VOICE_CONNECT) {
                    context.reply(config.getEmojis().Error + "No tengo permisos para conectarme a ese canal de voz!").setEphemeral(true).queue();
                    return;
                }
            }
        }
        if(!isUrl(Search)){
            Search = "ytsearch:" + Search;
            isSearch = true;
        }

        loadAndPlay(mng, context, Search, isSearch);

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

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}