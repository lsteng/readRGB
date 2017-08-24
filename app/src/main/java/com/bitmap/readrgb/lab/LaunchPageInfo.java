package com.bitmap.readrgb.lab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitmap.readrgb.mainActivity;

/**
 * Created by 03070048 on 2017/8/24.
 */
public class LaunchPageInfo {
    private String TAG = "LaunchPageInfo";
    private Context mContext;

    private Bitmap bmp;
    private int rows, columns; //[列(row)]寬, [行(column)]高
    private TextView TV;

    public static synchronized LaunchPageInfo getInstance(Context context){
        return new LaunchPageInfo(context);
    }

    public LaunchPageInfo(Context context){
        mContext = context;
    }

    public final static int hideDataType   = 0;
    public final static int decodeDataType = 1;
    public void setData(ImageView iv, TextView tv, int actionType){
        this.TV = tv;

        BitmapDrawable mDrawable =  (BitmapDrawable) iv.getDrawable();
        bmp = mDrawable.getBitmap();

//        String pathName = mContext.getExternalCacheDir().getAbsolutePath()+"/pic/datahiding.png";
//        TV.append("pathName:"+pathName +"\n");
//        bmp = BitmapFactory.decodeFile(pathName);

        rows = bmp.getWidth();
        columns = bmp.getHeight();
        Log.d(TAG, "rows:"+rows +"_columns:"+ columns);

        switch(actionType){
            case decodeDataType:
                decodeData();
                break;
            case hideDataType:
                break;
        }
    }

    private void hideData(String keys, int pos){

    }

    public void decodeData(){
        TV.append("rows:"+rows +"_columns:"+ columns +"\n");

        int topLeftX     = 0;
        int topLeftY     = 0;
        int topRightX    = 0;
        int topRightY    = rows-1;
        int BottomLeftX  = 0;
        int BottomLeftY  = columns-1;
        int BottomRightX = rows-1;
        int BottomRightY = columns-1;

        getARGBcolor(topLeftX, topLeftY, topRightX, topRightY);
        getARGBcolor(BottomLeftX, BottomLeftY, BottomRightX, BottomRightY);

        ((mainActivity)mContext).ProcessTime(mainActivity.endCount);
    }

    private void getARGBcolor(int posX1, int posY1, int posX2, int posY2){
//        Log.d(TAG, posX1+"_"+posY1+":"+posX2+"_"+posY2);
        TV.append("pos:"+ posX1+"_"+posY1+":"+posX2+"_"+posY2 +"\n");

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
        int numberKey = Integer.valueOf(binaryKey, 2);
        TV.append("-> "+ binaryKey +" : "+ numberKey +"\n");

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
