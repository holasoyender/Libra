package libra.Commands.Config;

import libra.Config.Config;
import libra.Database.Database;
import libra.Utils.Command.Command;
import libra.Utils.Command.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;

public class Deshabilitados implements Command {

    private final CommandManager manager;

    public Deshabilitados(CommandManager manager) {
        this.manager = manager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {

        if (context.getGuild() == null || context.getMember() == null) return;

        if (!context.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .setAuthor("Comandos deshabilitados", null, context.getJDA().getSelfUser().getAvatarUrl())
                .setFooter("Pedido por "+context.getUser().getAsTag(), context.getUser().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setThumbnail(context.getJDA().getSelfUser().getAvatarUrl());


        Document GuildDocument = Database.getGuildDocument(context.getGuild().getId());

        if(GuildDocument == null) {
            context.reply(config.getEmojis().Error+"Parece que tu servidor no está en nuestra base de datos!\nAcciona el comando otra vez, y si el error persiste, contacta con el soporte.").setEphemeral(true).queue();
            Database.createGuildDocument(context.getGuild().getId());
            return;
        }

        ArrayList<String> disabledCommands = (ArrayList<String>) GuildDocument.get("DisabledCommands");

        if(disabledCommands.size() == 0) {
            context.reply(config.getEmojis().Error+"No hay comandos deshabilitados en este servidor!").setEphemeral(true).queue();
            return;
        }

        for(String _command : disabledCommands) {

            Command command = manager.getCommand(_command);
            if(command == null) continue;

            Embed.addField("`"+command.getName()+"`", command.getDescription(), true);
        }

        context.replyEmbeds(Embed.build()).queue();
    }

    @Override
    public String getName() {
        return "deshabilitados";
    }

    @Override
    public String getDescription() {
        return "Lista de todos los comandos deshabilitados";
    }

    @Override
    public String getUsage() {
        return "deshabilitados";
    }

    @Override
    public String getPermissions() {
        return "Gestionar el servidor";
    }

    @Override
    public String getCategory() {
        return "Configuración";
    }

    @Override
    public CommandData getSlashData() {
        return null;
    }
}
