package co.fusionx.relay.event.query;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.internal.base.RelayMainUser;

public class QueryMessageSelfEvent extends QuerySelfEvent {

    public final String message;
    public final List<FormatSpanInfo> formats;

    public QueryMessageSelfEvent(final QueryUser user, final RelayMainUser relayMainUser,
            final String message, final List<FormatSpanInfo> formats) {
        super(user, relayMainUser);

        this.message = message;
        this.formats = formats;
    }
}