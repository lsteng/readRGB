package com.bitmap.readrgb.util.connect;

/**
 * Created by kennethyeh on 16/4/29.
 */
public class ConnectionConstant {

    public interface ResError{

        public int CONNECT_TIMED_OUT   = 2;
        public int CONNECT_ERROR       = 3;
    }

    public static final int connectionTimeout   = 15000; // ip connect timeout
    public static final int socketTimeout       = 15000; // after ip connection do socket connect timeout

    public static boolean checkConnectError(int httpStatus){
        boolean flag = false;
        if(httpStatus == ConnectionConstant.ResError.CONNECT_TIMED_OUT
                || httpStatus == ConnectionConstant.ResError.CONNECT_ERROR
                ){
            flag = true;
        }
        return flag;
    }
}
