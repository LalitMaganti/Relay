package co.fusionx.relay.internal.parser;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.internal.constants.CapCommand;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.function.Consumer;
import co.fusionx.relay.internal.sender.CapPacketSender;
import co.fusionx.relay.util.ParseUtils;

import static co.fusionx.relay.constants.CapCapability.SASL;
import static co.fusionx.relay.constants.CapCapability.getCapabilityFromString;

public class CapParser {

    private final Map<CapCommand, Consumer<List<String>>> mCapCommandMap;

    private final InternalServer mServer;

    private final ConnectionConfiguration mConnectionConfiguration;

    private final CapPacketSender mCapSender;

    private Set<ModifiedCapability> mPossibleCapabilities;

    @Inject
    public CapParser(final ConnectionConfiguration configuration, final InternalServer server,
            final CapPacketSender sender) {
        mServer = server;
        mConnectionConfiguration = configuration;
        mCapSender = sender;

        mCapCommandMap = new EnumMap<>(CapCommand.class);
        initalizeCommandMap(mCapCommandMap);
    }

    public static Set<ModifiedCapability> parseCapabilities(final String caps) {
        final Set<ModifiedCapability> capabilitySet = new HashSet<>();
        final List<String> capabilities = ParseUtils.splitRawLine(caps, false);

        for (final String capability : capabilities) {
            final Pair<String, Modifier> pair = Modifier.consumeModifier(capability);
            final CapCapability capCapability = getCapabilityFromString(pair.getLeft());
            if (capCapability == null) {
                continue;
            }
            capabilitySet.add(new ModifiedCapability(pair.getRight(), capCapability));
        }

        return capabilitySet;
    }

    public boolean serverHasCapability(final CapCapability capability) {
        for (final ModifiedCapability modifiedCapability : mPossibleCapabilities) {
            if (modifiedCapability.getCapability() == capability) {
                return true;
            }
        }
        return false;
    }

    public void parseCode(final int code, final List<String> parsedArray) {
        switch (code) {
            case ServerReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(2);
                mServer.getBus().post(new GenericServerEvent(mServer, loginMessage));
                break;
            case ServerReplyCodes.RPL_SASL_SUCCESSFUL:
                final String successful = parsedArray.get(0);
                mServer.getBus().post(new GenericServerEvent(mServer, successful));
                break;
            case ServerReplyCodes.ERR_SASL_FAIL:
            case ServerReplyCodes.ERR_SASL_TOO_LONG:
                final String error = parsedArray.get(0);
                mServer.getBus().post(new GenericServerEvent(mServer, error));
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
                final String username = mConnectionConfiguration.getSaslUsername();
                final String password = mConnectionConfiguration.getSaslPassword();
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
        mPossibleCapabilities = parseCapabilities(colonLessCapabilities);

        final boolean hasSasl = serverHasCapability(SASL);

        // We attempt to request every CAP capability we know about (with the exception of SASL)
        // before we try anything else
        if (mPossibleCapabilities.size() > 1 || mPossibleCapabilities.size() > 0 && !hasSasl) {
            mCapSender.sendRequestCapabilities(joinNonSaslCapabilities(mPossibleCapabilities));
        } else if (hasSasl && mConnectionConfiguration.shouldSendSasl()) {
            mCapSender.sendRequestSasl();
        } else {
            mCapSender.sendEnd();
        }
    }

    private String joinNonSaslCapabilities(final Set<ModifiedCapability> capabilities) {
        return FluentIterable.from(capabilities)
                .filter(c -> c.getCapability() != SASL)
                .transform(c -> c.getCapability().getCapabilityString())
                .join(Joiner.on(' '));
    }

    // TODO - a lot of assumptions are made by this method which are probably should not be made
    // - fix in the future
    private void parseAck(final List<String> parsedArray) {
        final String rawCapabilities = parsedArray.remove(0);
        final String colonLessCapabilities = ParseUtils.removeInitialColonIfExists(rawCapabilities);
        final Set<ModifiedCapability> capabilities = parseCapabilities(colonLessCapabilities);

        if (capabilities.size() == 1) {
            final ModifiedCapability modCap = Iterables.getLast(capabilities);
            if (modCap.getCapability() == SASL && modCap.getModifier() == null) {
                mCapSender.sendPlainAuthenticationRequest();
                return;
            }
        }

        for (final ModifiedCapability capability : capabilities) {
            mServer.addCapability(capability.getCapability());
        }

        final boolean hasSasl = serverHasCapability(SASL);
        if (hasSasl && mConnectionConfiguration.shouldSendSasl()) {
            mCapSender.sendRequestSasl();
        } else {
            mCapSender.sendEnd();
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
                    return Pair.of(modifiedString.substring(1), modifier);
                }
            }
            return Pair.of(modifiedString, null);
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