package libra.Events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener extends ListenerAdapter {

    private static final Logger Logger = LoggerFactory.getLogger("Event");

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.info("Cliente iniciado como {}", event.getJDA().getSelfUser().getAsTag());
    }
}
