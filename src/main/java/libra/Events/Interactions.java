package libra.Events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import libra.Database.Database;
import libra.Functions.DiscordTogether;
import libra.Lavaplayer.GuildMusicManager;
import libra.Lavaplayer.TrackScheduler;
import libra.Utils.Command.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static libra.Lavaplayer.Player.*;

public class Interactions extends ListenerAdapter {

    private final CommandManager manager = new CommandManager();

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
                                        .addOption("Moderación", "cmd:help:Moderación:"+event.getUser().getId(), "Lista de comandos de la sección de Moderación", Emoji.fromUnicode("\uD83D\uDDE1️"))
                                        .addOption("Música", "cmd:help:Música:" + event.getUser().getId(), "Lista de comandos de la sección de Música", Emoji.fromUnicode("\uD83C\uDFB5"))
                                        .addOption("Ocio", "cmd:help:Ocio:" + event.getUser().getId(), "Lista de comandos de la sección de Ocio", Emoji.fromUnicode("\uD83D\uDEF9"))
                                        .addOption("Configuración", "cmd:help:Configuración:"+event.getUser().getId(), "Lista de comandos de la sección de Configuración", Emoji.fromUnicode("⚙️"))
                                        .build()
                        ).queue();

                    }

                    break;

                case "infracciones":
                    if(event.getGuild() == null) return;

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
                    if(event.getGuild() == null) return;

                    String Action2 = Args[3];
                    int Page2 = Integer.parseInt(Args[2]);

                    if (!event.getUser().getId().equals(Args[4])) {
                        event.reply(config.getEmojis().Error + "No puedes usar este botón!").setEphemeral(true).queue();
                        return;
                    }

                    net.dv8tion.jda.api.entities.Guild guild = event.getGuild();
                    GuildMusicManager mng = getMusicManager(guild, null);
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

}
