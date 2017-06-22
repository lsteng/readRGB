package com.bitmap.readrgb.util.connect;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;


/**
 * Created by kennethyeh on 16/4/29.
 */
public class WebServiceTask extends AsyncTask<Void, Integer, Integer> {
    private Handler requestHandler;
    private RequestPara requrestPara;

    public WebServiceTask(Handler requestHandler, RequestPara requrestPara){

        this.requestHandler	= requestHandler;
        this.requrestPara 	= requrestPara;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Integer doInBackground(Void... params) {

        int httpStatus=0;
        int requestMethod = requrestPara.httpMethod;

        if(requestMethod == RequestPara.HTTP_METHOD_GET ){
            HttpGetUtil connectGet = new HttpGetUtil();
            requrestPara = connectGet.getHttpStatus(requrestPara);
        }
        if(requestMethod == RequestPara.HTTP_METHOD_GET_CloudFront ){
            HttpGetUtil connectGet = new HttpGetUtil();
            requrestPara = connectGet.getHttpStatus(requrestPara);
        }

        if(requestMethod == RequestPara.HTTP_METHOD_POST ){
            HttpPostUtil connectPost = new HttpPostUtil();
            requrestPara = connectPost.getHttpStatus(requrestPara);
        }

//        if(requestMethod == RequestPara.HTTP_METHOD_POST_JSON ){
//            HttpPostJSONUtil mHttpPostJSONUtil = new HttpPostJSONUtil();
//            requrestPara = mHttpPostJSONUtil.getHttpStatus(requrestPara);
//        }

        if(requestMethod == RequestPara.HTTP_METHOD_DELETE ){
//            HttpDeleteUtil connectDelete = new HttpDeleteUtil();
//            httpStatus = connectDelete.getHttpStatus(requrestPara);
//            if(httpStatus == HttpStatus.SC_OK){
//                requrestPara = connectDelete.getequrestPara();
//            }else{
//                requrestPara = connectDelete.getequrestPara();
//            }
        }

        if(requestMethod == RequestPara.HTTP_METHOD_PUT ){
//            HttpPutUtil connectPut = new HttpPutUtil();
//            httpStatus = connectPut.getHttpStatus(requrestPara);
//            if(httpStatus == HttpStatus.SC_OK){
//                requrestPara = connectPut.getequrestPara();
//            }else{
//                requrestPara = connectPut.getequrestPara();
//            }
        }
        return httpStatus;
    }

    @Override
    protected void onPostExecute(Integer status) {

        Message msg = Message.obtain(); // Creates an new Message instance
//        msg.what = ApiRequestControl.PARSE_RESPONSE;
        msg.obj = requrestPara;// Put the ArrayList into Message, into "obj" field.
        msg.setTarget(requestHandler); // Set the Handler
        msg.sendToTarget(); //Send the message
    }
}
