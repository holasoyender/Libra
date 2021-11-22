package libra;

import io.sentry.Sentry;
import libra.Events.Listener;
import libra.Config.Config;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import javax.security.auth.login.LoginException;

public class Main {

    public static void main(String[] args) {

        JDABuilder builder = JDABuilder.createDefault(null);
        Config config = new Config().getConfig();

        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.watching("the world"));

        builder.setToken(config.Token);
        builder.addEventListeners(new Listener());

        builder.disableCache(CacheFlag.ACTIVITY);
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_PRESENCES);
        builder.setLargeThreshold(50);

        try {
            builder.build();
            Sentry.init(options -> {
                options.setDsn(config.SentryDNS);
                options.setTracesSampleRate(1.0);
                options.setDebug(false);
            });
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
