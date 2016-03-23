package co.fusionx.relay.event.query;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.internal.base.RelayMainUser;

public class QueryActionSelfEvent extends QuerySelfEvent {

    public final String action;
    public final List<FormatSpanInfo> formats;

    public QueryActionSelfEvent(final QueryUser user, final RelayMainUser relayMainUser,
            final String action, final List<FormatSpanInfo> formats) {
        super(user, relayMainUser);

        this.action = action;
        this.formats = formats;
    }
}