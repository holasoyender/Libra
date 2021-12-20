package libra.Events;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import libra.Config.Config;
import libra.Database.Database;
import libra.Functions.DiscordTogether;
import libra.Functions.Recordatorios;
import libra.Lavaplayer.GuildMusicManager;
import libra.Lavaplayer.TrackScheduler;
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
import net.dv8tion.jda.api.utils.TimeFormat;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import static libra.Lavaplayer.Player.*;

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
        if(event.getGuild() == null) return;
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
                                        .addOption("Moderación", "cmd:help:Moderación:"+event.getUser().getId(), "Lista de comandos de la sección de Moderación", Emoji.fromUnicode("\uD83D\uDDE1️"))
                                        .addOption("Música", "cmd:help:Música:" + event.getUser().getId(), "Lista de comandos de la sección de Música", Emoji.fromUnicode("\uD83C\uDFB5"))
                                        .addOption("Ocio", "cmd:help:Ocio:" + event.getUser().getId(), "Lista de comandos de la sección de Ocio", Emoji.fromUnicode("\uD83D\uDEF9"))
                                        .addOption("Configuración", "cmd:help:Configuración:"+event.getUser().getId(), "Lista de comandos de la sección de Configuración", Emoji.fromUnicode("⚙️"))
                                        .build()
                        ).queue();

                    }

                    break;

                case "infracciones":

                    String UserID = Args[2];
                    int Page = Integer.parseInt(Args[3]);
                    String Action = Args[4];
                    String ModID = Args[5];

                    if (!event.getUser().getId().equals(ModID)) {
                        event.reply(config.getEmojis().Error + "No puedes usar este botón!").setEphemeral(true).queue();
                        return;
                    }

                    List<Document> allInfractions = Database.getInfractionsByID(UserID, event.getGuild().getId()).into(new ArrayList<>());

                    if (allInfractions.isEmpty()) {
                        event.reply(config.getEmojis().Error + "El usuario no tiene infracciones").setEphemeral(true).queue();
                        return;
                    }

                    List<List<Document>> allInfractionsSplit = new ArrayList<>();
                    int i = 0;
                    while (i < allInfractions.size()) {
                        allInfractionsSplit.add(allInfractions.subList(i, Math.min(i + 5, allInfractions.size())));
                        i += 5;
                    }


                    if (Action.equals("back")) {
                        Page -= 1;
                        if(Page < 0) {
                            event.reply(config.getEmojis().Error + "No puedes retroceder más!").setEphemeral(true).queue();
                            return;
                        }
                    }

                    if(Action.equals("next")) {
                        Page += 1;
                        if(Page > allInfractionsSplit.size() - 1) {
                            event.reply(config.getEmojis().Error + "No puedes avanzar más!").setEphemeral(true).queue();
                            return;
                        }
                    }
                    MessageEmbed oldEmbed = event.getMessage().getEmbeds().get(0);

                    String Author = "el usuario";
                    if(oldEmbed.getAuthor() != null) Author = oldEmbed.getAuthor().getName();
                    String Thumbnail = event.getJDA().getSelfUser().getAvatarUrl();
                    if(oldEmbed.getThumbnail() != null) Thumbnail = oldEmbed.getThumbnail().getUrl();

                    int finalPage = Page+1;

                    EmbedBuilder Embed = new EmbedBuilder()
                            .setColor(config.getEmbedColor())
                            .setAuthor(Author, null, event.getJDA().getSelfUser().getAvatarUrl())
                            .setThumbnail(Thumbnail)
                            .setDescription("Para borrar una infracción, usa el comando `/delinfracción <Usuario> <ID>`\n**"+allInfractions.size()+"** infracciones totales")
                            .setFooter("Página "+finalPage+" de " + allInfractionsSplit.size(), null);

                    for (Document infraction : allInfractionsSplit.get(Page)) {
                        Embed.addField(" - "+infraction.get("Type")+" #"+infraction.get("ID"), "```"+infraction.get("Reason")+"```\n**Fecha**: "+ TimeFormat.DEFAULT.format(infraction.getLong("Date"))+"\n**Duración**: "+infraction.get("Duration")+"\n**Moderador**: `"+infraction.get("Moderator")+"`", true);
                    }

                    event.editMessageEmbeds(Embed.build()).setActionRow(
                            Button.primary("cmd:infracciones:" + UserID + ":"+Page+":back:"+event.getUser().getId(), "◀"),
                            Button.primary("cmd:infracciones:" + UserID + ":"+Page+":next:"+event.getUser().getId(), "▶")
                    ).queue();


                    break;

                case "queue":

                    String Action2 = Args[3];
                    int Page2 = Integer.parseInt(Args[2]);

                    if (!event.getUser().getId().equals(Args[4])) {
                        event.reply(config.getEmojis().Error + "No puedes usar este botón!").setEphemeral(true).queue();
                        return;
                    }

                    net.dv8tion.jda.api.entities.Guild guild = event.getGuild();
                    GuildMusicManager mng = getMusicManager(guild);
                    TrackScheduler scheduler = mng.scheduler;

                    if(scheduler.queue.isEmpty()) {
                        event.reply(config.getEmojis().Error + " No hay canciones en la cola!").setEphemeral(true).queue();
                        return;
                    }

                    Queue<AudioTrack> rawQueue = scheduler.queue;
                    List<AudioTrack> queue = new ArrayList<>(rawQueue);

                    List<List<AudioTrack>> queueSplit = new ArrayList<>();
                    int _i = 0;
                    while (_i < queue.size()) {
                        queueSplit.add(queue.subList(_i, Math.min(_i + 5, queue.size())));
                        _i += 5;
                    }


                    if (Action2.equals("back")) {
                        Page2 -= 1;
                        if(Page2 < 0) {
                            event.reply(config.getEmojis().Error + "No puedes retroceder más!").setEphemeral(true).queue();
                            return;
                        }
                    }

                    if(Action2.equals("next")) {
                        Page2 += 1;
                        if(Page2 > queueSplit.size() - 1) {
                            event.reply(config.getEmojis().Error + "No puedes avanzar más!").setEphemeral(true).queue();
                            return;
                        }
                    }

                    int _finalPage = Page2+1;

                    EmbedBuilder Embed2 = new EmbedBuilder()
                            .setColor(config.getEmbedColor())
                            .setAuthor("Lista de reproducción", null, event.getJDA().getSelfUser().getAvatarUrl())
                            .setFooter("Página "+_finalPage+" de " + queueSplit.size() +" ("+queue.size()+" canciónes totales)", null);


                    for(AudioTrack track : queueSplit.get(Page2)) {
                        Embed2.addField(formatTitle(track.getInfo().title), "**Por**: "+track.getInfo().author + "\n**Duración**: " + getTimestamp(track.getInfo().length), false);
                    }

                    event.editMessageEmbeds(Embed2.build()).setActionRow(
                            Button.primary("cmd:queue:"+Page2+":back:"+event.getUser().getId(), "◀"),
                            Button.primary("cmd:queue:"+Page2+":next:"+event.getUser().getId(), "▶")
                    ).queue();

                    break;

                default: event.reply(config.getEmojis().Error + "Interacción desconocida!").setEphemeral(true).queue();


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

        Document GuildDocument = Database.getGuildDocument(event.getGuild().getId());
        if(GuildDocument == null)
            Database.createGuildDocument(event.getGuild().getId());

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
