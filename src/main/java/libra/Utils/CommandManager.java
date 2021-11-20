package libra.Utils;

import libra.Commands.Help;
import libra.Commands.Ping;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {

    private final List<Command> commands = new ArrayList<>();
    private final Config config = new Config().getConfig();

    public CommandManager(){
        addCommand(new Ping());
        addCommand(new Help(this));
    }

    private void addCommand(Command cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if(nameFound) {
            throw new IllegalArgumentException("Ya existe un comando con ese nombre!");
        }
        
        commands.add(cmd);
    }

    public List<Command> getCommands() {
        return commands;
    }

    @Nullable
    public Command getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (Command command : this.commands) {
            if(command.getName().equals(searchLower) || command.getAliases().contains(searchLower)){
                return command;
            }
        }

        return null;
    }

    public void run(GuildMessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)"+ Pattern.quote(config.Prefix), "").split("\\s+");

        String invoke = split[0].toLowerCase();
        Command cmd = this.getCommand(invoke);

        if(cmd != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);
            CommandContext context = new CommandContext(event, args);
            cmd.run(context);
        }
    }

}
