package co.fusionx.relay.constant;

public enum Capability {
    ACCOUNTNOTIFY("account-notify"),
    AWAYNOTIFY("away-notify"),
    EXTENDEDJOIN("extended-join"),
    MULTIPREFIX("multi-prefix"),
    SASL("sasl");

    private final String mCapability;

    private Capability(final String capability) {
        mCapability = capability;
    }

    public static Capability parseCapability(final String string) {
        for (final Capability capability : Capability.values()) {
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