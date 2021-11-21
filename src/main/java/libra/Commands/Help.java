package libra.Commands;

import com.mongodb.DBObject;
import libra.Utils.Command;
import libra.Utils.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

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
    public void run(SlashCommandEvent context, DBObject Guild) {

        OptionMapping CommandOption = context.getOption("comando");
        if(CommandOption == null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("Lista de comandos ", context.getJDA().getSelfUser().getAvatarUrl())
                    .setFooter("> " + context.getUser().getAsTag(), context.getUser().getAvatarUrl())
                    .setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
                    .setColor(Color.decode("#8F45E2"))
                    .setTimestamp(Instant.now());

            manager.getCommands().stream().map(Command::getName).forEach(
                    (cmd) -> embed.addField("`"+cmd+"`", Objects.requireNonNull(manager.getCommand(cmd)).getDescription() , true)
            );
            context.replyEmbeds(embed.build()).queue();

            return;
        }

        Command command = manager.getCommand(CommandOption.getAsString());

        if (command == null) {
            context.reply("Ese comando no existe!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Comando " + command.getName(), context.getJDA().getSelfUser().getAvatarUrl())
                .setFooter("> " + context.getUser().getAsTag(), context.getUser().getAvatarUrl())
                .setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
                .setColor(Color.decode("#8F45E2"))
                .setTimestamp(Instant.now())
                .addField("Nombre", command.getName(), true)
                .addField("Descripción", command.getDescription(), true)
                .addField("Forma de uso", command.getUsage(), true)
                .addField("Permisos", command.getPermissions(), true);

        context.replyEmbeds(embed.build()).queue();
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

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.STRING, "comando", "Nombre del comando a recibir ayuda"));
    }
}
