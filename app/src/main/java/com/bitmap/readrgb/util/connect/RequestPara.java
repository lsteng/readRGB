package com.bitmap.readrgb.util.connect;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by kennethyeh on 16/4/29.
 */
public class RequestPara implements Serializable {

    public static final int HTTP_METHOD_GET 	    =1;
    public static final int HTTP_METHOD_POST	    =2;
    public static final int HTTP_METHOD_DELETE	    =3;
    public static final int HTTP_METHOD_PUT		    =4;
    public static final int HTTP_METHOD_POST_JSON   =5;
    public static final int HTTP_METHOD_GET_CloudFront =6;

    public int httpMethod 		= HTTP_METHOD_GET;
    public String requestURL	= "";
    public Uri.Builder parmsBuilder;
    public String postJSONContent = null;
//    public RequestParseType.ParseType parseType;
    public int httpStatus;
    public String responseStrContent;
    public Object responseObject = null;
}
