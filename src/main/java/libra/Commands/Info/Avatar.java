package libra.Commands.Info;

import libra.Config.Config;
import libra.Utils.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

public class Avatar implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild,  Config config) {
        OptionMapping User = context.getOption("usuario");
        if(User == null) {
            String URL = context.getUser().getAvatarUrl()+ "?size=512";
            if(context.getUser().getAvatarUrl() == null) URL = "https://cdn.discordapp.com/embed/avatars/0.png";
            EmbedBuilder Embed = new EmbedBuilder().setImage(URL).setColor(config.EmbedColor);
            context.reply(config.Emojis.Success+"Aquí tienes tu avatar "+context.getUser().getAsMention()).addEmbeds(Embed.build()).queue();
        }else {
            String URL = User.getAsUser().getAvatarUrl()+ "?size=512";
            if(User.getAsUser().getAvatarUrl() == null) URL = "https://cdn.discordapp.com/embed/avatars/0.png";
            EmbedBuilder Embed = new EmbedBuilder().setImage(URL).setColor(config.EmbedColor);
            context.reply(config.Emojis.Success+"Aquí tienes el avatar de **"+User.getAsUser().getAsTag()+"**").addEmbeds(Embed.build()).queue();
        }
    }

    @Override
    public String getDescription() { return "Devuelve tu avatar o el de un usuario"; }
    @Override
    public String getUsage() { return "avatar [Usuario]"; }
    @Override
    public String getPermissions() { return "Todo el mundo"; }
    @Override
    public String getName() {
        return "avatar";
    }

    @Override
    public String getCategory() {
        return "Información";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription()).addOption(OptionType.USER, "usuario", "Usuario para mostrar su avatar" ,false);
    }

}
