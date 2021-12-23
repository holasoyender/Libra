package libra.Events;

import libra.Config.Config;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Pattern;

public class Messages extends ListenerAdapter {

    private final Config config = new Config();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getMentionedMembers().size() > 0) {
            if (event.getMessage().getMentionedMembers().get(0).getId().equals(event.getJDA().getSelfUser().getId())) {
                event.getChannel().sendMessage("**Hola :wave:**\nSoy `Libra`, un bot para Discord completamente en **Español** de código abierto.\nAquí te dejo unos botones para obtener más información").setActionRow(
                        Button.primary("cmd:bot:Main:" + event.getAuthor().getId(), "Lista de comandos"),
                        Button.link("https://libra.kirobot.cc", "Página web"),
                        Button.link("https://discord.com/api/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands", "Invita a Libra")
                ).queue();
            }
        }
        /* ------ Desarrollador ------ */
        if (!event.getAuthor().getId().equals(config.getOwnerID())) return;

        String prefix = config.getDefaultPrefix();
        String raw = event.getMessage().getContentRaw();

        if (!raw.startsWith(prefix)) return;
        String Command = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split(" ")[0];
        String[] args = raw.replace(prefix + Command + " ", "").split(" ");

        switch (Command) {
            case "eval" -> {
                String toEval = String.join(" ", args);
                if (toEval.length() <= 0) return;
                ScriptEngine Script = new ScriptEngineManager().getEngineByName("groovy");
                Script.put("jda", event.getJDA());
                Script.put("shardManager", event.getJDA().getShardManager());
                Script.put("guild", event.getGuild());
                Script.put("channel", event.getChannel());
                Script.put("msg", event.getMessage());
                try {
                    String result = Script.eval(toEval).toString();
                    event.getMessage().reply(String.format("```java\n%s```", result.replaceAll(event.getJDA().getToken(), "T0K3N"))).mentionRepliedUser(false).queue();
                } catch (ScriptException ex) {

                    event.getMessage().reply(String.format("```java\n%s```", ex.getMessage().replaceAll(event.getJDA().getToken(), "T0K3N"))).mentionRepliedUser(false).queue();
                }
            }
            case "shutdown" -> {
                event.getMessage().reply(config.getEmojis().Success + "El bot se ha apagado.").mentionRepliedUser(false).queue();
                event.getJDA().shutdown();
                System.exit(0);
            }
            case "mem" -> {
                Runtime rt = Runtime.getRuntime();
                double freeMem = rt.freeMemory();
                double totalMem = rt.totalMemory();
                double usedMem = totalMem - freeMem;

                event.getMessage().reply(String.format("```java\nMemoria usada: %s MB\nMemoria total: %s MB\nMemoria libre: %s MB\n\nProcesadores totales: %s Cores```", Math.floor(usedMem / 1000000), Math.floor(totalMem / 1000000), Math.floor(freeMem / 1000000),  Runtime.getRuntime().availableProcessors())).mentionRepliedUser(false).queue();
            }
            default -> {
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        event.getChannel().sendMessage("**Hola :wave:**\nSoy `Libra`, un bot para Discord completamente en **Español** de código abierto.\nAquí te dejo unos botones para obtener más información").setActionRow(
                Button.primary("cmd:bot:Main:" + event.getAuthor().getId(), "Lista de comandos"),
                Button.link("https://libra.kirobot.cc", "Página web"),
                Button.link("https://discord.com/api/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands", "Invita a Libra")
        ).queue();

    }
}
