package co.fusionx.relay.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.fusionx.relay.constant.CapModifier;
import co.fusionx.relay.constant.Capability;
import co.fusionx.relay.constant.PrefixedCapability;

public class CapUtils {

    public static Set<PrefixedCapability> parseCapabilities(final String caps) {
        final Set<PrefixedCapability> capabilitySet = new HashSet<>();
        final List<String> capabilities = ParseUtils.splitRawLine(caps, false);

        for (final String capability : capabilities) {
            final Pair<String, CapModifier> pair = CapModifier.consumeModifier(capability);
            final Capability cap = Capability.parseCapability(pair.getLeft());
            if (cap == null) {
                continue;
            }
            capabilitySet.add(new PrefixedCapability(pair.getRight(), cap));
        }

        return capabilitySet;
    }
}