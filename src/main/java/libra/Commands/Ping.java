package libra.Commands;

import com.mongodb.DBObject;
import libra.Utils.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Ping implements Command {

    @Override
    public void run(SlashCommandEvent context, DBObject Guild) {
        JDA jda = context.getJDA();
        context.replyFormat("Mi ping es de `%sms`", jda.getGatewayPing()).queue();
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
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription());
    }
}
