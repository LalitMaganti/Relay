package co.fusionx.relay.internal.base;

import co.fusionx.relay.base.SessionStatus;

public interface StatusManager {

    public SessionStatus getStatus();

    public void incrementAttemptCount();

    public boolean isReconnectNeeded();

    public void onConnecting();

    public void onConnected();

    public void onDisconnected(String serverMessage, boolean retryPending);

    public void onReconnecting();

    public void onStopped();
}