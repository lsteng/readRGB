package com.bitmap.readrgb.lab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitmap.readrgb.mainActivity;

import java.util.Random;

/**
 * Created by 03070048 on 2017/8/24.
 */
public class LaunchPageInfo {
    private String TAG = "LaunchPageInfo";
    private Context mContext;

    private Bitmap bmp;
    private int rows, columns; //[列(row)]寬, [行(column)]高
    private TextView TV;
    private int[][] rgbValues;

    private int topLeftX, topLeftY;
    private int topRightX, topRightY;
    private int BottomLeftX, BottomLeftY;
    private int BottomRightX, BottomRightY;

    private final int keyTypeX = 0;
    private final int keyTypeY = 1;
    private int keyIX;
    private int keyIY;

    public static synchronized LaunchPageInfo getInstance(Context context){
        return new LaunchPageInfo(context);
    }

    public LaunchPageInfo(Context context){
        mContext = context;
    }

    public final static int hideDataType   = 0;
    public final static int decodeDataType = 1;
    public final static int decodelocalData= 2;
    public void setData(ImageView iv, TextView tv, int actionType, int[][] rgbValues, Bitmap bitmap){
        this.TV = tv;
        this.rgbValues = rgbValues;

        switch(actionType){
            case hideDataType:
            case decodeDataType:
                bmp = bitmap;
                break;
            case decodelocalData:
                String pathName = mContext.getExternalCacheDir().getAbsolutePath() + "/pic/hideLP.png";
                TV.append("pathName:" + pathName + "\n");
                bmp = BitmapFactory.decodeFile(pathName);
                break;

        }

        rows = bmp.getWidth();
        columns = bmp.getHeight();
        Log.d(TAG, "rows:"+rows +"_columns:"+ columns);
        TV.append("rows:"+rows +"_columns:"+ columns + "\n");

        topLeftX     = 0;
        topLeftY     = 0;
        topRightX    = 0;
        topRightY    = rows-1;
        BottomLeftX  = 0;
        BottomLeftY  = columns-1;
        BottomRightX = rows-1;
        BottomRightY = columns-1;

        switch(actionType){
            case decodeDataType:
            case decodelocalData:
                decodeData();
                break;
            case hideDataType:
                hideData();
                break;
        }
    }

    private void hideData(){
        Random random = new Random();
        int n = 7;
        int ranges = (int)Math.pow(2, n); //2的N次方

        //埋放X座標
        int startX = (rows<ranges)?random.nextInt(rows):random.nextInt(ranges);
        String x = Integer.toBinaryString(startX);
        x = Fillin0(x, n); //預設二進位長度7位，長度小於7則補0
        TV.append("X座標:"+startX+" -> "+x +"\n");
        getARGBcolor(topLeftX, topLeftY, topRightX, topRightY, x, -1);

        //埋放Y座標
        int startY = (columns<ranges)?random.nextInt(columns):random.nextInt(ranges);
        String y = Integer.toBinaryString(startY);
        y = Fillin0(y, n); //預設二進位長度7位，長度小於7則補0
        TV.append("Y座標:"+startY+" -> "+y +"\n");
        getARGBcolor(BottomLeftX, BottomLeftY, BottomRightX, BottomRightY, y, -1);
    }

    public void decodeData(){
        getARGBcolor(topLeftX, topLeftY, topRightX, topRightY, null, keyTypeX);
        getARGBcolor(BottomLeftX, BottomLeftY, BottomRightX, BottomRightY, null, keyTypeY);
    }

