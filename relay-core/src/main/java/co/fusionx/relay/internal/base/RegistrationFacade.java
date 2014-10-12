package co.fusionx.relay.internal.base;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.internal.sender.CapSender;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.sender.ServerSender;
import co.fusionx.relay.util.Utils;

public class RegistrationFacade {

    private final ConnectionConfiguration mConnectionConfiguration;

    private final InternalSender mInternalSender;

    private final CapSender mCapSender;

    private final ServerSender mServerSender;

    @Inject
    public RegistrationFacade(final ConnectionConfiguration connectionConfiguration,
            final InternalSender internalSender, final CapSender capSender,
            final ServerSender serverSender) {
        mConnectionConfiguration = connectionConfiguration;
        mInternalSender = internalSender;
        mCapSender = capSender;
        mServerSender = serverSender;
    }

    public void registerConnection() {
        // By sending this line, the server *should* wait until we end the CAP negotiation
        // That is if the server supports IRCv3
        mCapSender.sendLs();

        // Follow RFC2812's recommended order of sending - PASS -> NICK -> USER
        if (StringUtils.isNotEmpty(mConnectionConfiguration.getServerPassword())) {
            mInternalSender.sendServerPassword(mConnectionConfiguration.getServerPassword());
        }
        mServerSender.sendNick(mConnectionConfiguration.getNickProvider().getFirst());
        mInternalSender.sendUser(mConnectionConfiguration.getServerUserName(),
                Utils.returnNonEmpty(mConnectionConfiguration.getRealName(), "RelayUser"));
    }

    public void postRegister() {
        // Identifies with NickServ if the password exists
        if (StringUtils.isNotEmpty(mConnectionConfiguration.getNickservPassword())) {
            mInternalSender.sendNickServPassword(mConnectionConfiguration.getNickservPassword());
        }
    }
}