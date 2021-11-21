package libra.Utils;

import com.mongodb.DBObject;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public interface Command {

    void run(SlashCommandEvent context, DBObject Guild);

    String getName();

    String getDescription();
    String getUsage();
    String getPermissions();

    CommandData getSlashData();

    default List<String> getAliases() {
        return List.of();
    }


}
