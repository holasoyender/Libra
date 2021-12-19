package libra.Commands.Bot;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bson.Document;

public class GitHub implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        context.reply(config.getEmojis().Success+"Aquí tienes el link de mi **repositorio de GitHub**!").mentionRepliedUser(false).addActionRow(
                Button.link("https://github.com/holasoyender/Libra", "\uD83D\uDCBE  GitHub")
        ).queue();

    }

    @Override
    public String getName() {
        return "github";
    }

    @Override
    public String getDescription() {
        return "Link al repositorio de GitHub oficial de Libra";
    }

    @Override
    public String getUsage() {
        return "github";
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
