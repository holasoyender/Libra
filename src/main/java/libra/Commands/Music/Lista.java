package libra.Commands.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import libra.Config.Config;
import libra.Lavaplayer.GuildMusicManager;
import libra.Lavaplayer.TrackScheduler;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static libra.Lavaplayer.Player.*;

public class Lista implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if(context.getGuild() == null) return;

        net.dv8tion.jda.api.entities.Guild guild = context.getGuild();
        GuildMusicManager mng = getMusicManager(guild);
        TrackScheduler scheduler = mng.scheduler;

        if(scheduler.queue.isEmpty()) {
            context.reply(config.getEmojis().Error + " No hay canciones en la cola!").setEphemeral(true).queue();
            return;
        }

        Queue<AudioTrack> rawQueue = scheduler.queue;
        List<AudioTrack> queue = new ArrayList<>(rawQueue);

        List<List<AudioTrack>> queueSplit = new ArrayList<>();
        int i = 0;
        while (i < queue.size()) {
            queueSplit.add(queue.subList(i, Math.min(i + 5, queue.size())));
            i += 5;
        }

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .setAuthor("Lista de reproducción", null, context.getJDA().getSelfUser().getAvatarUrl())
                .setFooter("Página 1 de " + queueSplit.size()+" ("+queue.size()+" canciónes totales)", null);

        for(AudioTrack track : queueSplit.get(0)) {
            Embed.addField(formatTitle(track.getInfo().title), "**Por**: "+track.getInfo().author + "\n**Duración**: " + getTimestamp(track.getInfo().length), false);
        }

        context.replyEmbeds(Embed.build()).setEphemeral(false).addActionRow(
                Button.primary("cmd:queue:0:back:"+context.getUser().getId(), "◀"),
                Button.primary("cmd:queue:0:next:"+context.getUser().getId(), "▶")
        ).queue();

    }

    @Override
    public String getName() {
        return "lista";
    }

    @Override
    public String getDescription() {
        return "Muestra la lista de reproducción";
    }

    @Override
    public String getUsage() {
        return "lista";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Música";
    }

    @Override
    public CommandData getSlashData() {
        return null;
    }
}
