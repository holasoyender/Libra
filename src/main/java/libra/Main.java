package libra;

import io.sentry.Sentry;
import libra.Config.Config;
import libra.Events.*;
import libra.Lavaplayer.Player;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import javax.security.auth.login.LoginException;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Main {

    public static void main(String[] args) {

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(null);
        Config Config = new Config();

        builder.disableCache(
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.ACTIVITY,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOTE
        );
        builder.enableCache(
                CacheFlag.VOICE_STATE
        );
        builder.setBulkDeleteSplittingEnabled(true);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.watching(Config.getStatus()));

        builder.setToken(Config.getToken());
        builder.addEventListeners(
                new Internal(),
                new GuildLogs(),
                new Messages(),
                new Interactions(),
                new Voice());

        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.disableIntents(
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_INVITES
        );
        builder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES
        );
        builder.setLargeThreshold(50);

        builder.setShardsTotal(-1);

        try {
            builder.build();
            new Player();
            if(Config.getSentry() != null) {
                Sentry.init(options -> {
                    options.setDsn(Config.getSentry());
                    options.setTracesSampleRate(1.0);
                    options.setDebug(false);
                });
            }
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
