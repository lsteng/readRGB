package com.bitmap.readrgb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 03070048 on 2016/9/12.
 */
public class DataDecoding {
    private String TAG = "DataDecoding";
    private Context mContext;

    private Bitmap bmp;
//    private BitmapARGB mBitmapARGB;
//    private int[][] rgbValues;
    private int rows, columns; //[列(row)]寬, [行(column)]高
    private int keyCount;
    private TextView TV, dTV;

    public static synchronized DataDecoding getInstance(Context context){
        return new DataDecoding(context);
    }

    public DataDecoding(Context context){
        mContext = context;
    }

    public void setData(ImageView iv, TextView tv){
        this.TV = tv;

//        BitmapDrawable mDrawable =  (BitmapDrawable) iv.getDrawable();
//        Bitmap bmp = mDrawable.getBitmap();

        String pathName = mContext.getExternalCacheDir().getAbsolutePath()+"/pic/datahiding.png";
        TV.append("pathName:"+pathName +"\n");
        bmp = BitmapFactory.decodeFile(pathName);
//        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.splash);
//        Bitmap bmp = BitmapUtils.decodeSampledBitmapFromFile(pathName, 512, 512);
//        iv.setImageBitmap(bmp);

        rows = bmp.getWidth();
        columns = bmp.getHeight();
//        rgbValues = new int[bmp.getWidth()][bmp.getHeight()];
        Log.d(TAG, "rows:"+rows +"_columns:"+ columns);

//        mBitmapARGB = new BitmapARGB(mContext);
//        mBitmapARGB.getARGB(bmp, rgbValues, BitmapARGB.decode);

        ((mainActivity)mContext).ProcessTime(mainActivity.decodeStart);
    }

    public void decodeData(TextView dtv){
        this.dTV = dtv;

        TV.append("rows:"+rows +"_columns:"+ columns +"\n");

//        keyCount = (keyCount<0) ? 16:keyCount;

        loadData(keySize, 0); //取字數長度
        TV.append("key length:"+keyCount +"\n");

        int cornerCount = keyCount/4*2;
        int remainder = keyCount % 4;
        switch (remainder){ //若隱藏資料不是4的倍數，取餘數做埋放
            case 0:
                loadData(TopLeft, cornerCount);
                loadData(TopRight, cornerCount);
                loadData(BottomRight, cornerCount);
                loadData(BottomLeft, cornerCount);
                break;
            case 1:
                loadData(TopLeft, cornerCount);
                loadData(TopRight, cornerCount+1);
                loadData(BottomRight, cornerCount);
                loadData(BottomLeft, cornerCount);
                break;
            case 2:
                loadData(TopLeft, cornerCount);
                loadData(TopRight, cornerCount+1);
                loadData(BottomRight, cornerCount+1);
                loadData(BottomLeft, cornerCount);
                break;
            case 3:
                loadData(TopLeft, cornerCount);
                loadData(TopRight, cornerCount+1);
                loadData(BottomRight, cornerCount+1);
                loadData(BottomLeft, cornerCount+1);
                break;
        }

        ((mainActivity)mContext).ProcessTime(mainActivity.endCount);
    }

    private final static int keySize     = 0;
    private final static int TopLeft     = 1;
    private final static int TopRight    = 2;
    private final static int BottomLeft  = 3;
    private final static int BottomRight = 4;
    private void loadData(int type, int length){
        int fixPosX, fixPosY;
        switch(type){
            case keySize: //從左2上至右2上
                fixPosY = 1;
                int sX1 = 1;
                int sX2 = sX1+1;
                keyCount = getKeyLength(sX1, fixPosY, sX2, fixPosY);
                break;

            case TopLeft: //從左上至右上
                fixPosY = 0;
                for(int i=0; i<length; i+=2){
                    int X1 = i;
                    int X2 = X1+1;
                    getARGBcolor(X1, fixPosY, X2, fixPosY);
                }
                break;
            case TopRight: //從右上至右下
                fixPosX = rows-1;
                for(int i=0; i<length; i+=2){
                    int Y1 = i;
                    int Y2 = Y1+1;
                    getARGBcolor(fixPosX, Y1, fixPosX, Y2);
                }
                break;
            case BottomRight: //從右下至左下
                fixPosY = columns-1;
                for(int i=0; i<length; i+=2){
                    int X1 = rows-i-1;
                    int X2 = X1-1;
                    getARGBcolor(X1, fixPosY, X2, fixPosY);
                }
                break;
            case BottomLeft: //從左下至左上
                fixPosX = 0;
                for(int i=0; i<length; i+=2){
                    int Y1 = columns-i-1;
                    int Y2 = Y1-1;
                    getARGBcolor(fixPosX, Y1, fixPosX, Y2);

//                    if(i>=length-2){
//                        ((mainActivity)mContext).ProcessTime(mainActivity.endCount);
//                    }
                }
                break;
        }
    }

