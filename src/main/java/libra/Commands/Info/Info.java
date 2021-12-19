package libra.Commands.Info;

import libra.Config.Config;
import libra.Utils.Command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.bson.Document;

import java.time.Instant;

public class Info implements Command {

    @Override
    public void run(SlashCommandEvent context, Document Guild,  Config config) {
        if(context.getGuild() == null) return;

        OptionMapping UserOption = context.getOption("usuario");
        Member Member = null;
        User User;
        StringBuilder Roles = new StringBuilder();

        if (UserOption == null) {
            User = context.getUser();
            Member = context.getMember();
        } else {
            User = UserOption.getAsUser();
        }

        String URL = User.getAvatarUrl() + "?size=512";
        if (User.getAvatarUrl() == null) URL = "https://cdn.discordapp.com/embed/avatars/0.png";

        String AcBadge = "";

        if(User.isBot()) AcBadge = config.getEmojis().BotBadge;
        if(User.getId().equals(context.getGuild().getOwnerId())) AcBadge = config.getEmojis().OwnerBadge;
        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .addField(User.getAsTag()+AcBadge , String.format("```yaml\nID: %s```",User.getId()),false)
                .addField("Badges", this.badges(User, config), true)
                .addField("Avatar", "[URL del Avatar]("+URL+")", true)
                .addField("Rol más alto", Member != null ? Member.getRoles().get(0).getAsMention() : "Sin roles", true)
                .setImage(User.retrieveProfile().complete().getBannerUrl() == null ? null : User.retrieveProfile().complete().getBannerUrl()+"?size=512")
                .setFooter("Pedido por "+context.getUser().getAsTag(), context.getUser().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setThumbnail(URL);

        if(Member == null) {
            Member = context.getGuild().getMember(User);

            if(Member == null) {
                Embed.addField("Todos los Roles", "`El usuario no está en el servidor`", false);

            }else {

                for(Role role : Member.getRoles()) {
                    Roles.append(role.getName()).append("\n");
                }

                if (Roles.length() <= 0) {
                    Embed.addField("Todos los Roles", "`El usuario no tiene roles`", false);
                    return;
                }
                Embed.addField("Todos los Roles", "```\n"+Roles+"```",false);
                Embed.addField(config.getEmojis().Invite+"Unido al servidor", TimeFormat.DEFAULT.format(Member.getTimeJoined())+" ("+TimeFormat.RELATIVE.format(Member.getTimeJoined())+")", false);
            }
        }else {
            for(Role role : Member.getRoles()) {
                Roles.append(role.getName()).append("\n");
            }
            if (Roles.length() <= 0) {
                Embed.addField("Todos los Roles", "`El usuario no tiene roles`", false);
                return;
            }

            Embed.addField("Todos los Roles", "```\n"+Roles+"```",false);
            Embed.addField(config.getEmojis().Invite+"Unido al servidor",TimeFormat.DEFAULT.format(Member.getTimeJoined())+" ("+TimeFormat.RELATIVE.format(Member.getTimeJoined())+")", false);
        }

        Embed.addField(config.getEmojis().Time+"Cuenta creada", TimeFormat.DEFAULT.format(User.getTimeCreated())+" ("+TimeFormat.RELATIVE.format(User.getTimeCreated())+")", false);

        context.replyEmbeds(Embed.build()).queue();

    }

    private String badges(User user, Config config) {
        StringBuilder Badges = new StringBuilder();

        for(User.UserFlag flag : user.getFlags()){
            if(Badges.length() > 0)
                Badges.append(" ");

            switch (flag) {
                case STAFF -> Badges.append(config.getEmojis().STAFF);
                case PARTNER -> Badges.append(config.getEmojis().PARTNER);
                case HYPESQUAD -> Badges.append(config.getEmojis().HYPESQUAD);
                case BUG_HUNTER_LEVEL_1 -> Badges.append(config.getEmojis().BUG_HUNTER_LEVEL_1);
                case HYPESQUAD_BRAVERY -> Badges.append(config.getEmojis().HYPESQUAD_BRAVERY);
                case HYPESQUAD_BRILLIANCE -> Badges.append(config.getEmojis().HYPESQUAD_BRILLIANCE);
                case HYPESQUAD_BALANCE -> Badges.append(config.getEmojis().HYPESQUAD_BALANCE);
                case EARLY_SUPPORTER -> Badges.append(config.getEmojis().EARLY_SUPPORTER);
                case BUG_HUNTER_LEVEL_2 -> Badges.append(config.getEmojis().BUG_HUNTER_LEVEL_2);
                case VERIFIED_DEVELOPER -> Badges.append(config.getEmojis().VERIFIED_DEVELOPER);
                case CERTIFIED_MODERATOR -> Badges.append(config.getEmojis().CERTIFIED_MODERATOR);
            }
        }

        return Badges.length() == 0 ? "Sin badges" : Badges.toString();
    }

    @Override
    public String getDescription() {
        return "Muestra información sobre un usuario";
    }

    @Override
    public String getUsage() {
        return "info [Usuario]";
    }

    @Override
    public String getPermissions() {
        return "Todo el mundo";
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getCategory() {
        return "Información";
    }

    @Override
    public CommandData getSlashData() {
        return new CommandData(this.getName(), this.getDescription()).addOption(OptionType.USER, "usuario", "Usuario a sacar información", false);
    }

}