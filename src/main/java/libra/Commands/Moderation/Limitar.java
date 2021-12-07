package libra.Commands.Moderation;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

public class Limitar implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getMember() == null || context.getGuild() == null) return;

        if (!context.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping TimeOption = context.getOption("tiempo");
        OptionMapping ChannelOption = context.getOption("canal");

        if (TimeOption == null) {
            context.reply(config.getEmojis().Error + "Debes especificar un tiempo valido!").setEphemeral(true).queue();
            return;
        }

        String rawTime = TimeOption.getAsString();
        GuildChannel Channel;

        if(ChannelOption == null) {
            Channel = context.getGuildChannel();
        }else {
            Channel = ChannelOption.getAsGuildChannel();
        }

        long Time = libra.Utils.Time.ms(rawTime)/1000;
        if(Time == 0) {
            rawTime = "0s";
        }
        if(rawTime.equals("off") || rawTime.equals("apagado") || rawTime.equals("no") || rawTime.equals("nada")) {
            rawTime = "0s";
        }

        if(Time > 21600) {
            context.reply(config.getEmojis().Error+"El tiempo no puede ser mayor a 6 horas").setEphemeral(true).queue();
            return;
        }

        if (Channel.getType() != ChannelType.TEXT) {
            context.reply(config.getEmojis().Error + "El canal de logs debe ser un canal de texto!").setEphemeral(true).queue();
            return;
        }

        try {
            Channel.getManager().setSlowmode(Math.toIntExact(Time)).queue();
            context.reply(config.getEmojis().Success + "El canal ha sido limitado a **" + rawTime + "**").setEphemeral(false).queue();
        } catch (Exception e) {
            context.reply(config.getEmojis().Error + "No he podido limitar el canal!").setEphemeral(true).queue();
            e.printStackTrace();
        }

    }

    @Override
    public String getName() {
        return "limitar";
    }

    @Override
    public String getDescription() {
        return "Limitar el intervalo de tiempo entre mensajes de un canal";
    }

    @Override
    public String getUsage() {
        return "limitar <Tiempo> <Canal>";
    }

    @Override
    public String getPermissions() {
        return "Gestionar canales";
    }

    @Override
    public String getCategory() {
        return "Moderación";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.STRING, "tiempo", "Tiempo entre mensajes", true),
                new OptionData(OptionType.CHANNEL, "canal", "Canal a limitar", false));
    }
}
