package libra.Commands;

import libra.Utils.Command;
import libra.Utils.CommandManager;
import libra.Config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.time.Instant;

import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.bson.Document;


public class Help implements Command {

    private final CommandManager manager;
    private final Config config = new Config().getConfig();

    public Help(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void run(SlashCommandEvent context, Document Guild) {

        OptionMapping CommandOption = context.getOption("comando");
        if (CommandOption == null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("Lista de comandos ", null , context.getJDA().getSelfUser().getAvatarUrl())
                    .setFooter("> " + context.getUser().getAsTag(), context.getUser().getAvatarUrl())
                    .setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
                    .setColor(config.EmbedColor)
                    .setTimestamp(Instant.now())
                    .setDescription("**Hola** :wave:, soy `Libra`. Un bot multifunción completamente en español para **Discord**\n**Navega por el menú para ver los comandos en función de su categoría!**\n\n **[Invitame!](https://discord.com/api/oauth2/authorize?client_id=" + context.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands)** - **[Servidor de Soporte](https://discord.gg/Rwy8J35)**");


            context.replyEmbeds(embed.build()).addActionRow(
                    SelectionMenu.create("cmd:help")
                            .setPlaceholder("Elija la categoría")
                            .addOption("Información", "cmd:help:Información:"+context.getUser().getId(), "Lista de comandos de la sección de información", Emoji.fromUnicode("\uD83D\uDCA1"))
                            .addOption("Bot", "cmd:help:Bot:"+context.getUser().getId(), "Lista de comandos de la sección de Bot", Emoji.fromUnicode("\uD83E\uDD16"))
                            .addOption("Música", "cmd:help:Música:"+context.getUser().getId(), "Lista de comandos de la sección de Música", Emoji.fromUnicode("\uD83C\uDFB5"))
                            .addOption("Ocio", "cmd:help:Ocio:"+context.getUser().getId(), "Lista de comandos de la sección de Ocio", Emoji.fromUnicode("\uD83D\uDEF9"))
                            .build()
            ).queue();

            return;
        }

        Command command = manager.getCommand(CommandOption.getAsString());

        if (command == null) {
            context.reply(config.Emojis.Error+"Ese comando no existe!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Comando: " + command.getName(), context.getJDA().getSelfUser().getAvatarUrl())
                .setFooter("> " + context.getUser().getAsTag(), context.getUser().getAvatarUrl())
                .setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
                .setColor(config.EmbedColor)
                .setTimestamp(Instant.now())
                .setDescription("Para listar todos los comandos puedes usar `/help`\n\n**Nombre del comando:** `"+command.getName()+"`\n**Descripción:** "+command.getDescription()+"\n**Forma de uso:** `/"+command.getUsage()+"`\n**Permisos:** "+command.getPermissions());

        context.replyEmbeds(embed.build()).addActionRow(
                Button.primary("cmd:help:Main:"+context.getUser().getId(), "Todos los comandos")
        ).queue();
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
    public String getCategory() {
        return "Bot";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.STRING, "comando", "Nombre del comando a recibir ayuda"));
    }
}