package libra.Utils.Command;

import libra.Commands.Bot.*;
import libra.Commands.Config.*;
import libra.Commands.Info.*;
import libra.Commands.Moderation.*;
import libra.Commands.Music.*;
import libra.Commands.Ocio.*;

import libra.Config.Config;
import libra.Database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class CommandManager {

    private final List<Command> commands = new ArrayList<>();
    private final Config config = new Config();

    public List<Command> getCommands() {
        return commands;
    }

    public CommandManager() {
        addCommand(new Clear());
        addCommand(new Deshabilitados(this));
        addCommand(new Habilitar());
        addCommand(new Deshabilitar(this));
        addCommand(new GitHub());
        addCommand(new SetLogs());
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

        if (nameFound) {
            throw new IllegalArgumentException("Ya existe un comando con ese nombre!");
        }

        commands.add(cmd);
    }

    @Nullable
    public Command getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (Command command : this.commands) {
            if (command.getName().equals(searchLower)) {
                return command;
            }
        }

        return null;
    }

    public List<Command> getCommandsByCategory(String Category) {
        List<Command> Commands = new ArrayList<>(Collections.emptyList());

        this.commands.forEach((cmd) -> {
            if (cmd.getCategory().equals(Category)) {
                Commands.add(cmd);
            }
        });

        return Commands;
    }

    public void run(SlashCommandEvent event) {

        if (event.getGuild() == null || event.getMember() == null) {
            event.reply("No puedo ejecutar comandos en mensajes privados!").queue();
            return;
        }

        Document Guild = Database.getGuildDocument(event.getGuild().getId());

        String invoke = event.getName();
        Command cmd = this.getCommand(invoke);

        if (cmd != null) {

            if (Guild != null) {
                ArrayList<String> disabledCommands = (ArrayList<String>) Guild.get("DisabledCommands");

                if (disabledCommands != null) {
                    if (disabledCommands.contains(cmd.getName())) {
                        if(!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                            event.reply(config.getEmojis().Error + "Este comando está deshabilitado para este servidor!").setEphemeral(true).queue();
                            return;
                        }
                    }
                }
            }

            String LogChannelID = Database.getLogChannelIDByGuildID(event.getGuild().getId());

            if (LogChannelID != null) {
                if (!LogChannelID.isEmpty()) {
                    TextChannel LogChannel = event.getGuild().getTextChannelById(LogChannelID);
                    if (LogChannel != null) {

                        EmbedBuilder Embed = new EmbedBuilder()
                                .setColor(config.getEmbedColor())
                                .setAuthor("Comando ejecutado", null, event.getJDA().getSelfUser().getAvatarUrl())
                                .setThumbnail(event.getMember().getUser().getAvatarUrl())
                                .addField(event.getMember().getUser().getAsTag(), String.format("```yaml\nID: %s```", event.getMember().getUser().getId()), false)
                                .addField("Comando", "`/" + cmd.getName() + "`", true)
                                .addField("Canal", "<#" + event.getChannel().getId() + ">", true);

                        LogChannel.sendMessageEmbeds(Embed.build()).queue();
                    }
                }
            }

            cmd.run(event, Guild, config);
        }
    }

}
