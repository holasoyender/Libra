package libra.Utils.Command;

import libra.Commands.Bot.*;
import libra.Commands.Info.*;
import libra.Commands.Music.Play;
import libra.Commands.Ocio.*;

import libra.Config.Config;
import libra.Database.Database;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager {

    private final List<Command> commands = new ArrayList<>();
    private final Config config = new Config();
    public List<Command> getCommands() {
        return commands;
    }

    public CommandManager() {
        addCommand(new Juntos());
        addCommand(new Recordar());
        addCommand(new Play());
        addCommand(new Tirar());
        addCommand(new Soporte());
        addCommand(new Gato());
        addCommand(new Perro());
        addCommand(new Invitar());
        addCommand(new Server());
        addCommand(new Bot());
        addCommand(new Ping());
        addCommand(new Help(this));
        addCommand(new Avatar());
        addCommand(new Info());
        addCommand(new Say());
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
        Document Guild = Database.getGuildDocument(event.getGuild().getId());

        String invoke = event.getName();
        Command cmd = this.getCommand(invoke);

        if(cmd != null) {
            cmd.run(event, Guild, config);
        }
    }

}
