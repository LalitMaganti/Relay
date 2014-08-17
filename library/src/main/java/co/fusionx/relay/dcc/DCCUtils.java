package co.fusionx.relay.dcc;

public class DCCUtils {

    public static void bytesTransferredToOutputBuffer(final long bytesTransferred,
            final byte[] outBuffer) {
        outBuffer[0] = (byte) ((bytesTransferred >> 24) & 0xff);
        outBuffer[1] = (byte) ((bytesTransferred >> 16) & 0xff);
        outBuffer[2] = (byte) ((bytesTransferred >> 8) & 0xff);
        outBuffer[3] = (byte) (bytesTransferred & 0xff);
    }
}
