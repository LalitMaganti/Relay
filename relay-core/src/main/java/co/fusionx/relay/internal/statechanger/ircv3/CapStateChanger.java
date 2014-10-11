package co.fusionx.relay.internal.statechanger.ircv3;

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

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.constant.CapCommand;
import co.fusionx.relay.constant.Capability;
import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.sender.CapSender;
import co.fusionx.relay.parser.ircv3.CapParser;
import co.fusionx.relay.util.ParseUtils;

import static co.fusionx.relay.constant.Capability.SASL;
import static co.fusionx.relay.constant.Capability.parseCapability;

public class CapStateChanger implements CapParser.CapObserver {

    private final InternalServer mServer;

    private final ConnectionConfiguration mConnectionConfiguration;

    private final CapSender mCapSender;

    private Set<CapParser.ModifiedCapability> mPossibleCapabilities;

    @Inject
    public CapStateChanger(final ConnectionConfiguration configuration, final InternalServer server,
            final CapSender sender) {
        mServer = server;
        mConnectionConfiguration = configuration;
        mCapSender = sender;
    }

    public static Set<CapParser.ModifiedCapability> parseCapabilities(final String caps) {
        final Set<CapParser.ModifiedCapability> capabilitySet = new HashSet<>();
        final List<String> capabilities = ParseUtils.splitRawLine(caps, false);

        for (final String capability : capabilities) {
            final Pair<String, CapParser.Modifier> pair = CapParser.Modifier
                    .consumeModifier(capability);
            final Capability capCapability = parseCapability(pair.getLeft());
            if (capCapability == null) {
                continue;
            }
            capabilitySet.add(new CapParser.ModifiedCapability(pair.getRight(), capCapability));
        }

        return capabilitySet;
    }

    public boolean serverHasCapability(final Capability capability) {
        for (final CapParser.ModifiedCapability modifiedCapability : mPossibleCapabilities) {
            if (modifiedCapability.getCapability() == capability) {
                return true;
            }
        }
        return false;
    }

    private String joinNonSaslCapabilities(final Set<CapParser.ModifiedCapability> capabilities) {
        return FluentIterable.from(capabilities)
                .filter(c -> c.getCapability() != SASL)
                .transform(c -> c.getCapability().getCapabilityString())
                .join(Joiner.on(' '));
    }

    @Override
    public void onCapabilitiesLsResponse(final String target,
            final Set<CapParser.ModifiedCapability> capabilities) {
        mPossibleCapabilities.addAll(capabilities);

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

    // TODO - a lot of assumptions are made by this method which are probably should not be made
    // - fix in the future
    @Override
    public void onCapabilitiesAccepted(final String target,
            final Set<CapParser.ModifiedCapability> capabilities) {
        if (capabilities.size() == 1) {
            final CapParser.ModifiedCapability modCap = Iterables.getLast(capabilities);
            if (modCap.getCapability() == SASL && modCap.getModifier() == null) {
                mCapSender.sendPlainAuthenticationRequest();
                return;
            }
        }

        for (final CapParser.ModifiedCapability capability : capabilities) {
            mServer.addCapability(capability.getCapability());
        }

        final boolean hasSasl = serverHasCapability(SASL);
        if (hasSasl && mConnectionConfiguration.shouldSendSasl()) {
            mCapSender.sendRequestSasl();
        } else {
            mCapSender.sendEnd();
        }
    }

    /*
    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String argument = parsedArray.get(0);
        switch (argument) {
            case "+":
                final String username = mConnectionConfiguration.getSaslUsername();
                final String password = mConnectionConfiguration.getSaslPassword();
                mCapSender.sendSaslPlainAuthentication(username, password);
                break;
        }
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        switch (code) {
            case ReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(2);
                mServer.postEvent(new GenericServerEvent(mServer, loginMessage));
                break;
            case ReplyCodes.RPL_SASL_SUCCESSFUL:
                final String successful = parsedArray.get(0);
                mServer.postEvent(new GenericServerEvent(mServer, successful));
                break;
            case ReplyCodes.ERR_SASL_FAIL:
            case ReplyCodes.ERR_SASL_TOO_LONG:
                final String error = parsedArray.get(0);
                mServer.postEvent(new GenericServerEvent(mServer, error));
                break;
        }
        mCapSender.sendEnd();
    }
    */
}