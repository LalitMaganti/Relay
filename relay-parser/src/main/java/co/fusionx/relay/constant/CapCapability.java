package co.fusionx.relay.constant;

public enum CapCapability {
    ACCOUNTNOTIFY("account-notify"),
    AWAYNOTIFY("away-notify"),
    EXTENDEDJOIN("extended-join"),
    MULTIPREFIX("multi-prefix"),
    SASL("sasl");

    private final String mCapability;

    private CapCapability(final String capability) {
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