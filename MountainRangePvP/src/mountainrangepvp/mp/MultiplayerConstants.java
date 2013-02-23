/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author lachlan
 */
public class MultiplayerConstants {

    public static final int STD_PORT = 2424;
    //
    public static final int CHECK_CODE = 0x01FA12D33;
    public static final int VERSION = 3;
    //
    public static final int PLAYER_UPDATE_TIMER = 50;
    //
    /**
     * Ping rate, in Hertz
     */
    public static final int PING_RATE = 1;
    public static final int PING_SERVER_FRESHNESS = 10000;
    public static final byte[] PING_DATA = new byte[]{
        (byte) (CHECK_CODE >>> 24),
        (byte) (CHECK_CODE >>> 16),
        (byte) (CHECK_CODE >>> 8),
        (byte) (CHECK_CODE),
        (byte) (VERSION >>> 24),
        (byte) (VERSION >>> 16),
        (byte) (VERSION >>> 8),
        (byte) (VERSION)};
    public static final int MULTICAST_PORT = 5050;
    public static final String MULTICAST_ADDRESS_STRING = "225.123.54.192";
    public static final InetAddress MULTICAST_ADDRESS;

    static {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(MULTICAST_ADDRESS_STRING);
        } catch (UnknownHostException ex) {
            addr = null;
        }

        MULTICAST_ADDRESS = addr;
    }
}
