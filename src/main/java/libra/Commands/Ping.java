package libra.Commands;

import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

public class Ping implements Command {

    private final Config config = new Config().getConfig();

    @Override
    public void run(SlashCommandEvent context, Document Guild) {
        JDA jda = context.getJDA();
        context.replyFormat(config.Emojis.Ping+"Mi ping es de `%sms`", jda.getGatewayPing()).queue();
    }

    @Override
    public String getDescription() { return "Muestra el ping del bot"; }
    @Override
    public String getUsage() { return "ping"; }
    @Override
    public String getPermissions() { return "Todo el mundo"; }
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getCategory() {
        return "Bot";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription());
    }
}
