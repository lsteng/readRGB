package com.bitmap.readrgb;

/*
 *********************************************************************
 *
 *    Computes the PSNR between two images.
 *
 *********************************************************************
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class PSNR {
//    public static double log10(double x) {
//        return Math.log(x)/Math.log(10);
//    }

//    public static void main (String[] args) {
//        int     nrows, ncols;
//        int     img1[][], img2[][];
//        double  peak, signal, noise, mse;
//
//        if (args.length != 4) {
//            System.out.println("Usage: Psnr <nrows> <ncols> <img1> <img2>");
//            return;
//        }
//        nrows = Integer.parseInt(args[0]);
//        ncols = Integer.parseInt(args[1]);
//        img1 = new int[nrows][ncols];
//        img2 = new int[nrows][ncols];
////        ArrayIO.readByteArray(args[2], img1, nrows, ncols);
////        ArrayIO.readByteArray(args[3], img2, nrows, ncols);
//
//        signal = noise = peak = 0;
//        for (int i=0; i<nrows; i++) {
//            for (int j=0; j<ncols; j++) {
//                signal += img1[i][j] * img1[i][j];
//                noise += (img1[i][j] - img2[i][j]) * (img1[i][j] - img2[i][j]);
//                if (peak < img1[i][j])
//                    peak = img1[i][j];
//            }
//        }
//
//        mse = noise/(nrows*ncols); // Mean square error 均方根差
//        System.out.println("MSE: " + mse);
//        System.out.println("SNR: " + 10*log10(signal/noise));
//        System.out.println("PSNR(max=255): " + (10*log10(255*255/mse)));
//        System.out.println("PSNR(max=" + peak + "): " + 10*log10((peak*peak)/mse));
//    }

    private String TAG = "PSNR";
    private Context mContext;

    public static synchronized PSNR getInstance(Context context){
        return new PSNR(context);
    }

    public PSNR(Context context){
        mContext = context;
    }

    public void calculator (final Bitmap originalImg, final Bitmap stegoImg, final TextView logTV) {
        final int rows = stegoImg.getWidth(); //[列(row)]寬
        final int columns = stegoImg.getHeight(); //[行(column)]高

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                Message msg = new Message();

                int[][] originalPixel = new int[rows][columns];
                int[][] stegoPixel = new int[rows][columns];
                double noise = 0; //像素差平方和(雜訊)
                int count = 0;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        originalPixel[i][j] = originalImg.getPixel(i, j);
                        stegoPixel[i][j] = stegoImg.getPixel(i, j);

                        int color1 = originalPixel[i][j];
                        int r1 = Color.red(color1);
                        int g1 = Color.green(color1);
                        int b1 = Color.blue(color1);

                        int color2 = stegoPixel[i][j];
                        int r2 = Color.red(color2);
                        int g2 = Color.green(color2);
                        int b2 = Color.blue(color2);

//                        if(originalPixel[i][j] != stegoPixel[i][j]){
                        if(r1 != r2 || g1 != g2 || b1 != b2){
                            noise = noise + Math.pow((originalPixel[i][j] - stegoPixel[i][j]), 2);

                            count = count+1;
                            Log.d(TAG, "no. "+count);
                            Log.d(TAG, "originalPixel["+i+","+j+"]: "+originalPixel[i][j]);
                            Log.d(TAG, "stegoPixel["+i+","+j+"]: "+stegoPixel[i][j]);
                        }
                    }
                }

                double mse = noise / (rows * columns); // Mean square error 均方根差
                // FrameSize是影像長度x寬度x通道數（灰階為1，彩色為3）
                // 通常PSNR值越高表示品質越好，一般而言當PSNR的值<30db時，代表以人的肉眼看起來是不能容忍的範圍。因此大部分PSNR值皆要>30db。
                // 但PSNR高，並不代表影像品質一定好，有時候還是必須靠人的肉眼輔助來判斷影像的品質才較為正確
//                double psnr = 10 * log10(255 * 255 / mse); // Peak Signal to Noise Ratio 峰值信號雜訊比(dB)
                double psnr = 10 * Math.abs(Math.log10((255*255)/mse)); // Peak Signal to Noise Ratio 峰值信號雜訊比(dB)
                Log.d(TAG, "noise: "+noise);
                Log.d(TAG, "mse: "+mse);
                Log.d(TAG, "psnr: "+psnr);
                logTV.setText("mse: "+mse +"\n"+ "psnr: "+psnr);

//                msg.what = msgID; //設定 Handler 要接收的事件ID
//                mHandler.sendMessage (msg) ; //送出事件訊息
//            }
//        };
//        thread.start();
    }

    /* 自定Handler
    所有透過 myMessageHandler.sendMessage 方法的 事件訊息
    都會到這集合並至 public void handleMessage ( Message msg ) 中執行
    */
    private final int msgID = 30;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            if(msg.what == msgID) {
//                ((mainActivity)mContext).ProcessTime(mainActivity.decodeStart);
//            }
        }
    };
}
