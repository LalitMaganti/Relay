package co.fusionx.relay.internal.provider;

import javax.inject.Inject;

import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.statechanger.InviteStateChanger;
import co.fusionx.relay.internal.statechanger.PartStateChanger;
import co.fusionx.relay.internal.statechanger.PingStateChanger;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.statechanger.JoinStateChanger;
import co.fusionx.relay.internal.statechanger.NameStateChanger;
import co.fusionx.relay.internal.statechanger.NickStateChanger;
import co.fusionx.relay.internal.statechanger.QuitStateChanger;
import co.fusionx.relay.internal.statechanger.TopicStateChanger;
import co.fusionx.relay.parser.ircv3.NickPrefixNameParser;
import co.fusionx.relay.parser.rfc.InviteParser;
import co.fusionx.relay.parser.rfc.NameParser;
import co.fusionx.relay.parser.rfc.JoinParser;
import co.fusionx.relay.parser.rfc.NickParser;
import co.fusionx.relay.parser.rfc.PartParser;
import co.fusionx.relay.parser.rfc.PingParser;
import co.fusionx.relay.parser.rfc.QuitParser;
import co.fusionx.relay.parser.rfc.TopicParser;

public class ParserObserverProvider {

    private final InternalServer mInternalServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryUserGroup;

    private final PacketSender mPacketSender;

    @Inject
    public ParserObserverProvider(final InternalServer internalServer,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryUserGroup,
            final PacketSender packetSender) {
        mInternalServer = internalServer;
        mUserChannelGroup = userChannelGroup;
        mQueryUserGroup = queryUserGroup;
        mPacketSender = packetSender;
    }

    public InviteParser.InviteObserver getInviteObserver() {
        return new InviteStateChanger(mInternalServer);
    }

    public JoinParser.JoinObserver getJoinObserver() {
        return new JoinStateChanger(mInternalServer, mUserChannelGroup);
    }

    public NickPrefixNameParser.NickPrefixNameObserver getNickPrefixNameObserver() {
        return new NameStateChanger(mUserChannelGroup);
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
}