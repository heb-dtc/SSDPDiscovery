package com.flo.upnpdevicedetector;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

/**
 * Created by florent.noel on 6/14/13.
 */
public class UPnPSocket {
    private static String TAG = UPnPSocket.class.getName();

    private SocketAddress mMulticastGroup;
    private MulticastSocket mMultiSocket;

    UPnPSocket(InetAddress deviceIp) throws IOException {
        Log.e(TAG, "UPnPSocket");

        mMulticastGroup = new InetSocketAddress(SSDPUtils.ADDRESS, SSDPUtils.PORT);
        mMultiSocket = new MulticastSocket(new InetSocketAddress(deviceIp, 0));

        mMultiSocket.setSoTimeout(SSDPUtils.MSG_TIMEOUT);
    }

    public void sendMulticastMsg() throws IOException {
        String ssdpMsg = SSDPUtils.buildSSDPSearchString();

        Log.e(TAG, "sendMulticastMsg: " + ssdpMsg);

        DatagramPacket dp = new DatagramPacket(ssdpMsg.getBytes(), ssdpMsg.length(), mMulticastGroup);
        mMultiSocket.send(dp);
    }

    public DatagramPacket receiveMulticastMsg() throws IOException {
        byte[] buf = new byte[2048];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);

        mMultiSocket.receive(dp);

        return dp;
    }

    /**
     * Closing the Socket.
     */
    public void close() {
        if (mMultiSocket != null) {
            mMultiSocket.close();
        }
    }
}
