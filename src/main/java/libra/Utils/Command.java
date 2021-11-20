package libra.Utils;

import java.util.List;

public interface Command {

    void run(CommandContext context);

    String getName();

    String getDescription();
    String getUsage();
    String getPermissions();

    default List<String> getAliases() {
        return List.of();
    }


}
