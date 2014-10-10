package co.fusionx.relay.util;

public class DCCUtils {

    public static void bytesTransferredToOutputBuffer(final long bytesTransferred,
            final byte[] outBuffer) {
        outBuffer[0] = (byte) ((bytesTransferred >> 24) & 0xff);
        outBuffer[1] = (byte) ((bytesTransferred >> 16) & 0xff);
        outBuffer[2] = (byte) ((bytesTransferred >> 8) & 0xff);
        outBuffer[3] = (byte) (bytesTransferred & 0xff);
    }

    /**
     * Returns the 32bit dotted format of the provided long ip.
     *
     * @param ip the long ip
     * @return the 32bit dotted format of <code>ip</code>
     * @throws IllegalArgumentException if <code>ip</code> is invalid
     */
    public static String ipDecimalToString(final long ip) {
        // if ip is bigger than 255.255.255.255 or smaller than 0.0.0.0
        if (ip > 4294967295l || ip < 0) {
            throw new IllegalArgumentException("Invalid IP Address");
        }

        final StringBuilder ipAddress = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            int shift = i * 8;
            ipAddress.append((ip & (0xff << shift)) >> shift);
            if (i > 0) {
                ipAddress.append(".");
            }
        }

        return ipAddress.toString();
    }
}
