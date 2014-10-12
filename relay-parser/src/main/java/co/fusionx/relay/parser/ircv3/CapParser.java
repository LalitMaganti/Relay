package co.fusionx.relay.parser.ircv3;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.constant.CapCommand;
import co.fusionx.relay.constant.PrefixedCapability;
import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.function.DualConsumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;
import co.fusionx.relay.util.CapUtils;

public class CapParser implements CommandParser {

    private final Map<CapCommand, DualConsumer<String, List<String>>> mCapCommandMap
            = new HashMap<>();

    private final ObserverHelper<CapObserver> mObserverHelper = new ObserverHelper<>();

    public CapParser() {
        initializeCommandMap(mCapCommandMap);
    }

    public CapParser addObserver(final CapObserver observer) {
        mObserverHelper.addObserver(observer);
        return this;
    }

    public CapParser addObservers(final Collection<? extends CapObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    private void initializeCommandMap(final Map<CapCommand, DualConsumer<String,
            List<String>>> map) {
        map.put(CapCommand.LS, new DualConsumer<String, List<String>>() {
            @Override
            public void apply(final String target, final List<String> list) {
                parseLs(target, list);
            }
        });
        map.put(CapCommand.ACK, new DualConsumer<String, List<String>>() {
            @Override
            public void apply(final String target, final List<String> list) {
                parseAck(target, list);
            }
        });
        map.put(CapCommand.NAK, new DualConsumer<String, List<String>>() {
            @Override
            public void apply(final String target, final List<String> list) {
                parseNak(target, list);
            }
        });
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String target = parsedArray.remove(0); // Remove the target (ourselves)

        // Remove the CAP subcommand
        final String subCommandString = parsedArray.remove(0);
        final CapCommand subCommand = CapCommand.getCommandFromString(subCommandString);

        // Get the parsed array
        final DualConsumer<String, List<String>> parser = mCapCommandMap.get(subCommand);
        parser.apply(target, parsedArray);
    }

    private void parseLs(final String target, final List<String> parsedArray) {
        final String rawCapabilities = parsedArray.get(0);
        final Set<PrefixedCapability> possibleCapabilities = CapUtils.parseCapabilities(
                rawCapabilities);

        mObserverHelper.notifyObservers(new Consumer<CapObserver>() {
            @Override
            public void apply(final CapObserver capObserver) {
                capObserver.onCapabilitiesLsResponse(target, possibleCapabilities);
            }
        });
    }

    private void parseAck(final String target, final List<String> parsedArray) {
        final String rawCapabilities = parsedArray.get(0);
        final Set<PrefixedCapability> capabilities = CapUtils.parseCapabilities(rawCapabilities);

        mObserverHelper.notifyObservers(new Consumer<CapObserver>() {
            @Override
            public void apply(final CapObserver capObserver) {
                capObserver.onCapabilitiesAccepted(target, capabilities);
            }
        });
    }

    private void parseNak(final String prefix, final List<String> parsedArray) {
        // TODO - this needs to be done
    }

    public static interface CapObserver {

        public void onCapabilitiesLsResponse(final String target,
                final Set<PrefixedCapability> capabilities);

        public void onCapabilitiesAccepted(final String target,
                final Set<PrefixedCapability> capabilities);
    }
}