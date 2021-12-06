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

        if(User.isBot()) AcBadge = "  <:Bot1:913500818345439283><:Bot2:913500818584518706>";
        if(User.getId().equals(context.getGuild().getOwnerId())) AcBadge = "  <:Owner:913501281400815686>";
        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(config.getEmbedColor())
                .addField(User.getAsTag()+AcBadge , String.format("```yaml\nID: %s```",User.getId()),false)
                .addField("Badges", this.badges(User), true)
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
                Embed.addField("<:Invite:913508352108150784>  Unido al servidor", TimeFormat.DEFAULT.format(Member.getTimeJoined())+" ("+TimeFormat.RELATIVE.format(Member.getTimeJoined())+")", false);
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
            Embed.addField("<:Invite:913508352108150784>  Unido al servidor",TimeFormat.DEFAULT.format(Member.getTimeJoined())+" ("+TimeFormat.RELATIVE.format(Member.getTimeJoined())+")", false);
        }

        Embed.addField("<:Time:913509404614230016>  Cuenta creada", TimeFormat.DEFAULT.format(User.getTimeCreated())+" ("+TimeFormat.RELATIVE.format(User.getTimeCreated())+")", false);

        context.replyEmbeds(Embed.build()).queue();

    }

    private String badges(User user) {
        StringBuilder Badges = new StringBuilder();

        for(User.UserFlag flag : user.getFlags()){
            if(Badges.length() > 0)
                Badges.append(" ");

            switch (flag) {
                case STAFF -> Badges.append("<:Staff:913504958912024626>");
                case PARTNER -> Badges.append("<:Partner:913504958362583061>");
                case HYPESQUAD -> Badges.append("<:Hypesquad:913504958727462953>");
                case BUG_HUNTER_LEVEL_1 -> Badges.append("<:Bug1:913504958777811045>");
                case HYPESQUAD_BRAVERY -> Badges.append("<:Bravery:913504958400303185>");
                case HYPESQUAD_BRILLIANCE -> Badges.append("<:Brillance:913504958404522066>");
                case HYPESQUAD_BALANCE -> Badges.append("<:Balance:913504958526148629>");
                case EARLY_SUPPORTER -> Badges.append("<:Early:913504958324813846>");
                case BUG_HUNTER_LEVEL_2 -> Badges.append("<:Bug2:913504958597443614>");
                case VERIFIED_DEVELOPER -> Badges.append("<:Developer:913504958526148630>");
                case CERTIFIED_MODERATOR -> Badges.append("<:Moderator:913504958643601499>");
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