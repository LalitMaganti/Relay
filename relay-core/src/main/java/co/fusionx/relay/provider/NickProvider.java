package co.fusionx.relay.provider;

public interface NickProvider {

    public String getFirst();

    public String getNickAtPosition(final int position);

    public int getNickCount();
}