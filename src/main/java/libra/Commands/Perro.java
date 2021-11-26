package libra.Commands;

import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

public class Perro implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        context.reply("En desarrollo!").setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "perro";
    }

    @Override
    public String getDescription() {
        return "Manda una foto aleatorio de un perro";
    }

    @Override
    public String getUsage() {
        return "perro";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription());
    }
}
