package libra.Events;

import libra.Utils.Command;
import libra.Utils.CommandManager;
import libra.Config.Config;
import libra.Utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

public class Listener extends ListenerAdapter {

    private final Logger Logger = new Logger().getLogger();
    private final CommandManager manager = new CommandManager();
    private final Config config = new Config().getConfig();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.EventLogger.info("Cliente iniciado como {}", event.getJDA().getSelfUser().getAsTag());

        CommandListUpdateAction Commands = event.getJDA().getGuildById("704029755975925841").updateCommands();
        /*
        * Para el uso global de los slash usa esto (El otro es solo para el servidor de test)
        * CommandListUpdateAction Commands = event.getJDA().updateCommands();
        * */

        CommandManager manager = new CommandManager();
        List<Command> commands = manager.getCommands();


        for(Command command : commands) {
            Commands.addCommands(command.getSlashData()).queue();
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        User user = event.getUser();

        if(user.isBot() || event.getGuild() == null) return;

        manager.run(event);
    }

    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        String Id = event.getValues().get(0);
        String[] Args = Id.split(":");

        if(Args[0].equals("cmd")) {

            switch (Args[1]) {

                case "help":

                    EmbedBuilder Embed = new EmbedBuilder()
                            .setAuthor("Sección de "+Args[2], null, event.getJDA().getSelfUser().getAvatarUrl())
                            .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                            .setColor(config.EmbedColor)
                            .setTimestamp(Instant.now())
                            .setDescription("Usa `help <Comando>` para más información sobre un comando específico");

                    manager.getCommandsByCategory(Args[2]).forEach((cmd) -> Embed.addField("`"+cmd.getName()+"`", cmd.getDescription(), true));
                    event.editMessageEmbeds(Embed.build()).queue();

                    break;

                default:
                    event.reply(config.Emojis.Error+"Interacción desconocida!").setEphemeral(true).queue();
                    break;
            }
        }
    }
}
