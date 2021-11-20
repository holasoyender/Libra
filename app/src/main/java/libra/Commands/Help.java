package libra.Commands;

import libra.Utils.Command;
import libra.Utils.CommandContext;
import libra.Utils.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Help implements Command {

    private final CommandManager manager;

    public Help(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void run(CommandContext context) {

        List<String> args = context.getArgs();
        TextChannel channel = context.getChannel();

        if (args.isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("Lista de comandos ", context.getSelfUser().getAvatarUrl())
                    .setFooter("> " + context.getAuthor().getAsTag(), context.getAuthor().getAvatarUrl())
                    .setThumbnail(context.getSelfUser().getAvatarUrl())
                    .setColor(Color.decode("#8F45E2"))
                    .setTimestamp(Instant.now());

            manager.getCommands().stream().map(Command::getName).forEach(
                    (cmd) -> embed.addField("`"+cmd+"`", Objects.requireNonNull(manager.getCommand(cmd)).getDescription() , true)
            );
            channel.sendMessageEmbeds(embed.build()).queue();

            return;
        }

        String search = args.get(0).toLowerCase();
        Command command = manager.getCommand(search);

        if (command == null) {
            channel.sendMessage("Ese comando no existe!").queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Comando " + command.getName(), context.getSelfUser().getAvatarUrl())
                .setFooter("> " + context.getAuthor().getAsTag(), context.getAuthor().getAvatarUrl())
                .setThumbnail(context.getSelfUser().getAvatarUrl())
                .setColor(Color.decode("#8F45E2"))
                .setTimestamp(Instant.now())
                .addField("Nombre", command.getName(), true)
                .addField("Descripción", command.getDescription(), true)
                .addField("Forma de uso", command.getUsage(), true)
                .addField("Permisos", command.getPermissions(), true);

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public String getDescription() {
        return "Información de los comandos del bot";
    }

    @Override
    public String getUsage() {
        return "help [Comando]";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getAliases() {
        return List.of("h", "commands", "comandos");
    }
}
