package co.fusionx.relay.parser.connection.cap;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

import android.util.Pair;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.constants.CapCommand;
import co.fusionx.relay.constants.ServerReplyCodes;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.sender.relay.RelayCapSender;
import co.fusionx.relay.util.ParseUtils;

import static co.fusionx.relay.constants.CapCapability.SASL;

public class CapParser {

    private final Map<CapCommand, Consumer<List<String>>> mCapCommandMap;

    private final RelayServer mServer;

    private final ServerConfiguration mServerConfiguration;

    private final RelayCapSender mCapSender;

    public CapParser(final RelayServer server, final ServerConfiguration serverConfiguration) {
        mServer = server;
        mServerConfiguration = serverConfiguration;

        mCapSender = new RelayCapSender(mServer.getRelayPacketSender());
        mCapCommandMap = new EnumMap<>(CapCommand.class);
        initalizeCommandMap(mCapCommandMap);
    }

    public static Set<ModifiedCapability> parseCapabilities(final String caps) {
        final Set<ModifiedCapability> capabilitySet = new HashSet<>();
        final List<String> capabilities = ParseUtils.splitRawLine(caps, false);

        for (final String capability : capabilities) {
            final Pair<String, Modifier> pair = Modifier.consumeModifier(capability);
            final CapCapability capCapability = CapCapability.getCapabilityFromString(pair.first);
            if (capCapability == null) {
                continue;
            }
            capabilitySet.add(new ModifiedCapability(pair.second, capCapability));
        }

        return capabilitySet;
    }

    public static ModifiedCapability collectionContainsCapability(
            final Collection<ModifiedCapability> capabilities, final CapCapability capability) {
        for (final ModifiedCapability modifiedCapability : capabilities) {
            if (modifiedCapability.getCapability() == capability) {
                return modifiedCapability;
            }
        }
        return null;
    }

    public void parseCode(final int code, final List<String> parsedArray) {
        switch (code) {
            case ServerReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(2);
                mServer.postAndStoreEvent(new GenericServerEvent(mServer, loginMessage));
                break;
            case ServerReplyCodes.RPL_SASL_SUCCESSFUL:
                final String successful = parsedArray.get(0);
                mServer.postAndStoreEvent(new GenericServerEvent(mServer, successful));
                break;
            case ServerReplyCodes.ERR_SASL_FAIL:
            case ServerReplyCodes.ERR_SASL_TOO_LONG:
                final String error = parsedArray.get(0);
                mServer.postAndStoreEvent(new GenericServerEvent(mServer, error));
                break;
        }
        mCapSender.sendEnd();
    }

    public void parseCAP(final List<String> parsedArray) {
        parsedArray.remove(0); // Remove the target (ourselves)

        // Remove the CAP subcommand
        final String subCommandString = parsedArray.remove(0);
        final CapCommand subCommand = CapCommand.getCommandFromString(subCommandString);

        // Get the parsed array
        final Consumer<List<String>> parser = mCapCommandMap.get(subCommand);
        parser.apply(parsedArray);
    }

    public void parseAuthenticate(final List<String> parsedArray) {
        final String argument = parsedArray.get(0);
        switch (argument) {
            case "+":
                final String username = mServerConfiguration.getSaslUsername();
                final String password = mServerConfiguration.getSaslPassword();
                mCapSender.sendSaslPlainAuthentication(username, password);
                break;
        }
    }

    private void initalizeCommandMap(final Map<CapCommand, Consumer<List<String>>> map) {
        map.put(CapCommand.LS, this::parseLS);
        map.put(CapCommand.ACK, this::parseAck);
        map.put(CapCommand.NAK, this::parseNak);
    }

    private void parseLS(final List<String> parsedArray) {
        final String rawCapabilities = parsedArray.remove(0);
        final String colonLessCapabilities = ParseUtils.removeInitialColonIfExists(rawCapabilities);
        final Set<ModifiedCapability> capabilities = parseCapabilities(colonLessCapabilities);

        final ModifiedCapability saslCap = collectionContainsCapability(capabilities, SASL);

        // We attempt to request every CAP capability we know about (with the exception of SASL)
        // before we try anything else
        if (capabilities.size() > 1 || capabilities.size() > 0 && saslCap == null) {
            mCapSender.sendRequestCapabilities(joinCapabilities(capabilities));
        } else if (saslCap != null && mServerConfiguration.shouldSendSasl()) {
            mCapSender.sendRequestSasl();
        } else {
            mCapSender.sendEnd();
        }
    }

    private String joinCapabilities(final Set<ModifiedCapability> capabilities) {
        return FluentIterable.from(capabilities)
                .transform(c -> c.getCapability().getCapabilityString())
                .join(Joiner.on(' '));
    }

    private void parseAck(final List<String> parsedArray) {
        final String rawCapabilities = parsedArray.remove(0);
        final String colonLessCapabilities = ParseUtils.removeInitialColonIfExists(rawCapabilities);
        final Set<ModifiedCapability> capabilities = parseCapabilities(colonLessCapabilities);

        final ModifiedCapability capability = collectionContainsCapability(capabilities, SASL);
        if (capability != null && capability.getModifier() != Modifier.DISABLE) {
            mCapSender.sendPlainAuthenticationRequest();
        }
    }

    private void parseNak(final List<String> parsedArray) {
        // TODO - this needs to be done
    }

    public static enum Modifier {
        DISABLE('-'),
        ACK('~'),
        STICKY('=');

        private final char mModifier;

        private Modifier(final char modifier) {
            mModifier = modifier;
        }

        public static Pair<String, Modifier> consumeModifier(final String modifiedString) {
            for (final Modifier modifier : Modifier.values()) {
                if (modifiedString.charAt(0) == modifier.getModifier()) {
                    return new Pair<>(modifiedString.substring(1), modifier);
                }
            }
            return new Pair<>(modifiedString, null);
        }

        public char getModifier() {
            return mModifier;
        }
    }

    public static final class ModifiedCapability {

        private final Modifier mModifier;

        private final CapCapability mCapability;

        public ModifiedCapability(final Modifier modifier, final CapCapability capability) {
            mModifier = modifier;
            mCapability = capability;
        }

        public Modifier getModifier() {
            return mModifier;
        }

        public CapCapability getCapability() {
            return mCapability;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final ModifiedCapability that = (ModifiedCapability) o;
            return mCapability == that.mCapability && mModifier == that.mModifier;
        }

        @Override
        public int hashCode() {
            int result = mModifier != null ? mModifier.hashCode() : 0;
            result = 31 * result + (mCapability != null ? mCapability.hashCode() : 0);
            return result;
        }
    }
}