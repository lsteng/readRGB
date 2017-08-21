package com.bitmap.readrgb.util.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import com.bitmap.readrgb.mainActivity;

/**
 * Created by 03070048 on 2016/9/10.
 */
public class BitmapARGB {
    private String TAG = "BitmapARGB";
    //a Bitmap that will act as a handle to the image
    private Bitmap mBitmap;
    //an integer array that will store ARGB pixel values
    private int[][] argbValues;
    private Context mContext;

    private int rows; //[列(row)]寬
    private int columns; //[行(column)]高

    public final static int done   = 0; //自訂事件ID
    public final static int load   = 1;
    public final static int hide   = 2;
    public final static int decode = 3;
    public final static int save   = 4;
    public String ResultMsg = "";  //宣告一變數承接Thread要向外傳的值。

    public static synchronized BitmapARGB getInstance(Context context){
        return new BitmapARGB(context);
    }

    public BitmapARGB(Context context){
        mContext = context;
    }

//    public void getARGB(final Bitmap bmp, final int[][] rgbValues, final int msgID) {
    public void getARGB(final Bitmap bmp, final int msgID) {
        mBitmap = bmp;
        rows = bmp.getWidth(); //[列(row)]寬
        columns = bmp.getHeight(); //[行(column)]高

        Thread thread = new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                int[][] rgbValues = new int[rows][columns];

                //get the ARGB value from each pixel of the image and store it into the array
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        //This is a great opportunity to filter the ARGB values
                        rgbValues[i][j] = bmp.getPixel(i, j);

//                        int color = rgbValues[i][j];
//                        int r = Color.red(color);
//                        int g = Color.green(color);
//                        int b = Color.blue(color);
////                        tv.append("pos:" + i + "," + j +
////                                "  R:" + r +
////                                "  G:" + g +
////                                "  B:" + b +
////                                "  hex:" + Integer.toHexString(color) +
////                                //"  Char:"+Character.toString((char)(r+33))+Character.toString((char)(g+33))+Character.toString((char)(b+33)) +
////                                "\n");
                        //ResultMsg = Result ; //將接收回來的字串放至 ResultMsg 變數中。
                    }
                }
                //Do something with the ARGB value array
                argbValues = rgbValues;
                msg.what = msgID; //設定 Handler 要接收的事件ID
                getARGBHandler.sendMessage (msg) ; //送出事件訊息
            }
        };
        thread.start();
    }
    /* 自定Handler
    所有透過 myMessageHandler.sendMessage 方法的 事件訊息
    都會到這集合並至 public void handleMessage ( Message msg ) 中執行
    */
    public Handler getARGBHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case load:
                    ((mainActivity)mContext).ProcessTime(mainActivity.endCount);
                    break;
                case hide:
                    ((mainActivity)mContext).ProcessTime(mainActivity.hideStart);
                    break;
                case decode:
                    ((mainActivity)mContext).ProcessTime(mainActivity.decodeStart);
                    break;
            }
        }
    };

    public int[][] getARGBvalues(){
        return argbValues;
    }
    public Bitmap getBitmap(){
        return mBitmap;
    };

    public void setRGB(final int[][] rgbValues, final int msgID) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                int count = 0;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        int color = rgbValues[i][j];

//                        int a = (int)Color.alpha(color);
                        int r = (int)Color.red(color);
                        int g = (int)Color.green(color);
                        int b = (int)Color.blue(color);

//                        Log.d("Pixel Value", "color: "+color);
//                        Log.d("Pixel Value", "Color.rgb: "+Color.rgb(r,g,b));
//                        rgbValues[i][j] = Color.rgb(r,g,b);

//                        int color1 = mBitmap.getPixel(i, j);;
//                        int r1 = Color.red(color1);
//                        int g1 = Color.green(color1);
//                        int b1 = Color.blue(color1);
//
//                        if(r != r1 || g != g1 || b != b1){
//                            count = count+1;
//                            Log.d(TAG, "no. "+count);
//                            Log.d(TAG, "originalPixel["+i+","+j+"]: "+color);
//                            Log.d(TAG, "stegoPixel["+i+","+j+"]: "+color1);
//                        }

//                        bmp.setPixel(i, j, Color.argb(a, r, g, b));
                        mBitmap.setPixel(i, j, Color.rgb(r, g, b));
                    }
                }

                msg.what = msgID; //設定 Handler 要接收的事件ID
                setRGBHandler.sendMessage (msg) ; //送出事件訊息
            }
        };
        thread.start();
    }

    private Handler setRGBHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case done:
                    ((mainActivity)mContext).ProcessTime(mainActivity.endCount);
                    break;
                case save:
                    ((mainActivity)mContext).ProcessTime(mainActivity.endSaveCount);
                    break;
            }
        }
    };
}
