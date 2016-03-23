package co.fusionx.relay.event.query;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.base.QueryUser;

public class QueryActionWorldEvent extends QueryWorldEvent {

    public final String action;
    public final List<FormatSpanInfo> formats;

    public QueryActionWorldEvent(QueryUser userNick, String action, List<FormatSpanInfo> formats) {
        super(userNick);

        this.action = action;
        this.formats = formats;
    }
}