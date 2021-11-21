package libra.Commands;

import com.mongodb.DBObject;
import libra.Utils.Command;
import libra.Utils.CommandContext;
import net.dv8tion.jda.api.JDA;

public class Ping implements Command {

    @Override
    public void run(CommandContext context, DBObject Guild) {
        JDA jda = context.getJDA();
        context.getChannel().sendMessageFormat("Mi ping es de `%sms`", jda.getGatewayPing()).queue();
        System.out.println(Guild);
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
}
