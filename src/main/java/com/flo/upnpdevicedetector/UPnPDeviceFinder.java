package com.flo.upnpdevicedetector;

import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by florent.noel on 6/14/13.
 */
public class UPnPDeviceFinder {
    private static String TAG = UPnPDeviceFinder.class.getName();

    private InetAddress mInetDeviceAdr;
    //private String mDeviceIP = "127.0.0.1";

    private UPnPSocket mSock;
    private ArrayList<String> mUPnPDeviceList = new ArrayList<String>();

    UPnPDeviceFinder(boolean IPV4){
        mInetDeviceAdr = getDeviceLocalIP(IPV4);
        Log.e(TAG, "IP is: " + mInetDeviceAdr);

        try {
            mSock = new UPnPSocket(mInetDeviceAdr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getDeviceLocalIP(boolean useIPv4) {
        Log.e(TAG, "getDeviceLocalIP");

        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        Log.e(TAG, "IP from inet is: " + addr);
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4){
                                Log.e(TAG, "IP v4");
                                return addr;
                            }
                        } else {
                            if (!isIPv4) {
                                Log.e(TAG, "IP v6");
                                //int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                //return delim<0 ? sAddr : sAddr.substring(0, delim);
                                return addr;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return null;
    }

    public ArrayList<String> getUPnPDevicesList(){

        if(mSock == null){
            Log.e(TAG, "socket is null, return");
            return null;
        }

        mUPnPDeviceList.clear();

        try {
            //broadcast SSDP search messages
            mSock.sendMulticastMsg();

            //listen to responses from network until the socket timeout
            while (true) {
                Log.e(TAG, "wait for dev. response");
                DatagramPacket dp = mSock.receiveMulticastMsg();
                String receivedString = new String(dp.getData());
                receivedString = receivedString.substring(0,dp.getLength());
                Log.e(TAG, "found dev: " + receivedString);
                mUPnPDeviceList.add(receivedString);
            }
        }
        catch (IOException e){
            //sock timeout will get us out of the loop
            Log.e(TAG, "time out");
            mSock.close();
        }

        return mUPnPDeviceList;
    }

}
