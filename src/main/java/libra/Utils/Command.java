package libra.Utils;

import com.mongodb.DBObject;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

public interface Command {

    void run(SlashCommandEvent context, Document Guild);

    String getName();

    String getDescription();
    String getUsage();
    String getPermissions();
    String getCategory();

    CommandData getSlashData();

}
