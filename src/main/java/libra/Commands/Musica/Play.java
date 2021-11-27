package libra.Commands.Musica;

import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

public class Play implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Poner una canción en el canal de voz";
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
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.STRING, "canción", "Nombre o enlace de la canción", true));
    }
}
