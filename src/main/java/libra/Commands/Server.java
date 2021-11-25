package libra.Commands;

import com.mongodb.DBObject;
import libra.Utils.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Server implements Command {
    @Override
    public void run(SlashCommandEvent context, DBObject Guild) {
        context.reply("En desarrollo").setEphemeral(true).queue();

    }

    @Override
    public String getName() {
        return "server";
    }

    @Override
    public String getDescription() {
        return "Muestra la información del servidor";
    }

    @Override
    public String getUsage() {
        return "server";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Información";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription());
    }
}