    private int getKeyLength(int posX1, int posY1, int posX2, int posY2){
//        TV.append("keySize pos:"+ posX1+"_"+posY1 +":"+ posX2+"_"+posY2 +"\n");

        int color1 = bmp.getPixel(posX1, posY1);
        int color2 = bmp.getPixel(posX2, posY2);

        int r1 = (int)Color.red(color1);
        int g1 = (int)Color.green(color1);
        int b1 = (int)Color.blue(color1);

        int r2 = (int)Color.red(color2);
        int g2 = (int)Color.green(color2);
        int b2 = (int)Color.blue(color2);

        TV.append(r1 +"_"+ g1 +"_"+ b1 +" "+
                r2 +"_"+ g2 +"_"+ b2 +"\n");

        String r = Math.abs(r1-r2)==0 ? "000":FillIn0(Integer.toBinaryString(Math.abs(r1-r2)));
        String g = Math.abs(g1-g2)==0 ? "00":FillIn0(Integer.toBinaryString(Math.abs(g1-g2)));
        String b = Math.abs(b1-b2)==0 ? "00":FillIn0(Integer.toBinaryString(Math.abs(b1-b2)));

        String binaryKey = r + g + b;
        TV.append("-> "+ binaryKey +"\n");
        return Integer.valueOf(binaryKey, 2);
    }

    private void getARGBcolor(int posX1, int posY1, int posX2, int posY2){
//        Log.d(TAG, posX1+"_"+posY1+":"+posX2+"_"+posY2);

//        int color1 = rgbValues[posX1][posY1];
//        int color2 = rgbValues[posX2][posY2];
        int color1 = bmp.getPixel(posX1, posY1);
        int color2 = bmp.getPixel(posX2, posY2);

        int a1 = (int) Color.alpha(color1);
        int r1 = (int)Color.red(color1);
        int g1 = (int)Color.green(color1);
        int b1 = (int)Color.blue(color1);

        int a2 = (int)Color.alpha(color2);
        int r2 = (int)Color.red(color2);
        int g2 = (int)Color.green(color2);
        int b2 = (int)Color.blue(color2);

        TV.append(a1 +"_"+ r1 +"_"+ g1 +"_"+ b1 +" "+
                  a2 +"_"+ r2 +"_"+ g2 +"_"+ b2 +"\n");

        //Bitmap取alpha值，會都為255不透明，故不埋值
//        String a = Math.abs(a1-a2)==0 ? "00":FillIn0(Integer.toBinaryString(Math.abs(a1-a2)));
        String r = Math.abs(r1-r2)==0 ? "000":FillIn0(Integer.toBinaryString(Math.abs(r1-r2)));
        String g = Math.abs(g1-g2)==0 ? "00":FillIn0(Integer.toBinaryString(Math.abs(g1-g2)));
        String b = Math.abs(b1-b2)==0 ? "00":FillIn0(Integer.toBinaryString(Math.abs(b1-b2)));

        String binaryKey = r + g + b;
        int ascii = Integer.valueOf(binaryKey, 2);
        String key = Character.toString((char)ascii);

        TV.append("-> "+ binaryKey +" : "+ ascii +"\n");
        dTV.append(key);

//        Log.d(TAG, "----------------------------- \n");
    }

    //預設二進位長度2位，長度小於2則補0
    private String FillIn0(String s){
        int totalLength = 2;

        StringBuffer outBinaryStrBuf = new StringBuffer();
        for(int j=0; j<(totalLength -s.length()); j++){
            outBinaryStrBuf.append("0");
        }

        return outBinaryStrBuf + s;
    }
}
