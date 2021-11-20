package libra.Events;

import libra.Utils.CommandManager;
import libra.Utils.Config;
import libra.Utils.Logger;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Listener extends ListenerAdapter {

    private final Logger Logger = new Logger().getLogger();

    private final Config config = new Config().getConfig();
    private final CommandManager manager = new CommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.EventLogger.info("Cliente iniciado como {}", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if(user.isBot() || event.isWebhookMessage()) return;

        String prefix = config.Prefix;
        String raw = event.getMessage().getContentRaw();

        if(!raw.startsWith(prefix)) return;

        manager.run(event);
    }
}
