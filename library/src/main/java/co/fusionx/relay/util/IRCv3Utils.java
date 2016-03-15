package co.fusionx.relay.util;

import android.util.Pair;

import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.internal.base.RelayServer;

public class IRCv3Utils {

    public static Pair<String, UserLevel> consumeNickPrefixes(final RelayServer server,
            final String rawNick) {
        if (server.getCapabilities().contains(CapCapability.MULTIPREFIX)) {
            UserLevel level = UserLevel.NONE;
            for (int i = 0, length = rawNick.length(); i < length; i++) {
                final char c = rawNick.charAt(i);
                final UserLevel charLevel = UserLevel.getLevelFromPrefix(c);
                if (charLevel == UserLevel.NONE) {
                    return new Pair<>(rawNick.substring(i), level);
                } else if (level == UserLevel.NONE) {
                    level = charLevel;
                }
            }
            // This should never happen
            return null;
        }

        final UserLevel level = UserLevel.getLevelFromPrefix(rawNick.charAt(0));
        final String nick = level == UserLevel.NONE ? rawNick : rawNick.substring(1);
        return new Pair<>(nick, level);
    }
}