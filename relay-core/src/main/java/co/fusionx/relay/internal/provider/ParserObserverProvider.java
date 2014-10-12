package co.fusionx.relay.internal.provider;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.statechanger.ircv3.CapStateChanger;
import co.fusionx.relay.internal.statechanger.ircv3.SaslStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.InviteStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.JoinStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.KickStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.MotdStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.NameStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.NickStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.PartStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.PingStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.QuitStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.TopicCodeStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.TopicStateChanger;
import co.fusionx.relay.parser.ircv3.CapParser;
import co.fusionx.relay.parser.ircv3.NickPrefixNameParser;
import co.fusionx.relay.parser.ircv3.SaslParser;
import co.fusionx.relay.parser.rfc.InviteParser;
import co.fusionx.relay.parser.rfc.JoinParser;
import co.fusionx.relay.parser.rfc.KickParser;
import co.fusionx.relay.parser.rfc.MotdParser;
import co.fusionx.relay.parser.rfc.NickParser;
import co.fusionx.relay.parser.rfc.PartParser;
import co.fusionx.relay.parser.rfc.PingParser;
import co.fusionx.relay.parser.rfc.QuitParser;
import co.fusionx.relay.parser.rfc.TopicCodeParser;
import co.fusionx.relay.parser.rfc.TopicParser;

public class ParserObserverProvider {

    private final InternalServer mInternalServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryUserGroup;

    private final PacketSender mPacketSender;

    // Derived fields
    private final ConnectionConfiguration mConnectionConfiguration;

    @Inject
    public ParserObserverProvider(final InternalServer internalServer,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryUserGroup,
            final PacketSender packetSender) {
        mInternalServer = internalServer;
        mUserChannelGroup = userChannelGroup;
        mQueryUserGroup = queryUserGroup;
        mPacketSender = packetSender;

        mConnectionConfiguration = internalServer.getConfiguration().getConnectionConfiguration();
    }

    public InviteParser.InviteObserver getInviteObserver() {
        return new InviteStateChanger(mInternalServer);
    }

    public JoinParser.JoinObserver getJoinObserver() {
        return new JoinStateChanger(mInternalServer, mUserChannelGroup);
    }

    public KickParser.KickObserver getKickObserver() {
        return new KickStateChanger(mInternalServer, mUserChannelGroup);
    }

    public NickParser.NickObserver getNickProvider() {
        return new NickStateChanger(mInternalServer, mUserChannelGroup);
    }

    public PartParser.PartObserver getPartObserver() {
        return new PartStateChanger(mInternalServer, mUserChannelGroup);
    }

    public PingParser.PingObserver getPingObserver() {
        return new PingStateChanger(mPacketSender);
    }

    public TopicParser.TopicObserver getTopicObserver() {
        return new TopicStateChanger(mInternalServer, mUserChannelGroup);
    }

    public QuitParser.QuitObserver getQuitObserver() {
        return new QuitStateChanger(mUserChannelGroup, mQueryUserGroup);
    }

    // Reply code parser observers
    public MotdParser.MotdObserver getMotdObserver() {
        return new MotdStateChanger(mInternalServer);
    }

    public NickPrefixNameParser.NickPrefixNameObserver getNickPrefixNameObserver() {
        return new NameStateChanger(mUserChannelGroup);
    }

    public TopicCodeParser.TopicCodeObserver getTopicCodeObserver() {
        return new TopicCodeStateChanger(mInternalServer, mUserChannelGroup);
    }

    // IRCv3 observers
    public CapParser.CapObserver getCapObserver() {
        return new CapStateChanger(mConnectionConfiguration, mInternalServer, mPacketSender);
    }

    public SaslParser.SaslObserver getSaslObserver() {
        return new SaslStateChanger(mConnectionConfiguration, mInternalServer, mPacketSender);
    }
}