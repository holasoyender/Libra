package libra.Commands.Bot;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

public class Say implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild,  Config config) {

        OptionMapping SayRaw = context.getOption("texto");

        if (SayRaw == null) {
            context.reply(config.getEmojis().Error+"No puedes hacer que diga un mensaje vacío!").setEphemeral(true).queue();
            return;
        }
        String Say = SayRaw.getAsString();

        if (Say.length() <= 0) {
            context.reply(config.getEmojis().Error+"No puedes hacer que diga un mensaje vacío!").setEphemeral(true).queue();
            return;
        }

        try {
            context.getChannel().sendMessage(Say).queue();
            context.reply(config.getEmojis().Success+"He dicho `"+Say+"`").setEphemeral(true).queue();
        } catch (Exception e) {
            context.reply(config.getEmojis().Error+"Ha ocurrido un error al intentar mandar ese mensaje!").queue();
        }

    }

    @Override
    public String getName() {
        return "say";
    }

    @Override
    public String getDescription() {
        return "Hacer que el bot diga algo";
    }

    @Override
    public String getUsage() {
        return "say <Texto>";
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
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.STRING, "texto", "Texto para que mande el bot", true));
    }
}
