package co.fusionx.relay.constants;

public enum CapCapability {
    SASL("sasl");

    private final String mCapability;

    CapCapability(final String capability) {
        mCapability = capability;
    }

    public static CapCapability getCapabilityFromString(final String string) {
        for (final CapCapability capability : CapCapability.values()) {
            if (capability.getCapabilityString().equals(string)) {
                return capability;
            }
        }
        return null;
    }

    public String getCapabilityString() {
        return mCapability;
    }
}