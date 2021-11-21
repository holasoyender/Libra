package libra.Utils;

import com.mongodb.DBObject;

import java.util.List;

public interface Command {

    void run(CommandContext context, DBObject Guild);

    String getName();

    String getDescription();
    String getUsage();
    String getPermissions();

    default List<String> getAliases() {
        return List.of();
    }


}
