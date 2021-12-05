package libra.Events;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import libra.Config.Config;
import libra.Functions.DiscordTogether;
import libra.Functions.Recordatorios;
import libra.Utils.Command.Command;
import libra.Utils.Command.CommandManager;
import libra.Utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

public class Listener extends ListenerAdapter {

    private final Logger Logger = new Logger().getLogger();
    private final CommandManager manager = new CommandManager();
    private final Config config = new Config();

    private WebhookClient internalLogWebhook = null;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.EventLogger.info("Cliente iniciado como {}", event.getJDA().getSelfUser().getAsTag());

        Recordatorios.start(event.getJDA());

        Guild Guild = event.getJDA().getGuildById("704029755975925841");
        if (Guild == null) {
            System.out.println("No existe el servidor de tests");
            return;
        }
        CommandListUpdateAction Commands = Guild.updateCommands();

        CommandManager manager = new CommandManager();
        List<Command> commands = manager.getCommands();

        int i = 0;
        for (Command command : commands) {
            i = i + 1;

            if (command.getSlashData() == null) {

                Commands.addCommands(new CommandData(command.getName(), command.getDescription()));

            } else {
                Commands.addCommands(command.getSlashData());
            }

        }
        Commands.queue();
        Logger.LoadLogger.info("Se han cargado " + i + " comandos.");

        if (config.getLogWebhookURL() != null) {
            try {
                WebhookClientBuilder webhookBuilder = new WebhookClientBuilder(config.getLogWebhookURL());
                internalLogWebhook = webhookBuilder.build();
            } catch (Exception e) {
                Logger.EventLogger.error("No se pudo iniciar el webhook logs interno.");
            }
        }

        WebhookEmbed Embed = new WebhookEmbedBuilder()
                .setColor(0x5b6cec)
                .setAuthor(new WebhookEmbed.EmbedAuthor("Shard " + event.getJDA().getShardInfo().getShardId() + " iniciada", event.getJDA().getSelfUser().getAvatarUrl(), null))
                .build();

