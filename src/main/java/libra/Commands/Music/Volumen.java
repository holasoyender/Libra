package libra.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import libra.Config.Config;
import libra.Lavaplayer.GuildMusicManager;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import static libra.Lavaplayer.Player.getMusicManager;

public class Volumen implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getGuild() == null) return;

        GuildMusicManager mng = getMusicManager(context.getGuild());
        OptionMapping raw = context.getOption("volumen");
        AudioPlayer player = mng.player;

        if (raw == null) {
            context.reply(config.getEmojis().Volume + "El volumen actual de la canción es del **"+player.getVolume()+"%**.").setEphemeral(true).queue();
            return;
        }

        long newVolume = raw.getAsLong();

        if (newVolume > 100 || newVolume < 10) {
            context.reply(config.getEmojis().Error + " El volumen debe de ser un número entre 10 y 100!").setEphemeral(true).queue();
            return;
        }
        context.reply(config.getEmojis().Success+ " El volumen de la canción ha cambiado de **"+player.getVolume()+"%** a **"+newVolume+"%**.").setEphemeral(true).queue();

        player.setVolume((int) newVolume);
    }

    @Override
    public String getName() {
        return "volumen";
    }

    @Override
    public String getDescription() {
        return "Cambiar o ver el volumen de la canción";
    }

    @Override
    public String getUsage() {
        return "volumen <Volumen>";
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
                .addOptions(new OptionData(OptionType.INTEGER, "volumen", "Volumen a establecer para la canción", false));
    }
}
