package libra.Commands.Bot;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import java.lang.management.ManagementFactory;

import static me.duncte123.botcommons.StringUtils.replaceLast;

public class Uptime implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        final long duration = ManagementFactory.getRuntimeMXBean().getUptime();

        final long years = duration / 31104000000L;
        final long months = duration / 2592000000L % 12;
        final long days = duration / 86400000L % 30;
        final long hours = duration / 3600000L % 24;
        final long minutes = duration / 60000L % 60;
        final long seconds = duration / 1000L % 60;

        String uptime = (years == 0 ? "" : "**" + years + "** Years, ") + (months == 0 ? "" : "**" + months + "** Meses, ") + (days == 0 ? "" : "**" + days + "** Días, ") + (hours == 0 ? "" : "**" + hours + "** Horas, ")
                + (minutes == 0 ? "" : "**" + minutes + "** Minutos y ") + (seconds == 0 ? "" : "**" + seconds + "** Segundos! ");

        context.reply(config.getEmojis().Time+"Llevo online " + uptime).queue();
    }

    @Override
    public String getName() {
        return "uptime";
    }

    @Override
    public String getDescription() {
        return "Ver cuanto tiempo llevo online";
    }

    @Override
    public String getUsage() {
        return "uptime";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Bot";
    }

    @Override
    public CommandData getSlashData() {
        return null;
    }
}
