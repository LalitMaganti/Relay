package co.fusionx.relay.event.query;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.base.QueryUser;

public class QueryMessageWorldEvent extends QueryWorldEvent {

    public final String message;
    public final List<FormatSpanInfo> formats;

    public QueryMessageWorldEvent(final QueryUser user, final String message,
            final List<FormatSpanInfo> formats) {
        super(user);

        this.message = message;
        this.formats = formats;
    }
}