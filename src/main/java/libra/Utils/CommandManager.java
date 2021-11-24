package libra.Utils;

import com.mongodb.DBObject;
import libra.Commands.Avatar;
import libra.Database.Database;
import libra.Commands.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager {

    private final List<Command> commands = new ArrayList<>();

    public List<Command> getCommands() {
        return commands;
    }

    public CommandManager() {
        addCommand(new Ping());
        addCommand(new Help(this));
        addCommand(new Avatar());
        addCommand(new Info());
    }

    public void addCommand(Command cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if(nameFound) {
            throw new IllegalArgumentException("Ya existe un comando con ese nombre!");
        }

        commands.add(cmd);
    }

    @Nullable
    public Command getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (Command command : this.commands) {
            if(command.getName().equals(searchLower)){
                return command;
            }
        }

        return null;
    }

    public List<Command> getCommandsByCategory(String Category) {
        List<Command> Commands = new ArrayList<>(Collections.emptyList());

        this.commands.forEach((cmd) -> {
            if(cmd.getCategory().equals(Category)) {
                Commands.add(cmd);
            }
        });

        return Commands;
    }

    public void run(SlashCommandEvent event) {

        if(event.getGuild() == null) return;
        DBObject Guild = Database.getGuildDocument(event.getGuild().getId());

        String invoke = event.getName();
        Command cmd = this.getCommand(invoke);

        if(cmd != null) {
            cmd.run(event, Guild);
        }
    }

}
