package libra.Commands.Config;

import libra.Config.Config;
import libra.Database.Database;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class SetLogs implements Command {
    @Override
    public void run(SlashCommandEvent context, Document Guild, Config config) {
        if (context.getMember() == null || context.getGuild() == null) return;

        if (!context.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            context.reply(config.getEmojis().Error + "No tienes permisos para ejecutar este comando").setEphemeral(true).queue();
            return;
        }

        OptionMapping Enabled = context.getOption("habilitar");
        OptionMapping Channel = context.getOption("canal");

        if (Enabled == null) {
            context.reply(config.getEmojis().Error + "Debes especificar si quieres habilitar o deshabilitar los logs del servidor").setEphemeral(true).queue();
            return;
        }

        Boolean isEnabled = Enabled.getAsBoolean();

        if (Channel == null) {

            if (isEnabled) {
                context.reply(config.getEmojis().Error + "Debes especificar un canal para enviar los logs del servidor si quieres que estén habilitados!").setEphemeral(true).queue();
            }else {

                Document GuildDocument = Database.getGuildDocument(context.getGuild().getId());

                if (GuildDocument == null) {

                    Database.getDatabase().getCollection("Guilds").insertOne(
                            new Document("GuildID", context.getGuild().getId())
                                    .append("Logs", new Document("Enabled", isEnabled).append("ChannelID", "")));

                    context.reply(config.getEmojis().Success + "Los logs del servidor han sido deshabilitados!").setEphemeral(false).queue();

                } else {

                    Database.getDatabase().getCollection("Guilds").updateOne(eq("GuildID", context.getGuild().getId()),
                            new Document("$set", new Document("Logs", new Document("Enabled", isEnabled).append("ChannelID", ""))));

                    context.reply(config.getEmojis().Success + "Los logs del servidor han sido deshabilitados!").setEphemeral(false).queue();

                }
            }
        } else {

            Document GuildDocument = Database.getGuildDocument(context.getGuild().getId());
            GuildChannel LogChannel = Channel.getAsGuildChannel();

            if (GuildDocument == null) {


                Database.getDatabase().getCollection("Guilds").insertOne(
                        new Document("GuildID", context.getGuild().getId())
                                .append("Logs", new Document("Enabled", isEnabled).append("ChannelID", LogChannel.getId())));

                context.reply(config.getEmojis().Success + "Los logs del servidor han sido habilitados en el canal "+LogChannel.getAsMention()+"!").setEphemeral(false).queue();

            } else {

                Database.getDatabase().getCollection("Guilds")
                        .updateOne(eq("GuildID", context.getGuild().getId()),
                                new Document("$set", new Document("Logs", new Document("Enabled", isEnabled).append("ChannelID", LogChannel.getId()))));

                context.reply(config.getEmojis().Success + "Los logs del servidor han sido habilitados en el canal "+LogChannel.getAsMention()+"!").setEphemeral(false).queue();

            }
        }

    }

    @Override
    public String getName() {
        return "setlogs";
    }

    @Override
    public String getDescription() {
        return "Establece el canal de logs del servidor";
    }

    @Override
    public String getUsage() {
        return "setlogs <Canal>";
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
        return new CommandData(this.getName(), this.getDescription())
                .addOptions(new OptionData(OptionType.BOOLEAN, "habilitar", "Habilitar o deshabilitar los logs del servidor", true),
                        new OptionData(OptionType.CHANNEL, "canal", "Canal para enviar los logs del servidor", false));

    }
}