        if (internalLogWebhook != null)
            internalLogWebhook.send(Embed);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getMentionedMembers().size() > 0) {
            if (event.getMessage().getMentionedMembers().get(0).getId().equals(event.getJDA().getSelfUser().getId())) {
                event.getChannel().sendMessage("**Hola :wave:**\nSoy `Libra`, un bot para Discord completamente en **Español** de código abierto.\nAquí te dejo unos botones para obtener más información").setActionRow(
                        Button.primary("cmd:bot:Main:" + event.getAuthor().getId(), "Lista de comandos"),
                        Button.link("https://libra.kirobot.cc", "Página web"),
                        Button.link("https://discord.com/api/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands", "Invita a Libra")
                ).queue();
            }
        }
        /* ------ Desarrollador ------ */
        if (!event.getAuthor().getId().equals(config.getOwnerID())) return;

        String prefix = config.getDefaultPrefix();
        String raw = event.getMessage().getContentRaw();

        if (!raw.startsWith(prefix)) return;
        String Command = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split(" ")[0];
        String[] args = raw.replace(prefix + Command + " ", "").split(" ");

        switch (Command) {
            case "eval" -> {
                String toEval = String.join(" ", args);
                if (toEval.length() <= 0) return;
                ScriptEngine Script = new ScriptEngineManager().getEngineByName("groovy");
                Script.put("jda", event.getJDA());
                Script.put("shardManager", event.getJDA().getShardManager());
                Script.put("guild", event.getGuild());
                Script.put("channel", event.getChannel());
                Script.put("msg", event.getMessage());
                try {
                    String result = Script.eval(toEval).toString();
                    event.getMessage().reply(String.format("```java\n%s```", result.replaceAll(event.getJDA().getToken(), "T0K3N"))).mentionRepliedUser(false).queue();
                } catch (ScriptException ex) {

                    event.getMessage().reply(String.format("```java\n%s```", ex.getMessage().replaceAll(event.getJDA().getToken(), "T0K3N"))).mentionRepliedUser(false).queue();
                }
            }
            case "shutdown" -> {
                event.getMessage().reply(config.getEmojis().Success + "El bot se ha apagado.").mentionRepliedUser(false).queue();
                event.getJDA().shutdown();
                System.exit(0);
            }
            default -> {
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        event.getChannel().sendMessage("**Hola :wave:**\nSoy `Libra`, un bot para Discord completamente en **Español** de código abierto.\nAquí te dejo unos botones para obtener más información").setActionRow(
                Button.primary("cmd:bot:Main:" + event.getAuthor().getId(), "Lista de comandos"),
                Button.link("https://libra.kirobot.cc", "Página web"),
                Button.link("https://discord.com/api/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands", "Invita a Libra")
        ).queue();

    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        User user = event.getUser();

        if (user.isBot() || event.getGuild() == null) return;

        manager.run(event);
    }

    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        String Id = event.getValues().get(0);
        String[] Args = Id.split(":");
        if (Args[0].equals("cmd")) {

            switch (Args[1]) {
                case "help" -> {
                    if (!event.getUser().getId().equals(Args[3])) {
                        event.reply(config.getEmojis().Error + "No puedes usar este menú!").setEphemeral(true).queue();
                        return;
                    }
                    EmbedBuilder Embed = new EmbedBuilder()
                            .setAuthor("Sección de " + Args[2], null, event.getJDA().getSelfUser().getAvatarUrl())
                            .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                            .setColor(config.getEmbedColor())
                            .setTimestamp(Instant.now())
                            .setDescription("Usa `help <Comando>` para más información sobre un comando específico");
                    manager.getCommandsByCategory(Args[2]).forEach((cmd) -> Embed.addField("`" + cmd.getName() + "`", cmd.getDescription(), true));
                    event.editMessageEmbeds(Embed.build()).queue();
                }
                case "together" -> {
                    if (!event.getUser().getId().equals(Args[3])) {
                        event.reply(config.getEmojis().Error + "  No puedes usar este menú").setEphemeral(true).queue();
                    }
                    DiscordTogether.handleDiscordTogether(event, Args[2]);
                }
                default -> event.reply(config.getEmojis().Error + "Interacción desconocida!").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        String Id = event.getComponentId();
        String[] Args = Id.split(":");

        if (Args[0].equals("cmd")) {

            switch (Args[1]) {

                case "help":
                case "bot":

                    if (Args[2].equals("Main")) {

                        if (!event.getUser().getId().equals(Args[3])) {
                            event.reply(config.getEmojis().Error + "No puedes usar este botón!").setEphemeral(true).queue();
                            return;
                        }

                        EmbedBuilder Embed = new EmbedBuilder()
                                .setAuthor("Lista de comandos ", null, event.getJDA().getSelfUser().getAvatarUrl())
                                .setFooter("> " + event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                                .setColor(config.getEmbedColor())
                                .setTimestamp(Instant.now())
                                .setDescription("**Hola** :wave:, soy `Libra`. Un bot multifunción completamente en español para **Discord**\n**Navega por el menú para ver los comandos en función de su categoría!**\n\n **[Invitame!](https://discord.com/api/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands)** - **[Servidor de Soporte](https://discord.gg/Rwy8J35)**");

                        event.editMessageEmbeds(Embed.build()).setContent("").setActionRow(
                                SelectionMenu.create("cmd:help")
                                        .setPlaceholder("Elija la categoría")
                                        .addOption("Información", "cmd:help:Información:" + event.getUser().getId(), "Lista de comandos de la sección de información", Emoji.fromUnicode("\uD83D\uDCA1"))
                                        .addOption("Bot", "cmd:help:Bot:" + event.getUser().getId(), "Lista de comandos de la sección de Bot", Emoji.fromUnicode("\uD83E\uDD16"))
                                        .addOption("Música", "cmd:help:Música:" + event.getUser().getId(), "Lista de comandos de la sección de Música", Emoji.fromUnicode("\uD83C\uDFB5"))
                                        .addOption("Ocio", "cmd:help:Ocio:" + event.getUser().getId(), "Lista de comandos de la sección de Ocio", Emoji.fromUnicode("\uD83D\uDEF9"))
                                        .build()
                        ).queue();

                    }

                    break;

            }
        }

    }

    @Override
    public void onReconnected(@NotNull ReconnectedEvent event) {
        WebhookEmbed Embed = new WebhookEmbedBuilder()
                .setColor(0x68c14e)
                .setAuthor(new WebhookEmbed.EmbedAuthor("Shard " + event.getJDA().getShardInfo().getShardId() + " conectada", event.getJDA().getSelfUser().getAvatarUrl(), null))
                .build();

        if (internalLogWebhook != null)
            internalLogWebhook.send(Embed);
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        WebhookEmbed Embed = new WebhookEmbedBuilder()
                .setColor(0xffce00)
                .setAuthor(new WebhookEmbed.EmbedAuthor("Shard " + event.getJDA().getShardInfo().getShardId() + " desconectada", event.getJDA().getSelfUser().getAvatarUrl(), null))
                .build();

        if (internalLogWebhook != null)
            internalLogWebhook.send(Embed);
    }

    @Override
    public void onResumed(@NotNull ResumedEvent event) {
        WebhookEmbed Embed = new WebhookEmbedBuilder()
                .setColor(0x596ED6)
                .setAuthor(new WebhookEmbed.EmbedAuthor("Shard " + event.getJDA().getShardInfo().getShardId() + " resumida", event.getJDA().getSelfUser().getAvatarUrl(), null))
                .build();

        if (internalLogWebhook != null)
            internalLogWebhook.send(Embed);
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        WebhookEmbed Embed = new WebhookEmbedBuilder()
                .setColor(0xEA4F4C)
                .setAuthor(new WebhookEmbed.EmbedAuthor("Libra se ha apagado", event.getJDA().getSelfUser().getAvatarUrl(), null))
                .build();

        if (internalLogWebhook != null)
            internalLogWebhook.send(Embed);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        WebhookEmbed Embed = new WebhookEmbedBuilder()
                .setColor(0x5CD6FA)
                .setAuthor(new WebhookEmbed.EmbedAuthor("Me han metido en un nuevo servidor servidor", event.getJDA().getSelfUser().getAvatarUrl(), null))
                .addField(new WebhookEmbed.EmbedField(true, "Cantidad de miembros", "" + event.getGuild().getMemberCount()))
                .addField(new WebhookEmbed.EmbedField(true, "Propietario", "<@!" + event.getGuild().getOwnerId() + "> (" + event.getGuild().getOwnerId() + ")"))
                .build();

        if (internalLogWebhook != null)
            internalLogWebhook.send(Embed);
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        WebhookEmbed Embed = new WebhookEmbedBuilder()
                .setColor(0xEB4E4B)
                .setAuthor(new WebhookEmbed.EmbedAuthor("Me han sacado de un servidor", event.getJDA().getSelfUser().getAvatarUrl(), null))
                .addField(new WebhookEmbed.EmbedField(true, "Cantidad de miembros", "" + event.getGuild().getMemberCount()))
                .addField(new WebhookEmbed.EmbedField(true, "Propietario", "<@!" + event.getGuild().getOwnerId() + "> (" + event.getGuild().getOwnerId() + ")"))
                .build();

        if (internalLogWebhook != null)
            internalLogWebhook.send(Embed);
    }

}
