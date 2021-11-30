package libra.Commands.Ocio;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.bson.Document;

public class Juntos implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        if(context.getMember() == null) return;

        if(!context.getMember().getVoiceState().inVoiceChannel()){
            context.reply(config.getEmojis().Error+ "Debes estar en un canal de voz para usar este comando.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder Embed = new EmbedBuilder()
                .setAuthor("Discord Together", null, context.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("Haz click en el menu de interacción para ejecutar una aplicación de **Discord Together**!")
                .setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
                .setColor(config.getEmbedColor());

        context.replyEmbeds(Embed.build()).addActionRow(
                SelectionMenu.create("cmd:together")
                        .setPlaceholder("Elija la aplicación")
                        .addOption("YouTube", "cmd:together:YouTube:"+context.getUser().getId(), "Haz click para lanzar la aplicación YouTube Together", Emoji.fromUnicode("\uD83C\uDFA5"))
                        .addOption("Poker", "cmd:together:Poker:"+context.getUser().getId(), "Haz click para lanzar la aplicación Poker Night", Emoji.fromUnicode("\uD83C\uDFB1"))
                        .addOption("Betrayal", "cmd:together:Betrayal:"+context.getUser().getId(), "Haz click para lanzar la aplicación Betrayal.io", Emoji.fromUnicode("\uD83D\uDD2B"))
                        .addOption("Fishing", "cmd:together:Fishing:"+context.getUser().getId(), "Haz click para lanzar la aplicación Fishington.io", Emoji.fromUnicode("\uD83C\uDFA3"))
                        .addOption("Chess", "cmd:together:Chess:"+context.getUser().getId(), "Haz click para lanzar la aplicación Chess in the Park", Emoji.fromUnicode("♟"))
                        .addOption("Letter Tile", "cmd:together:LetterTile:"+context.getUser().getId(), "Haz click para lanzar la aplicación Letter Tile", Emoji.fromUnicode("\uD83D\uDCDC"))
                        .addOption("Word Snack", "cmd:together:WordSnack:"+context.getUser().getId(), "Haz click para lanzar la aplicación Word Snack", Emoji.fromUnicode("\uD83C\uDF3D"))
                        .addOption("Doodle Crew", "cmd:together:DoodleCrew:"+context.getUser().getId(), "Haz click para lanzar la aplicación Doodle Crew", Emoji.fromUnicode("\uD83D\uDD8A️"))
                        .build()
        ).queue();
    }

    @Override
    public String getName() {
        return "juntos";
    }

    @Override
    public String getDescription() {
        return "Lanzar el menú de Discord Together";
    }

    @Override
    public String getUsage() {
        return "juntos";
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
        return null;
    }
}
