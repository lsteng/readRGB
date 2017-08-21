package com.bitmap.readrgb.lab;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by 03070048 on 2016/9/10.
 */
public class DataHiding {
    private String TAG = "DataHiding";
    private Context mContext;

    private TextView TV;

    public static synchronized DataHiding getInstance(Context context){
        return new DataHiding(context);
    }

    public DataHiding(Context context){
        mContext = context;
    }

    private int[][] rgbValues;
    private int rows, columns; //[列(row)]寬, [行(column)]高
    public void setData(String key, int[][] rgbValues, int rows, int columns, TextView tv){
        this.rgbValues = rgbValues;
        this.rows = rows;
        this.columns = columns;
        this.TV = tv;

        int totalCount = key.length();
        String[] keys = new String[totalCount];
        for(int i=0; i<key.length(); i++){
            char c = key.charAt(i);
            int a = (int) c;
            String b = Integer.toBinaryString(a);

//            int charLength = 7; //預設二進位長度7位，長度小於7則補0
//            StringBuffer outBinaryStrBuf = new StringBuffer();
//            for(int j=0; j<(charLength - b.length()); j++){
//                outBinaryStrBuf.append("0");
//            }
//            keys[i] = outBinaryStrBuf + b;
            keys[i] = Fillin0(b, 7); //預設二進位長度7位，長度小於7則補0
            Log.d(TAG, "key:"+c +"_ascii:"+a +"_binary:"+keys[i]);
        }

        Log.d(TAG, "rows:"+rows +"_columns:"+ columns);
        tv.append("rows:"+rows +"_columns:"+ columns +"\n");

        //埋放字數長度
        String[] keyLength = new String[1];
        keyLength[0] = Integer.toBinaryString(totalCount);
//        int charLength = 7; //預設二進位長度7位，長度小於7則補0
//        StringBuffer outBinaryStrBuf = new StringBuffer();
//        for(int j=0; j<(charLength - keyLength[0].length()); j++){
//            outBinaryStrBuf.append("0");
//        }
//        keyLength[0] = outBinaryStrBuf + keyLength[0];
        keyLength[0] = Fillin0(keyLength[0], 7); //預設二進位長度7位，長度小於7則補0
        tv.append("key length:"+totalCount+" -> "+keyLength[0] +"\n");
        hideData(keySize, 0, keyLength, 0);

        int cornerCount = totalCount/4*2;
        int remainder = totalCount % 4;
        switch (remainder){ //若隱藏資料不是4的倍數，取餘數做埋放
            case 0:
                hideData(TopLeft, cornerCount, keys, 0);
                hideData(TopRight, cornerCount, keys, cornerCount/2);
                hideData(BottomRight, cornerCount, keys, cornerCount/2*2);
                hideData(BottomLeft, cornerCount, keys, cornerCount/2*3);
                break;
            case 1:
                hideData(TopLeft, cornerCount, keys, 0);
                hideData(TopRight, cornerCount+1, keys, cornerCount/2);
                hideData(BottomRight, cornerCount, keys, (cornerCount/2*2)+1);
                hideData(BottomLeft, cornerCount, keys, (cornerCount/2*3)+1);
                break;
            case 2:
                hideData(TopLeft, cornerCount, keys, 0);
                hideData(TopRight, cornerCount+1, keys, cornerCount/2);
                hideData(BottomRight, cornerCount+1, keys, (cornerCount/2*2)+1);
                hideData(BottomLeft, cornerCount, keys, (cornerCount/2*3)+2);
                break;
            case 3:
                hideData(TopLeft, cornerCount, keys, 0);
                hideData(TopRight, cornerCount+1, keys, cornerCount/2);
                hideData(BottomRight, cornerCount+1, keys, (cornerCount/2*2)+1);
                hideData(BottomLeft, cornerCount+1, keys, (cornerCount/2*3)+2);
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

    private final static int keySize     = 0;
    private final static int TopLeft     = 1;
    private final static int TopRight    = 2;
    private final static int BottomLeft  = 3;
    private final static int BottomRight = 4;
    private void hideData(int type, int length, String[] keys, int startPos){
        int fixPosX, fixPosY;
        switch(type){
            case keySize: //從左2上至右2上
                fixPosY = 1;
                int sX1 = 1;
                int sX2 = sX1+1;
                getARGBcolor(keys[0], sX1, fixPosY, sX2, fixPosY);
                break;

            case TopLeft: //從左上至右上
                fixPosY = 0;
                for(int i=0; i<length; i+=2){
                    int X1 = i;
                    int X2 = X1+1;
                    getARGBcolor(keys[i/2], X1, fixPosY, X2, fixPosY);
                }
                break;
            case TopRight: //從右上至右下
                fixPosX = rows-1;
                for(int i=0; i<length; i+=2){
                    int Y1 = i;
                    int Y2 = Y1+1;
                    getARGBcolor(keys[((i/2) + startPos)], fixPosX, Y1, fixPosX, Y2);
                }
                break;
            case BottomRight: //從右下至左下
                fixPosY = columns-1;
                for(int i=0; i<length; i+=2){
                    int X1 = rows-i-1;
                    int X2 = X1-1;
                    getARGBcolor(keys[((i/2) + startPos)], X1, fixPosY, X2, fixPosY);
                }
                break;
            case BottomLeft: //從左下至左上
                fixPosX = 0;
                for(int i=0; i<length; i+=2){
                    int Y1 = columns-i-1;
                    int Y2 = Y1-1;
                    getARGBcolor(keys[((i/2) + startPos)], fixPosX, Y1, fixPosX, Y2);
                }
                break;
        }
    }

    private final int type_alpha = 0;
    private final int type_red   = 1;
    private final int type_green = 2;
    private final int type_blue  = 3;
    private int a1, r1, g1, b1;
    private int a2, r2, g2, b2;
    private void getARGBcolor(String charKey, int posX1, int posY1, int posX2, int posY2){
        TV.append("pos:"+ posX1+"_"+posY1 +":"+ posX2+"_"+posY2 +"\n");

        int color1 = rgbValues[posX1][posY1];
        int color2 = rgbValues[posX2][posY2];

        a1 = (int)Color.alpha(color1);
        r1 = (int)Color.red(color1);
        g1 = (int)Color.green(color1);
        b1 = (int)Color.blue(color1);

        a2 = (int)Color.alpha(color2);
        r2 = (int)Color.red(color2);
        g2 = (int)Color.green(color2);
        b2 = (int)Color.blue(color2);

        TV.append(a1 +"_"+ r1 +"_"+ g1 +"_"+ b1 +" "+
                   a2 +"_"+ r2 +"_"+ g2 +"_"+ b2 +"\n");
        TV.append(charKey +"\n");

        //Bitmap取alpha值，會都為255不透明，故不埋值
//        resetColor(type_alpha, a1, a2, charKey.substring(-1,-1));
        resetColor(type_red, r1, r2, charKey.substring(0,3));
        resetColor(type_green, g1, g2, charKey.substring(3,5));
        resetColor(type_blue, b1, b2, charKey.substring(5,7));

        TV.append("----------------------------- \n");

//        rgbValues[posX1][posY1] = Color.argb(a1, r1, g1, b1);
//        rgbValues[posX2][posY2] = Color.argb(a2, r2, g2, b2);
        rgbValues[posX1][posY1] = Color.rgb(r1, g1, b1);
        rgbValues[posX2][posY2] = Color.rgb(r2, g2, b2);
    }

    public int[][] getARGBvalues(){
      return rgbValues;
    };

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
}
