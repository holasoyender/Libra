package libra.Commands.Ocio;

import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

public class Juntos implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        context.reply("En desarrollo").setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "juntos";
    }

    @Override
    public String getDescription() {
        return "Lanzar el menú de Discord Together";
    }

    @Override
    public String getUsage() {
        return "juntos";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Ocio";
    }

    @Override
    public CommandData getSlashData() {
        return null;
    }
}
