package libra.Events;

import libra.Database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class GuildLogs extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        String LogChannelID = Database.getLogChannelIDByGuildID(event.getGuild().getId());
        if (LogChannelID == null) return;

        TextChannel LogChannel = event.getGuild().getTextChannelById(LogChannelID);
        if (LogChannel == null) return;

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(Color.decode("#2BFB8D"))
                .setAuthor("Un usuario se ha unido al servidor", null,  event.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(event.getMember().getUser().getAvatarUrl())
                .addField(event.getMember().getUser().getAsTag(), String.format("```yaml\nID: %s```", event.getMember().getUser().getId()), false);

        LogChannel.sendMessageEmbeds(Embed.build()).queue();

    }

}
