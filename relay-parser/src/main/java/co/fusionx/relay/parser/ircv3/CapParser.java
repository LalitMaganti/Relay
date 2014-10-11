package co.fusionx.relay.parser.ircv3;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.constant.CapCommand;
import co.fusionx.relay.constant.Capability;
import co.fusionx.relay.function.DualConsumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.util.ParseUtils;

public class CapParser implements CommandParser {

    private final Map<CapCommand, DualConsumer<String, List<String>>> mCapCommandMap;

    private final CapObserver mCapObserver;

    public CapParser(final CapObserver capObserver) {
        mCapObserver = capObserver;

        mCapCommandMap = new HashMap<>();
        initializeCommandMap(mCapCommandMap);
    }

    public static Set<ModifiedCapability> parseCapabilities(final String caps) {
        final Set<ModifiedCapability> capabilitySet = new HashSet<>();
        final List<String> capabilities = ParseUtils.splitRawLine(caps, false);

        for (final String capability : capabilities) {
            final Pair<String, Modifier> pair = Modifier.consumeModifier(capability);
            final Capability cap = Capability.parseCapability(pair.getLeft());
            if (cap == null) {
                continue;
            }
            capabilitySet.add(new ModifiedCapability(pair.getRight(), cap));
        }

        return capabilitySet;
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
        final String colonLessCapabilities = ParseUtils.removeInitialColonIfExists(rawCapabilities);
        final Set<ModifiedCapability> possibleCapabilities
                = parseCapabilities(colonLessCapabilities);

        mCapObserver.onCapabilitiesLsResponse(target, possibleCapabilities);
    }

    private void parseAck(final String target, final List<String> parsedArray) {
        final String rawCapabilities = parsedArray.get(0);
        final String colonLessCapabilities = ParseUtils.removeInitialColonIfExists(rawCapabilities);
        final Set<ModifiedCapability> capabilities = parseCapabilities(colonLessCapabilities);

        mCapObserver.onCapabilitiesAccepted(target, capabilities);
    }

    private void parseNak(final String prefix, final List<String> parsedArray) {
        // TODO - this needs to be done
    }

    public static enum Modifier {
        DISABLE('-'),
        ACK('~'),
        STICKY('=');

        private final char mModifier;

        private Modifier(final char modifier) {
            mModifier = modifier;
        }

        public static Pair<String, Modifier> consumeModifier(final String modifiedString) {
            for (final Modifier modifier : Modifier.values()) {
                if (modifiedString.charAt(0) == modifier.getModifier()) {
                    return Pair.of(modifiedString.substring(1), modifier);
                }
            }
            return Pair.of(modifiedString, null);
        }

        public char getModifier() {
            return mModifier;
        }
    }

    public static interface CapObserver {

        public void onCapabilitiesLsResponse(final String target,
                final Set<ModifiedCapability> capabilities);

        public void onCapabilitiesAccepted(String target, Set<ModifiedCapability> capabilities);
    }

    public static final class ModifiedCapability {

        private final Modifier mModifier;

        private final Capability mCapability;

        public ModifiedCapability(final Modifier modifier, final Capability capability) {
            mModifier = modifier;
            mCapability = capability;
        }

        public Modifier getModifier() {
            return mModifier;
        }

        public Capability getCapability() {
            return mCapability;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final ModifiedCapability that = (ModifiedCapability) o;
            return mCapability == that.mCapability;
        }

        @Override
        public int hashCode() {
            return mCapability != null ? mCapability.hashCode() : 0;
        }
    }
}