package libra.Commands.Ocio;

import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

public class Tirar implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        OptionMapping SayRaw = context.getOption("numero");

        if(SayRaw == null) {
            context.reply("Debes especificar un número").queue();
            return;
        }

        double numero = SayRaw.getAsDouble();
        int tirada = (int) (Math.random() * numero) + 1;

        context.reply("Ha salido " + tirada+ "!").queue();

    }

    @Override
    public String getName() {
        return "tirar";
    }

    @Override
    public String getDescription() {
        return "Escoge un número aleatorio entre 1 y el numero dado";
    }

    @Override
    public String getUsage() {
        return "tirar <Número>";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getCategory() {
        return "Ocio";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.NUMBER, "numero", "Número máximo a elegir", true));
    }
}
