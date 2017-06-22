package com.bitmap.readrgb.util.connect.connect;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by kennethyeh on 16/2/24.
 */
public class HttpPostUtil {
    private static final String TAG = "HttpPostUtil";
    private InputStream dataFileImputStream;
    private BufferedReader br;
    private String returnStr = "";
    private RequestPara mRequrestPara;


    public RequestPara getHttpStatus(RequestPara requrestPara){
        int httpStatus =0;
        this.mRequrestPara = requrestPara;
        HttpURLConnection myURLConnection=null;
        try {
            String requestURL = requrestPara.requestURL;
            Uri.Builder parmsBuilder = requrestPara.parmsBuilder;
//			DebugLog.d(TAG, "requestURL:" + requestURL);
            URL myURL = new URL(requestURL);
            myURLConnection = (HttpURLConnection)myURL.openConnection();
            //		myURLConnection.setRequestProperty ("AuthorizeKey", GlobalParams.AUTHORIZE_KEY);
            myURLConnection.setRequestMethod("POST");
//            myURLConnection.setUseCaches(false);
            myURLConnection.setDoInput(true);
            myURLConnection.setDoOutput(true);

            myURLConnection.setConnectTimeout(ConnectionConstant.connectionTimeout);
            myURLConnection.setReadTimeout(ConnectionConstant.socketTimeout);

            if(parmsBuilder!=null){
                String query = parmsBuilder.build().getEncodedQuery();
//                DebugLog.d(TAG, "query:" + query);
                OutputStream os = myURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
            }

            httpStatus = myURLConnection.getResponseCode();
            // 讀取資料
            br = new BufferedReader(new InputStreamReader(openConnectionCheckRedirects(myURLConnection), "UTF-8"));
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
            Log.d(TAG, "SocketTimeoutException" + e.toString());  httpStatus = ConnectionConstant.ResError.CONNECT_TIMED_OUT;}
        catch (Exception e){Log.d(TAG,"getHttpStatus IOException : " + e.toString()); httpStatus = ConnectionConstant.ResError.CONNECT_ERROR;;
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
            closeData();
        }
        return mRequrestPara;
    }

    public String getReturnStr(){
        return returnStr;
    }

    public RequestPara getRequestPara(){
        return mRequrestPara;
    }

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
