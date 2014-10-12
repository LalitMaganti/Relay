package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class NoticeParser implements CommandParser {

    public final ObserverHelper<NoticeObserver> mObserverHelper = new ObserverHelper<>();

    public NoticeParser addObserver(final NoticeObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String recipient = parsedArray.get(0);
        final String notice = parsedArray.get(1);

        // Notices can be CTCP replies
        /*if (CTCPParser.isCtcpMessage(notice)) {
            mCTCPParser.onParseReply(parsedArray, prefix);
        } else {
        }*/

        mObserverHelper.notifyObservers(new Consumer<NoticeObserver>() {
            @Override
            public void apply(final NoticeObserver observer) {
                observer.onNotice(prefix, recipient, notice);
            }
        });
    }

    public static interface NoticeObserver {

        public void onNotice(final String prefix, final String recipient, final String notice);
    }
}