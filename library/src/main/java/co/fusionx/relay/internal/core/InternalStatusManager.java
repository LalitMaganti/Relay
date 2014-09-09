package co.fusionx.relay.internal.core;

import co.fusionx.relay.core.SessionStatus;

public interface InternalStatusManager {

    public SessionStatus getStatus();

    public void incrementAttemptCount();

    public void resetAttemptCount();

    public boolean isReconnectNeeded();

    public void onConnecting();

    public void onConnected();

    public void onDisconnected(String serverMessage, boolean retryPending);

    public void onReconnecting();

    public void onStopped();
}