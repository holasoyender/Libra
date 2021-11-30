package libra.Commands.Bot;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bson.Document;

public class Soporte implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        context.reply(config.getEmojis().Success+"Puedes acceder a mi servidor de soporte haciendo click en el botón!").mentionRepliedUser(false).addActionRow(
                Button.link("https://discord.gg/Rwy8J35", "\uD83D\uDD27  Servidor de soporte")
        ).queue();

    }

    @Override
    public String getName() {
        return "soporte";
    }

    @Override
    public String getDescription() {
        return "Invitación al servidor de soporte del bot";
    }

    @Override
    public String getUsage() {
        return "soporte";
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
