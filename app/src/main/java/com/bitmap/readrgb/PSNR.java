package com.bitmap.readrgb;

/*
 *********************************************************************
 *
 *    Computes the PSNR between two images.
 *
 *********************************************************************
 */

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class PSNR {
    public static double log10(double x) {
        return Math.log(x)/Math.log(10);
    }

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

    public static void calculator (final Bitmap originalImg, final Bitmap stegoImg, final TextView logTV) {
        final int rows = stegoImg.getWidth(); //[列(row)]寬
        final int columns = stegoImg.getHeight(); //[行(column)]高

        Thread thread = new Thread() {
            @Override
            public void run() {
                Message msg = new Message();

                int[][] originalPixel = new int[rows][columns];
                int[][] stegoPixel = new int[rows][columns];
                double noise = 0; //像素差平方和(雜訊)

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
                        if(r1 != r2){
                            noise = noise + Math.pow((originalPixel[i][j] - stegoPixel[i][j]), 2);

                            Log.d("PSNR", "originalPixel["+i+","+j+"]: "+originalPixel[i][j]);
                            Log.d("PSNR", "stegoPixel["+i+","+j+"]: "+stegoPixel[i][j]);
                        }
                    }
                }

                double mse = noise / (rows * columns); // Mean square error 均方根差
                double psnr = 10 * log10(255 * 255 / mse); // Peak Signal to Noise Ratio 峰值信號雜訊比(dB)
                Log.d("PSNR", "noise: "+noise);
                Log.d("PSNR", "mse: "+mse);
                Log.d("PSNR", "psnr: "+psnr);
                //logTV.setText("mse: "+mse +"\n"+ "psnr: "+psnr);

                msg.what = msgID; //設定 Handler 要接收的事件ID
                mHandler.sendMessage (msg) ; //送出事件訊息
            }
        };
        thread.start();
    }

    /* 自定Handler
    所有透過 myMessageHandler.sendMessage 方法的 事件訊息
    都會到這集合並至 public void handleMessage ( Message msg ) 中執行
    */
    public final static int msgID = 30;
    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            if(msg.what == msgID) {
//                ((mainActivity)mContext).ProcessTime(mainActivity.decodeStart);
//            }
        }
    };
}