    private final int type_alpha = 0;
    private final int type_red   = 1;
    private final int type_green = 2;
    private final int type_blue  = 3;
    private int a1, r1, g1, b1;
    private int a2, r2, g2, b2;
    private void getARGBcolor(int posX1, int posY1, int posX2, int posY2, String hideKey, int keyType){
//        Log.d(TAG, posX1+"_"+posY1+":"+posX2+"_"+posY2);
        TV.append("pos:"+ posX1+"_"+posY1+":"+posX2+"_"+posY2 +"\n");

        int color1 = bmp.getPixel(posX1, posY1);
        int color2 = bmp.getPixel(posX2, posY2);

        a1 = (int)Color.alpha(color1);
        r1 = (int)Color.red(color1);
        g1 = (int)Color.green(color1);
        b1 = (int)Color.blue(color1);

        a2 = (int)Color.alpha(color2);
        r2 = (int)Color.red(color2);
        g2 = (int)Color.green(color2);
        b2 = (int)Color.blue(color2);

//        TV.append(a1 +"_"+ r1 +"_"+ g1 +"_"+ b1 +" "+
//                  a2 +"_"+ r2 +"_"+ g2 +"_"+ b2 +"\n");
        TV.append(r1 +"_"+ g1 +"_"+ b1 +" "+
                  r2 +"_"+ g2 +"_"+ b2 +"\n");

        if(hideKey==null){
            //Bitmap取alpha值，會都為255不透明，故不埋值
//        String a = Math.abs(a1-a2)==0 ? "00":FillIn0(Integer.toBinaryString(Math.abs(a1-a2)));
            String r = Math.abs(r1-r2)==0 ? "000":Fillin0(Integer.toBinaryString(Math.abs(r1-r2)), 2); //預設二進位長度2位，長度小於2則補0
            String g = Math.abs(g1-g2)==0 ? "00":Fillin0(Integer.toBinaryString(Math.abs(g1-g2)), 2); //預設二進位長度2位，長度小於2則補0
            String b = Math.abs(b1-b2)==0 ? "00":Fillin0(Integer.toBinaryString(Math.abs(b1-b2)), 2); //預設二進位長度2位，長度小於2則補0

            String binaryKey = r + g + b;
            int numberKey = Integer.valueOf(binaryKey, 2);
            TV.append("-> "+ binaryKey +" : "+ numberKey +"\n");

            switch(keyType){
                case keyTypeX:
                    keyIX = numberKey;
                    TV.append("keyIX-> "+ keyIX +"\n");
                    break;
                case keyTypeY:
                    keyIY = numberKey;
                    TV.append("keyIY-> "+ keyIY +"\n");
                    ((mainActivity)mContext).ProcessTime(mainActivity.endDecodeLP);
                    break;
            }
        }else{
            TV.append(hideKey +"\n");

            //Bitmap取alpha值，會都為255不透明，故不埋值
//        resetColor(type_alpha, a1, a2, charKey.substring(-1,-1));
            resetColor(type_red, r1, r2, hideKey.substring(0,3));
            resetColor(type_green, g1, g2, hideKey.substring(3,5));
            resetColor(type_blue, b1, b2, hideKey.substring(5,7));

            TV.append("----------------------------- \n");

//        rgbValues[posX1][posY1] = Color.argb(a1, r1, g1, b1);
//        rgbValues[posX2][posY2] = Color.argb(a2, r2, g2, b2);
            rgbValues[posX1][posY1] = Color.rgb(r1, g1, b1);
            rgbValues[posX2][posY2] = Color.rgb(r2, g2, b2);
        }
    }

    public int[][] getARGBvalues(){
        return rgbValues;
    };
    public int getKeyIX(){ return keyIX; }
    public int getKeyIY(){ return keyIY; }

    private void resetColor(int typeColor, int currentP, int nextP, String binaryKey){
        int to10Key = Integer.valueOf(binaryKey, 2);
        int diff = currentP - nextP;

        int compare = to10Key - Math.abs(diff);
        if(compare != 0){
            if(diff > 0){ //正，currentP > nextP
                if(compare > 0){ //需加大，原差距較小
                    if(nextP - Math.abs(compare) < 0){ //若nextP減少小於0，則currentP加入剩值
                        currentP = currentP + Math.abs(nextP - Math.abs(compare));
                        nextP = 0;
                    }else{
                        nextP = nextP - Math.abs(compare);
                    }
                }
                if(compare < 0){ //需縮小，原差距較大
                    if(nextP + Math.abs(compare) > 255){ //若next增加超過255，則currentP扣除剩值
                        currentP = currentP - ((nextP+Math.abs(compare))-255);
                        nextP = 255;
                    }else{
                        nextP = nextP + Math.abs(compare);
                    }
                }
            }

            if(diff < 0){ //負，currentP < nextP (算法與前者相反)
                if(compare > 0){ //需加大，原差距較小
                    if(nextP + Math.abs(compare) > 255){ //若next增加超過255，則currentP扣除剩值
                        currentP = currentP - ((nextP + Math.abs(compare))-255);
                        nextP = 255;
                    }else{
                        nextP = nextP + Math.abs(compare);
                    }
                }
                if(compare < 0){ //需縮小，原差距較大
                    if(nextP - Math.abs(compare) < 0){ //若nextP減少小於0，則currentP加入剩值
                        currentP = currentP + Math.abs(nextP - Math.abs(compare));
                        nextP = 0;
                    }else{
                        nextP = nextP - Math.abs(compare);
                    }
                }
            }

            if(diff == 0) { //currentP = nextP
                if (nextP + Math.abs(compare) > 255) { //若next增加超過255，則currentP扣除剩值
                    currentP = currentP - ((nextP + Math.abs(compare))-255);
                    nextP = 255;
                } else {
                    nextP = nextP + Math.abs(compare);
                }
            }
        }

        TV.append(binaryKey +":"+ to10Key +" -> " +currentP +" - "+ nextP +"\n");

        switch(typeColor){
            case type_alpha:
                a1 = currentP;
                a2 = nextP;
                break;
            case type_red:
                r1 = currentP;
                r2 = nextP;
                break;
            case type_green:
                g1 = currentP;
                g2 = nextP;
                break;
            case type_blue:
                b1 = currentP;
                b2 = nextP;
                break;
        }
    }

    //預設二進位長度，長度小於則補0
    private static String Fillin0(String s, int charLength){
        StringBuffer outBinaryStrBuf = new StringBuffer();
        for(int j=0; j<(charLength-s.length()); j++){
            outBinaryStrBuf.append("0");
        }
        return outBinaryStrBuf + s;
    }
}
