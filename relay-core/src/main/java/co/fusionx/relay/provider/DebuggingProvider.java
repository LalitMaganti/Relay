package co.fusionx.relay.provider;

public interface DebuggingProvider {

    public void logLineFromServer(final String line);

    public void logLineToServer(final String line);

    public void logNonFatalError(String nonFatalError);

    public void handleFatalError(RuntimeException ex);
}