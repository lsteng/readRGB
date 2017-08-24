package com.bitmap.readrgb.lab;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by 03070048 on 2017/8/22.
 */
public class DataHiding2 {
    private String TAG = "DataHiding2";
    private Context mContext;
    private int mSeed;
    private TextView TV;

    public static synchronized DataHiding2 getInstance(Context context){
        return new DataHiding2(context);
    }

    public DataHiding2(Context context){
        mContext = context;

    }

    private int[][] rgbValues;
    private int rows, columns; //[列(row)]寬, [行(column)]高
    public void setData(String key, int[][] rgbValues, int rows, int columns, int seed, TextView tv){
        this.rgbValues = rgbValues;
        this.rows = rows;
        this.columns = columns;
        this.mSeed = seed;
        this.TV = tv;

        int totalCount = key.length();

        Log.d(TAG, "rows:"+rows +"_columns:"+ columns);
        tv.append("rows:"+rows +"_columns:"+ columns +"\n");

        //使用亂數種子，先得到起始點座標
        Random random = new Random();
        random.setSeed(mSeed);
        randomStartPos(rows, columns, random);
        //依照字數長度，以亂數種子隨機產生不重複座標
        randomPos(rows, columns, key.length(), random);

        //埋放字數長度
        String keyLength;
        keyLength = Integer.toBinaryString(totalCount);
        keyLength = Fillin0(keyLength, 7); //預設二進位長度7位，長度小於7則補0
        tv.append("key length:"+totalCount+" -> "+keyLength +"\n");
        hideData(keyLength, 0);

        //埋放內容
        String[] keys = new String[totalCount];
        for(int i=0; i<key.length(); i++){
            char c = key.charAt(i);
            int a = (int) c;
            String b = Integer.toBinaryString(a);
            keys[i] = Fillin0(b, 7); //預設二進位長度7位，長度小於7則補0
            Log.d(TAG, "key:"+c +"_ascii:"+a +"_binary:"+keys[i]);

            hideData(keys[i], i+1);
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

    private void hideData(String keys, int pos){
        String position = coordinateList.get(pos);
        int index = position.indexOf(coordinateIndex);
        int length = position.length();
        int Px = Integer.valueOf(position.substring(0, index));
        int Py = Integer.valueOf(position.substring(index+1, length));

        getARGBcolor(keys, Px, Py, Px+1, Py);
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

    //二維陣列中存放不重複的x,y座標 (2x1)
    private List<String> coordinateList;
    private String coordinateIndex = ",";
    HashSet<String> hset;
    public void randomStartPos(int rangeX, int rangeY, Random random) {
        coordinateList = new ArrayList<String>();
        hset = new HashSet<String>();

        //長寬最大值奇偶數處理
        rangeX = (rangeX%2==0) ? rangeX-1:rangeX-2;
//        rangeY = (rangeY%2==0) ? rangeY-1:rangeY-2;

        int x,y ;
        String coordinate ;

        x = random.nextInt(rangeX)/2*2; //(2x1)為一區塊
        y = random.nextInt(rangeY);
        coordinate = x + coordinateIndex + y;
        hset.add(coordinate);
        coordinateList.add(coordinate);

//        int startPoint = 0;
//        TV.append((startPoint)+". "+coordinateList.get(startPoint) +"\n");
    }

    public void randomPos(int rangeX, int rangeY, int keyLength, Random random) {
        //長寬最大值奇偶數處理
        rangeX = (rangeX%2==0) ? rangeX-1:rangeX-2;
//        rangeY = (rangeY%2==0) ? rangeY-1:rangeY-2;

        int x,y ;
        String coordinate ;

        while(hset.size()<=(keyLength)){
            x=random.nextInt(rangeX)/2*2; //(2x1)為一區塊
            y=random.nextInt(rangeY);
            coordinate=x+ coordinateIndex +y;
            hset.add(coordinate);
        }

        //刪除埋放的起始點
        hset.remove(coordinateList.get(0));

        /* hset呼叫iterator()方法，回傳Iterator型態的物件，
         * 該物件包含所有setTest內所存放的值，將該物件存入iterator
         */
        Iterator<String> iterator = hset.iterator() ;
        /* hasNext()為使用游標走訪Iterator物件，檢查下一筆元素是包含物件，
         * 有包含物件則傳回true，否則false。
         * 游標的起始位置在Iterator第一筆元素之前，所以第一次執行hasNext()，
         * 會檢查Iterator的第一筆元素，使用此方式可以走訪Iterator內的所有物件
         */
        while(iterator.hasNext()){
            /* next()為使用游標走訪Iterator，取出下一筆的值。
             * 游標的起始位置在Iterator第一筆之前，所以第一次執行next()，會取出Iterator的第一筆資料
             */
            String nextP = iterator.next();
            coordinateList.add(nextP);
        }

//        Collections.sort(coordinateList); //依照文字進行排序
//        for(int i=0; i<coordinateList.size(); i++){
//            TV.append((i)+". "+coordinateList.get(i) +"\n");
//        }
    }
}
