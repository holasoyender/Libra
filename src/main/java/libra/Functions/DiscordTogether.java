package libra.Functions;

import libra.Config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Random;

public class DiscordTogether {

    private final static Config Config = new Config();

    public DiscordTogether(SelectionMenuEvent event, String activity) {
        handleDiscordTogether(event, activity);
    }

    public static void handleDiscordTogether(SelectionMenuEvent event, String activity) {

        if(!event.getMember().getVoiceState().inVoiceChannel() || event.getMember().getVoiceState().getChannel() == null) {
            event.reply(Config.getEmojis().Error+"  Tienes que estar en un canal de voz para poder usar este menú!").setEphemeral(true).queue();
            return;
        }


        String[] emojis = {"⛳", "\uD83E\uDE81", "\uD83D\uDEF9", "\uD83C\uDFF9", "\uD83C\uDFD3", "\uD83C\uDFD1"};
        int random = new Random().nextInt(emojis.length);
        String Invite;

        EmbedBuilder Embed = new EmbedBuilder()
                .setColor(Config.getEmbedColor())
                .setAuthor("Discord Together", null, event.getJDA().getSelfUser().getAvatarUrl());


        switch (activity) {

            case "YouTube" -> {
                Embed.setDescription("Ha seleccionado la actividad **YouTube Together**");

                try {
                    Invite = event.getMember().getVoiceState().getChannel().createInvite().setTargetApplication("755600276941176913").setUnique(true).complete().getCode();
                }catch (Exception e) {
                    event.reply(Config.getEmojis().Error+"  No tengo permisos para crear invitaciones en este canal!").setEphemeral(true).queue();
                    return;
                }

                Button Btn = Button.link("https://discord.gg/"+Invite, emojis[random]+"  Ir a la actividad");
                event.editMessageEmbeds(Embed.build()).setActionRow(Btn).queue();
            }
            case "Poker" -> {
                Embed.setDescription("Ha seleccionado la actividad **Poker Night**");

                try {
                    Invite = event.getMember().getVoiceState().getChannel().createInvite().setTargetApplication("755827207812677713").setUnique(true).complete().getCode();
                }catch (Exception e) {
                    event.reply(Config.getEmojis().Error+"  No tengo permisos para crear invitaciones en este canal!").setEphemeral(true).queue();
                    return;
                }

                Button Btn = Button.link("https://discord.gg/"+Invite, emojis[random]+"  Ir a la actividad");
                event.editMessageEmbeds(Embed.build()).setActionRow(Btn).queue();
            }
            case "Betrayal" -> {
                Embed.setDescription("Ha seleccionado la actividad **Betrayal.io**");

                try {
                    Invite = event.getMember().getVoiceState().getChannel().createInvite().setTargetApplication("773336526917861400").setUnique(true).complete().getCode();
                }catch (Exception e) {
                    event.reply(Config.getEmojis().Error+"  No tengo permisos para crear invitaciones en este canal!").setEphemeral(true).queue();
                    return;
                }

                Button Btn = Button.link("https://discord.gg/"+Invite, emojis[random]+"  Ir a la actividad");
                event.editMessageEmbeds(Embed.build()).setActionRow(Btn).queue();
            }
            case "Fishing" -> {
                Embed.setDescription("Ha seleccionado la actividad **Fishing.io**");

                try {
                    Invite = event.getMember().getVoiceState().getChannel().createInvite().setTargetApplication("814288819477020702").setUnique(true).complete().getCode();
                }catch (Exception e) {
                    event.reply(Config.getEmojis().Error+"  No tengo permisos para crear invitaciones en este canal!").setEphemeral(true).queue();
                    return;
                }

                Button Btn = Button.link("https://discord.gg/"+Invite, emojis[random]+"  Ir a la actividad");
                event.editMessageEmbeds(Embed.build()).setActionRow(Btn).queue();
            }
            case "Chess" -> {
                Embed.setDescription("Ha seleccionado la actividad **Chess in the Park**");

                try {
                    Invite = event.getMember().getVoiceState().getChannel().createInvite().setTargetApplication("832012774040141894").setUnique(true).complete().getCode();
                }catch (Exception e) {
                    event.reply(Config.getEmojis().Error+"  No tengo permisos para crear invitaciones en este canal!").setEphemeral(true).queue();
                    return;
                }

                Button Btn = Button.link("https://discord.gg/"+Invite, emojis[random]+"  Ir a la actividad");
                event.editMessageEmbeds(Embed.build()).setActionRow(Btn).queue();
            }
            case "LetterTile" -> {
                Embed.setDescription("Ha seleccionado la actividad **Letter Tile**");

                try {
                    Invite = event.getMember().getVoiceState().getChannel().createInvite().setTargetApplication("879863686565621790").setUnique(true).complete().getCode();
                }catch (Exception e) {
                    event.reply(Config.getEmojis().Error+"  No tengo permisos para crear invitaciones en este canal!").setEphemeral(true).queue();
                    return;
                }

                Button Btn = Button.link("https://discord.gg/"+Invite, emojis[random]+"  Ir a la actividad");
                event.editMessageEmbeds(Embed.build()).setActionRow(Btn).queue();
            }
            case "WordSnack" -> {
                Embed.setDescription("Ha seleccionado la actividad **Word Snack**");

                try {
                    Invite = event.getMember().getVoiceState().getChannel().createInvite().setTargetApplication("879863976006127627").setUnique(true).complete().getCode();
                }catch (Exception e) {
                    event.reply(Config.getEmojis().Error+"  No tengo permisos para crear invitaciones en este canal!").setEphemeral(true).queue();
                    return;
                }

                Button Btn = Button.link("https://discord.gg/"+Invite, emojis[random]+"  Ir a la actividad");
                event.editMessageEmbeds(Embed.build()).setActionRow(Btn).queue();
            }
            case "DoodleCrew" -> {
                Embed.setDescription("Ha seleccionado la actividad **Doodle Crew**");

                try {
                    Invite = event.getMember().getVoiceState().getChannel().createInvite().setTargetApplication("878067389634314250").setUnique(true).complete().getCode();
                }catch (Exception e) {
                    event.reply(Config.getEmojis().Error+"  No tengo permisos para crear invitaciones en este canal!").setEphemeral(true).queue();
                    return;
                }

                Button Btn = Button.link("https://discord.gg/"+Invite, emojis[random]+"  Ir a la actividad");
                event.editMessageEmbeds(Embed.build()).setActionRow(Btn).queue();
            }
            default -> {
                Embed.setDescription("No he podido encontrar la actividad \"" + activity+"\"");
                event.editMessageEmbeds(Embed.build()).setActionRow().queue();
            }
        }
    }
}
