package libra.Utils.Command;

import libra.Config.Config;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

public interface Command {

    void run(SlashCommandEvent context, Document Guild, Config config);

    String getName();

    String getDescription();
    String getUsage();
    String getPermissions();
    String getCategory();

    CommandData getSlashData();

}
