package com.flo.upnpdevicedetector;

import android.util.Log;

/**
 * Created by florent.noel on 6/14/13.
 */
public class SSDPUtils {
    private static String TAG = SSDPUtils.class.getName();

    public static final String ADDRESS = "239.255.255.250";

    public static final int PORT = 1900;

    public static final int MAX_REPLY_TIME = 5;
    public static final int MSG_TIMEOUT = MAX_REPLY_TIME * 1000 + 1000;

    public static final String STRING_MSEARCH = "M-SEARCH * HTTP/1.1";

    public static final String STRING_RootDevice = "ST: upnp:rootdevice";

    public static final String NEWLINE = "\r\n";
    public static final String MAN = "Man:\"ssdp:discover\"";

    public static String LOCATION_TEXT = "LOCATION: http://";

    public static String buildSSDPSearchString(){
        StringBuilder content = new StringBuilder();

        content.append(STRING_MSEARCH).append(NEWLINE);
        content.append("Host: " + ADDRESS + ":" + PORT).append(NEWLINE);
        content.append(MAN).append(NEWLINE);
        content.append("MX: " + MAX_REPLY_TIME).append(NEWLINE);
        content.append(STRING_RootDevice).append(NEWLINE);
        content.append(NEWLINE);

        Log.e(TAG, content.toString());

        return content.toString();
    }

    public static String parseIP(String msearchAnswer){
        String ip = "0.0.0.0";

        //find the index of "LOCATION: http://"
        int loactionLinePos = msearchAnswer.indexOf(LOCATION_TEXT);

        if(loactionLinePos != -1){
            //position the index right after "LOCATION: http://"
            loactionLinePos += LOCATION_TEXT.length();

            //find the next semi-colon (would be the one that separate IP from PORT nr)
            int locColon = msearchAnswer.indexOf(":", loactionLinePos);
            //grab IP
            ip = msearchAnswer.substring(loactionLinePos, locColon);
        }
        return ip;
    }
}
