package co.fusionx.relay.internal.provider;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.internal.base.RegistrationFacade;
import co.fusionx.relay.internal.base.SessionLevelObserver;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.statechanger.ircv3.CapStateChanger;
import co.fusionx.relay.internal.statechanger.ircv3.SaslStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.InviteStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.JoinStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.KickStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.ModeStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.MotdStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.NameStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.NickStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.NoticeStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.PartStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.PingStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.PrivmsgStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.QuitStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.TopicCodeStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.TopicStateChanger;
import co.fusionx.relay.internal.statechanger.rfc.WallopsStateChanger;
import co.fusionx.relay.parser.ircv3.CapParser;
import co.fusionx.relay.parser.ircv3.NickPrefixNameParser;
import co.fusionx.relay.parser.ircv3.SaslParser;
import co.fusionx.relay.parser.rfc.InviteParser;
import co.fusionx.relay.parser.rfc.JoinParser;
import co.fusionx.relay.parser.rfc.KickParser;
import co.fusionx.relay.parser.rfc.ModeParser;
import co.fusionx.relay.parser.rfc.MotdParser;
import co.fusionx.relay.parser.rfc.NickParser;
import co.fusionx.relay.parser.rfc.NoticeParser;
import co.fusionx.relay.parser.rfc.PartParser;
import co.fusionx.relay.parser.rfc.PingParser;
import co.fusionx.relay.parser.rfc.PrivmsgParser;
import co.fusionx.relay.parser.rfc.QuitParser;
import co.fusionx.relay.parser.rfc.TopicCodeParser;
import co.fusionx.relay.parser.rfc.TopicParser;
import co.fusionx.relay.parser.rfc.WallopsParser;
import co.fusionx.relay.parser.rfc.WelcomeParser;

public class ParserObserverProvider {

    private final InternalStatusManager mStatusManager;

    private final RegistrationFacade mRegistrationFacade;

    private final InternalServer mInternalServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryUserGroup;

    private final PacketSender mPacketSender;

    // Derived fields
    private final ConnectionConfiguration mConnectionConfiguration;

    @Inject
    public ParserObserverProvider(final InternalStatusManager statusManager,
            final RegistrationFacade registrationFacade,
            final InternalServer internalServer,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryUserGroup,
            final PacketSender packetSender) {
        mStatusManager = statusManager;
        mRegistrationFacade = registrationFacade;
        mInternalServer = internalServer;
        mUserChannelGroup = userChannelGroup;
        mQueryUserGroup = queryUserGroup;
        mPacketSender = packetSender;

        mConnectionConfiguration = internalServer.getConfiguration().getConnectionConfiguration();
    }

    public List<? extends InviteParser.InviteObserver> getInviteObservers() {
        return ImmutableList.of(new InviteStateChanger(mInternalServer));
    }

    public List<? extends JoinParser.JoinObserver> getJoinObservers() {
        return ImmutableList.of(new JoinStateChanger(mInternalServer, mUserChannelGroup));
    }

    public List<? extends KickParser.KickObserver> getKickObservers() {
        return ImmutableList.of(new KickStateChanger(mInternalServer, mUserChannelGroup));
    }

    public Collection<? extends ModeParser.ModeObserver> getModeObservers() {
        return ImmutableList.of(new ModeStateChanger(mInternalServer, mUserChannelGroup));
    }

    public Collection<? extends NoticeParser.NoticeObserver> getNoticeObservers() {
        return ImmutableList.of(new NoticeStateChanger(mInternalServer, mUserChannelGroup,
                mQueryUserGroup));
    }

    public List<? extends NickParser.NickObserver> getNickObservers() {
        return ImmutableList.of(new NickStateChanger(mInternalServer, mUserChannelGroup));
    }

    public List<? extends PartParser.PartObserver> getPartObservers() {
        return ImmutableList.of(new PartStateChanger(mInternalServer, mUserChannelGroup));
    }

    public List<? extends PingParser.PingObserver> getPingObservers() {
        return ImmutableList.of(new PingStateChanger(mPacketSender));
    }

    public Collection<? extends PrivmsgParser.PrivmsgObserver> getPrivMsgObservers() {
        return ImmutableList.of(new PrivmsgStateChanger(mInternalServer, mUserChannelGroup,
                mQueryUserGroup));
    }

    public List<? extends TopicParser.TopicObserver> getTopicObservers() {
        return ImmutableList.of(new TopicStateChanger(mInternalServer, mUserChannelGroup));
    }

    public List<? extends QuitParser.QuitObserver> getQuitObservers() {
        return ImmutableList.of(new QuitStateChanger(mUserChannelGroup, mQueryUserGroup));
    }

    public Collection<? extends WallopsParser.WallopsObserver> getWallopsObservers() {
        return ImmutableList.of(new WallopsStateChanger(mInternalServer));
    }

    // Reply code parser observers
    public List<? extends MotdParser.MotdObserver> getMotdObservers() {
        return ImmutableList.of(new MotdStateChanger(mInternalServer));
    }

    public List<? extends NickPrefixNameParser.NickPrefixNameObserver> getNickPrefixNameObservers() {
        return ImmutableList.of(new NameStateChanger(mUserChannelGroup));
    }

    public List<? extends TopicCodeParser.TopicCodeObserver> getTopicCodeObservers() {
        return ImmutableList.of(new TopicCodeStateChanger(mInternalServer, mUserChannelGroup));
    }

    public List<? extends WelcomeParser.WelcomeObserver> getWelcomeObservers() {
        return ImmutableList.of(new SessionLevelObserver(mStatusManager, mRegistrationFacade,
                mUserChannelGroup));
    }

    // IRCv3 observers
    public List<? extends CapParser.CapObserver> getCapObservers() {
        return ImmutableList.of(new CapStateChanger(mConnectionConfiguration, mInternalServer,
                mPacketSender));
    }

    public List<? extends SaslParser.SaslObserver> getSaslObservers() {
        return ImmutableList.of(new SaslStateChanger(mConnectionConfiguration, mInternalServer,
                mPacketSender));
    }
}