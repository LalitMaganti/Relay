package co.fusionx.relay.internal.sender;

import android.text.TextUtils;

import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.event.query.QueryActionSelfEvent;
import co.fusionx.relay.event.query.QueryClosedEvent;
import co.fusionx.relay.event.query.QueryMessageSelfEvent;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.packet.query.QueryActionPacket;
import co.fusionx.relay.internal.packet.query.QueryMessagePacket;
import co.fusionx.relay.sender.QuerySender;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayQuerySender implements QuerySender {

    private final PacketSender mSender;

    private final LibraryUser mUser;

    private final String mQueryNick;

    private final InternalQueryUserGroup mQueryGroup;

    private InternalQueryUser mQueryUser;

    public RelayQuerySender(final PacketSender sender, final LibraryUser user,
            final InternalQueryUserGroup queryGroup) {
        mSender = sender;
        mUser = user;
        mQueryGroup = queryGroup;

        mQueryNick = mQueryUser.getNick().getNickAsString();
    }

    // I tried very hard to avoid this but no matter how much I tried to abstract this away,
    // circular dependencies keep popping up due to having to send the channel in events
    public void setQueryUser(final InternalQueryUser queryUser) {
        mQueryUser = queryUser;
    }

    @Override
    public void sendAction(final String action) {
        if (TextUtils.isEmpty(action)) {
            return;
        }
        mSender.sendPacket(new QueryActionPacket(mQueryNick, action));

        if (getPreferences().isSelfEventHidden()) {
            return;
        }
        mQueryUser.getBus().post(new QueryActionSelfEvent(mQueryUser, mUser, action));
    }

    @Override
    public void sendMessage(final String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mSender.sendPacket(new QueryMessagePacket(mQueryNick, message));

        if (getPreferences().isSelfEventHidden()) {
            return;
        }
        mQueryUser.getBus().post(new QueryMessageSelfEvent(mQueryUser, mUser, message));
    }

    @Override
    public void close() {
        mQueryGroup.removeQueryUser(mQueryUser);
        mQueryUser.getBus().post(new QueryClosedEvent(mQueryUser));
    }
}
