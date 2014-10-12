package co.fusionx.relay.constant;

public final class PrefixedCapability {

    private final CapModifier mCapModifier;

    private final Capability mCapability;

    public PrefixedCapability(final CapModifier capModifier, final Capability capability) {
        mCapModifier = capModifier;
        mCapability = capability;
    }

    public CapModifier getCapModifier() {
        return mCapModifier;
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

        final PrefixedCapability that = (PrefixedCapability) o;
        return mCapability == that.mCapability;
    }

    @Override
    public int hashCode() {
        return mCapability != null ? mCapability.hashCode() : 0;
    }
}