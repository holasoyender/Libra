package libra.Events;

import libra.Utils.Command;
import libra.Utils.CommandManager;
import libra.Config.Config;
import libra.Utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
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

        Guild Guild = event.getJDA().getGuildById("903340301442252821");
        if(Guild == null) {
            System.out.println("No existe el servidor de tests");
            return;
        }
        CommandListUpdateAction Commands = Guild.updateCommands();
        Commands.queue();

        CommandListUpdateAction GlobalCommands = event.getJDA().updateCommands();
        GlobalCommands.queue();


        CommandManager manager = new CommandManager();
        List<Command> commands = manager.getCommands();


        for(Command command : commands) {
            System.out.println("Registrado el comando "+command.getName());
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

                    if(!event.getUser().getId().equals(Args[3])) {
                        event.reply(config.Emojis.Error+"No puedes usar este menú!").setEphemeral(true).queue();
                        return;
                    }

                        EmbedBuilder Embed = new EmbedBuilder()
                                .setAuthor("Sección de " + Args[2], null, event.getJDA().getSelfUser().getAvatarUrl())
                                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                                .setColor(config.EmbedColor)
                                .setTimestamp(Instant.now())
                                .setDescription("Usa `help <Comando>` para más información sobre un comando específico");

                        manager.getCommandsByCategory(Args[2]).forEach((cmd) -> Embed.addField("`" + cmd.getName() + "`", cmd.getDescription(), true));
                        event.editMessageEmbeds(Embed.build()).queue();


                    break;

                default:
                    event.reply(config.Emojis.Error+"Interacción desconocida!").setEphemeral(true).queue();
                    break;
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        String Id = event.getComponentId();
        String[] Args = Id.split(":");

        if(Args[0].equals("cmd")) {

            switch (Args[1]) {

                case "help":

                    if(Args[2].equals("Main")) {

                        EmbedBuilder Embed = new EmbedBuilder()
                                .setAuthor("Lista de comandos ", null , event.getJDA().getSelfUser().getAvatarUrl())
                                .setFooter("> " + event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                                .setColor(config.EmbedColor)
                                .setTimestamp(Instant.now())
                                .setDescription("**Hola** :wave:, soy `Libra`. Un bot multifunción completamente en español para **Discord**\n**Navega por el menú para ver los comandos en función de su categoría!**\n\n **[Invitame!](https://discord.com/api/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands)** - **[Servidor de Soporte](https://discord.gg/Rwy8J35)**");


                        event.editMessageEmbeds(Embed.build()).setActionRow(
                                SelectionMenu.create("cmd:help")
                                        .setPlaceholder("Elija la categoría")
                                        .addOption("Información", "cmd:help:Información:"+event.getUser().getId(), "Lista de comandos de la sección de información", Emoji.fromUnicode("\uD83D\uDCA1"))
                                        .addOption("Bot", "cmd:help:Bot:"+event.getUser().getId(), "Lista de comandos de la sección de Bot", Emoji.fromUnicode("\uD83E\uDD16"))
                                        .addOption("Música", "cmd:help:Música:"+event.getUser().getId(), "Lista de comandos de la sección de Música", Emoji.fromUnicode("\uD83C\uDFB5"))
                                        .addOption("Ocio", "cmd:help:Ocio:"+event.getUser().getId(), "Lista de comandos de la sección de Ocio", Emoji.fromUnicode("\uD83D\uDEF9"))
                                        .build()
                        ).queue();

                    }

                    break;
            }
        }

    }
}
