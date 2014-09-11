package co.fusionx.relay.internal.bus;

import co.fusionx.relay.internal.core.Postable;
import co.fusionx.relay.core.Registerable;

public interface EventBus<T> extends Postable<T>, Registerable {
}