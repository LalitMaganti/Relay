package co.fusionx.relay.internal.statechanger.ircv3;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.HashSet;
import java.util.Set;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.constant.CapModifier;
import co.fusionx.relay.constant.Capability;
import co.fusionx.relay.constant.PrefixedCapability;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.sender.CapSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.parser.ircv3.CapParser;

public class CapStateChanger implements CapParser.CapObserver {

    private final InternalServer mServer;

    private final ConnectionConfiguration mConnectionConfiguration;

    private final CapSender mCapSender;

    private Set<PrefixedCapability> mPossibleCapabilities;

    public CapStateChanger(final ConnectionConfiguration configuration, final InternalServer server,
            final PacketSender sender) {
        mServer = server;
        mConnectionConfiguration = configuration;

        mPossibleCapabilities = new HashSet<>();
        mCapSender = new CapSender(sender);
    }

    @Override
    public void onCapabilitiesLsResponse(final String target,
            final Set<PrefixedCapability> capabilities) {
        mPossibleCapabilities.addAll(capabilities);

        final boolean hasSasl = isCapabilityPossible(Capability.SASL);

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
            final Set<PrefixedCapability> capabilities) {
        if (capabilities.size() == 1) {
            final PrefixedCapability modCap = Iterables.getLast(capabilities);
            if (modCap.getCapability() == Capability.SASL &&
                    modCap.getCapModifier() != CapModifier.DISABLE) {
                // If there is only one capability and it's SASL which isn't being disabled,
                // then send a plain SASL request
                mCapSender.sendPlainAuthenticationRequest();
                return;
            }
        }

        for (final PrefixedCapability capability : capabilities) {
            if (capability.getCapModifier() != CapModifier.DISABLE) {
                // For every capability which is not disabled, add the capability to the server
                mServer.addCapability(capability.getCapability());
            }
        }

        // If the server can SASL and we can send it, then request it.  Otherwise end capability
        // negotiation
        final boolean hasSasl = isCapabilityPossible(Capability.SASL);
        if (hasSasl && mConnectionConfiguration.shouldSendSasl()) {
            mCapSender.sendRequestSasl();
        } else {
            mCapSender.sendEnd();
        }
    }

    public boolean isCapabilityPossible(final Capability capability) {
        for (final PrefixedCapability prefixedCapability : mPossibleCapabilities) {
            if (prefixedCapability.getCapability() == capability) {
                return true;
            }
        }
        return false;
    }

    private String joinNonSaslCapabilities(final Set<PrefixedCapability> capabilities) {
        return FluentIterable.from(capabilities)
                .filter(c -> c.getCapability() != Capability.SASL)
                .transform(c -> c.getCapability().getCapabilityString())
                .join(Joiner.on(' '));
    }
}