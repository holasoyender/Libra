package libra.Events;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import libra.Config.Config;
import libra.Database.Database;
import libra.Functions.Recordatorios;
import libra.Utils.Command.Command;
import libra.Utils.Command.CommandManager;
import libra.Utils.Logger;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class Internal extends ListenerAdapter {

    private final Logger Logger = new Logger().getLogger();
    private final Config config = new Config();

    private WebhookClient internalLogWebhook = null;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.EventLogger.info("Cliente iniciado como {}", event.getJDA().getSelfUser().getAsTag());

        Recordatorios.start(event.getJDA());

        Guild Guild = event.getJDA().getGuildById("704029755975925841");
        if (Guild == null) {
            java.lang.System.out.println("No existe el servidor de tests");
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
                .setColor(0x56F772)
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
