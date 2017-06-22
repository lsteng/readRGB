package com.bitmap.readrgb.util.connect;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by kennethyeh on 16/4/29.
 */
public class HttpGetUtil {
    private static final String TAG = "HttpGetUtil";
    private InputStream dataFileImputStream;
    private BufferedReader br;
    private String returnStr = "";
    private RequestPara mRequrestPara;


    public RequestPara getHttpStatus(RequestPara requrestPara){
        int httpStatus =0;
        HttpURLConnection myURLConnection=null;
        this.mRequrestPara = requrestPara;
        try {
            String requestURL = requrestPara.requestURL;
//			DebugLog.d(TAG, "requestURL:" + requestURL);
            URL myURL = new URL(requestURL);
            myURLConnection = (HttpURLConnection)myURL.openConnection();
            //		myURLConnection.setRequestProperty ("AuthorizeKey", GlobalParams.AUTHORIZE_KEY);
            myURLConnection.setRequestMethod("GET");
            if(requrestPara.httpMethod==RequestPara.HTTP_METHOD_GET_CloudFront){
                Log.d(TAG,"content:"+requrestPara.postJSONContent);
//                String encrypt = getSrcEncrypt(requrestPara.postJSONContent);// AES 內文加密
//                Log.d(TAG,"en:"+encrypt);
//                DebugLog.d(TAG,"decrypt:"+ getDencrypt(encrypt));
//                myURLConnection.setRequestProperty("X-ES-Request", encrypt);
//                DebugLog.d(TAG, "request header X-ES-Request:\n"+myURLConnection.getRequestProperty("X-ES-Request"));
                myURLConnection.setRequestProperty("Cache-Control", "no-cache");
            }

            //      myURLConnection.setUseCaches(false);
            //		myURLConnection.setDoInput(true);
            //		myURLConnection.setDoOutput(false);

            myURLConnection.setConnectTimeout(ConnectionConstant.connectionTimeout);
            myURLConnection.setReadTimeout(ConnectionConstant.socketTimeout);

            httpStatus = myURLConnection.getResponseCode();
            Log.i(TAG, "httpStatus:" + httpStatus);
            if(requrestPara.httpMethod==RequestPara.HTTP_METHOD_GET_CloudFront){
                Log.d(TAG, "response header Age:"+myURLConnection.getHeaderField("Age"));
                Log.d(TAG, "response header X-Cache:"+myURLConnection.getHeaderField("X-Cache"));
                Log.i(TAG, "from CloudFront ---------------");
            }
            // 讀取資料
            br = new BufferedReader(new InputStreamReader(openConnectionCheckRedirects(myURLConnection), "UTF-8"));
            httpStatus = redirectStatus;
//			br = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                response.append(line + "\n");
            }
            returnStr = response.toString();
            mRequrestPara.responseStrContent =returnStr;
        }
        catch (SocketTimeoutException e){
            Log.d(TAG,"SocketTimeoutException" + e.toString());  httpStatus = ConnectionConstant.ResError.CONNECT_TIMED_OUT;}
        catch (Exception e){Log.d(TAG,"getHttpStatus IOException : " + e.toString());
            httpStatus = ConnectionConstant.ResError.CONNECT_ERROR;;
            try {
                br = new BufferedReader(new InputStreamReader(myURLConnection.getErrorStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    response.append(line + "\n");
                }
                returnStr = response.toString();
                mRequrestPara.responseStrContent =returnStr;
            }
            catch (Exception e1) {}
        }
        finally{
            Log.d(TAG, "httpStatus:" + httpStatus);
            mRequrestPara.httpStatus = httpStatus;
//            DebugLog.d(TAG, "returnStr:" + returnStr);
            closeData();
        }
        return mRequrestPara;
    }

    /**
     * AES 內文加密
     * **/
//    private String getSrcEncrypt(String sSrc){
//        String encryptStr = "";
//        try {
//            AESCrypt mAESCrypt = new AESCrypt();
//            encryptStr = mAESCrypt.encrypt(sSrc);
////            char[] cArray = encryptStr.toCharArray();
////            StringBuffer sb = new StringBuffer("");
////            for(char c:cArray){
////                sb.append(c);
////                DebugLog.d(TAG, ""+c);
////            }
////            encryptStr = sb.toString().trim();
//        } catch (Exception e) {}
//        return encryptStr;
//    }
//
//    private String getDencrypt(String sSrc){
//        String decryptStr = "";
//        try {
//            AESCrypt mAESCrypt = new AESCrypt();
//            decryptStr = mAESCrypt.decrypt(sSrc);
//        } catch (Exception e) {}
//        return decryptStr;
//    }

    public String getReturnStr(){
        return returnStr;
    }

    public RequestPara getRequestPara(){
        return mRequrestPara;
    }

    private int redirectStatus = 200;
    private InputStream openConnectionCheckRedirects(URLConnection c) throws IOException {
        boolean redir;
        int redirects = 0;
        InputStream in = null;
        do {
            if (c instanceof HttpURLConnection) {
                ((HttpURLConnection) c).setInstanceFollowRedirects(false);
            }
            // We want to open the input stream before getting headers
            // because getHeaderField() et al swallow IOExceptions.
            in = c.getInputStream();

            redir = false;
            if (c instanceof HttpURLConnection) {
                HttpURLConnection http = (HttpURLConnection) c;
                int stat = http.getResponseCode();
                redirectStatus = stat;
                if (stat >= 300 && stat <= 307 && stat != 306 &&
                        stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
                    URL base = http.getURL();
                    String loc = http.getHeaderField("Location");
                    URL target = null;
                    if (loc != null) {
                        target = new URL(base, loc);
                    }
                    http.disconnect();

                    // Redirection should be allowed only for HTTP and HTTPS
                    // and should be limited to 5 redirections at most.
                    if (target == null || !(target.getProtocol().equals("http")
                            || target.getProtocol().equals("https"))
                            || redirects >= 5) {
                        throw new SecurityException("illegal URL redirect");
                    }
                    redir = true;
                    c = target.openConnection();
                    redirects++;
                }
            }
        }
        while (redir);
        return in;
    }

    private void closeData(){
        if(br!=null){
            try {
                br.close();
                br=null;
            }
            catch (IOException e) {;}
        }
        if(dataFileImputStream!=null){
            try {
                dataFileImputStream.close();
                dataFileImputStream = null;
            }
            catch (IOException e) {;}
        }
    }
}
