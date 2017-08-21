package com.bitmap.readrgb.util.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author:Jack Tony
 * @description  :
 * @web :
 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
 * http://www.cnblogs.com/kobe8/p/3877125.html
 *
 * @date  :2015年1月27日
 */
public class BitmapUtils {
    private final static String TAG = "BitmapUtils";

    /**
     * @description 计算图片的压缩比率
     *
     * @param options 参数
     * @param reqWidth 目标的宽度
     * @param reqHeight 目标的高度
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     *
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * @description 从Resources中加载图片
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长宽，目的是得到图片的宽高
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        options.inMutable = true;  //如果为true，则返回一个可以调用setpixel设置每个像素颜色的bitmap，否则调用setpixel会crash
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize); // 通过得到的bitmap，进一步得到目标大小的缩略图
    }

    /**
     * @description 从SD卡上加载图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inMutable = true;  //如果为true，则返回一个可以调用setpixel设置每个像素颜色的bitmap，否则调用setpixel会crash
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    //儲存圖片至檔案
//    public static void saveBitmapToFile(Context context, Bitmap bmp, TextView tv) {
    public static void saveBitmapToFile(Context context, Bitmap bmp) {
        FileOutputStream out = null;
        try {
//            File path = new File (getSDCardPath()[0] + "/Download/DataHiding");
//            path.mkdirs();
//            out = new FileOutputStream(new File(path, "/datahiding.jpg"));

//            out = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/datahiding.jpg"));
            Log.d(TAG, "DownloadsDir:" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

//            out = new FileOutputStream(new File(context.getFilesDir(), "/datahiding.jpg"));
            Log.d(TAG, "FilesDir:" + context.getFilesDir().getPath());

            String path = context.getExternalCacheDir().getAbsolutePath()+"/pic/";
            Log.d(TAG, "CacheDir:" + path);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = "datahiding.png";
            out = new FileOutputStream(path + fileName);

            //PNG is a lossless format, the compression factor (100) is ignored
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);

//            tv.append("save img: "+path +fileName);
            Log.d(TAG, "save img:"+path +fileName);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * 得到sdcard的路徑
     * @return 返回一個字符串數組 下標0:內置sdcard 下標1:外置sdcard
     */
    public static String[] getSDCardPath(){
        String[] sdCardPath= new  String[ 2 ];
        File sdFile=Environment.getExternalStorageDirectory();
        File[] files=sdFile.getParentFile().listFiles();
        for (File file:files){
            if (file.getAbsolutePath().equals(sdFile.getAbsolutePath())){ //得到外置sdcard
                sdCardPath[1]=sdFile.getAbsolutePath();
            } else if (file.getAbsolutePath().contains("sdcard")){ //得到內置sdcard
                sdCardPath[0]=file.getAbsolutePath();
            }
        }
        return  sdCardPath;
    }

    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos =  new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,  100 , baos); //質量壓縮方法，這裡100表示不壓縮，把壓縮後的數據存放到baos中
        int  options =  100 ;
        while  ( baos.toByteArray().length /  1024 > 100 ) {   //循環判斷如果壓縮後圖片是否大於100kb,大於繼續壓縮
            baos.reset(); //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos); //這裡壓縮options%，把壓縮後的數據存放到baos中
            options -=  10 ; //每次都減少10
        }
        ByteArrayInputStream isBm =  new  ByteArrayInputStream(baos.toByteArray()); //把壓縮後的數據baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm,  null ,  null ); //把ByteArrayInputStream數據生成圖片
        return  bitmap;
    }

    public static Bitmap setCompress(Bitmap image) {
        ByteArrayOutputStream baos =  new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,  100 , baos);
//        if ( baos.toByteArray().length /  1024 > 1024 ) { //判斷如果圖片大於1M,進行壓縮避免在生成圖片（BitmapFactory.decodeStream）時溢出
//            baos.reset(); //重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.PNG,  50 , baos); //這裡壓縮50%，把壓縮後的數據存放到baos中
//        }
        ByteArrayInputStream isBm =  new  ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts =  new  BitmapFactory.Options();
        //開始讀入圖片，此時把options.inJustDecodeBounds 設回true了
        newOpts.inJustDecodeBounds =  true ;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm,  null , newOpts);
        newOpts.inJustDecodeBounds =  false ;
        newOpts.inMutable = true;
        int  w = newOpts.outWidth;
        int  h = newOpts.outHeight;

        //現在主流手機比較多是800*480分辨率，所以高和寬我們設置為
//        float  hh = 800f; //這裡設置高度為800f
//        float  ww = 480f; //這裡設置寬度為480f
//        //縮放比。由於是固定比例縮放，只用高或者寬其中一個數據進行計算即可
        int  be =  1 ; //be=1表示不縮放
//        if (w > h && w > ww) { //如果寬度大的話根據寬度固定大小縮放
//            be = ( int ) (newOpts.outWidth / ww);
//        }  else if  (w < h && h > hh) { //如果高度高的話根據寬度固定大小縮放
//            be = ( int ) (newOpts.outHeight / hh);
//        }
//        if (be <= 0){
//            be = 1;
//        }
        newOpts.inSampleSize = be; //設置縮放比例

        //以999x999為標準，做等比例縮放
        int size = 1000;
        if (w > size || h > size) {
            int rate;
            if (w > h) {
                rate = w / size;
            } else {
                rate = h / size;
            }
            rate = (rate < 1) ? 1 : rate;

//            newOpts.inSampleSize = rate; //設置縮放比例
            newOpts.inSampleSize = calculateInSampleSize(newOpts, image.getWidth()/rate, image.getHeight()/rate);
        }


        //重新讀入圖片，注意此時已經把options.inJustDecodeBounds 設回false了
        isBm =  new  ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm,  null , newOpts);
//        return  compressImage(bitmap); //壓縮好比例大小後再進行質量壓縮
        return bitmap;
    }
}