package libra.Commands.Bot;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bson.Document;

public class Invitar implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        context.reply(config.getEmojis().Success+"Puedes añadirme a tu servidor haciendo click en el botón!").mentionRepliedUser(false).addActionRow(
                Button.link("https://discord.com/api/oauth2/authorize?client_id=" + context.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands", "\uD83D\uDCCC  Añádeme!")
        ).queue();

    }

    @Override
    public String getName() {
        return "invitar";
    }

    @Override
    public String getDescription() {
        return "Link para invitarme a tu servidor";
    }

    @Override
    public String getUsage() {
        return "invitar";
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
