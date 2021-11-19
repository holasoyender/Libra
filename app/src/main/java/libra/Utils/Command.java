package libra.Utils;
import java.util.List;

public interface Command {

    void handle(CommandContext context);
    String getName();

    default List<String> getAliases() {
        return List.of();
    }


}
