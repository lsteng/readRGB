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
 * Created by 03070048 on 2016/9/10.
 */
public class DataHiding3 {
    private String TAG = "DataHiding3";
    private Context mContext;

    private TextView TV;
    private String startP;

    public static synchronized DataHiding3 getInstance(Context context){
        return new DataHiding3(context);
    }

    public DataHiding3(Context context){
        mContext = context;
    }

    private int[][] rgbValues;
    private int rows, columns; //[列(row)]寬, [行(column)]高
    public void setData(String key, int[][] rgbValues, int rows, int columns, String startP, TextView tv){
        this.rgbValues = rgbValues;
        this.rows = rows;
        this.columns = columns;
        this.TV = tv;
        this.startP = startP;

        int totalCount = key.length();

        Log.d(TAG, "rows:"+rows +"_columns:"+ columns);
        tv.append("rows:"+rows +"_columns:"+ columns +"\n");

        //依照字數長度，隨機產生不重複座標
        randomPos(rows, columns, key.length());

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
        setPos2x2(pos);
        getARGBcolor(keys, pos);
    }

    private int[] posX = new int[4];
    private int[] posY = new int[4];
    private void setPos2x2(int pos){
        String position = coordinateList.get(pos);
        int index = position.indexOf(",");
        int length = position.length();
        posX[0] = Integer.valueOf(position.substring(0, index));
        posY[0] = Integer.valueOf(position.substring(index+1, length));
        posX[1] = posX[0] + 1;
        posY[1] = posY[0];
        posX[2] = posX[0];
        posY[2] = posY[0] + 1;
        posX[3] = posX[0] + 1;
        posY[3] = posY[0] + 1;
    }

    private final int type_alpha = 0;
    private final int type_red   = 1;
    private final int type_green = 2;
    private final int type_blue  = 3;
    private int[] colorA = new int[4];
    private int[] colorR = new int[4];
    private int[] colorG = new int[4];
    private int[] colorB = new int[4];
    private void getARGBcolor(String charKey, int pos){

        for(int i=0; i<4; i++){
            int color = rgbValues[posX[i]][posY[i]];
            colorA[i] = (int)Color.alpha(color);
            colorR[i] = (int)Color.red(color);
            colorG[i] = (int)Color.green(color);
            colorB[i] = (int)Color.blue(color);

            TV.append("pos:"+ posX[i]+"_"+posY[i] +"\n");
            TV.append("rgb:"+ colorR[i] +"_"+ colorG[i] +"_"+ colorB[i]  +"\n");
        }

        TV.append(charKey +"\n");

        //Bitmap取alpha值，會都為255不透明，故不埋值
        int numberKey = 1;
        int numberX   = 2;
        int numberY   = 3;
        //埋放內容
        resetColor(type_red, colorR[0], colorR[1], charKey.substring(0,3), numberKey);
        resetColor(type_green, colorG[0], colorG[1], charKey.substring(3,5), numberKey);
        resetColor(type_blue, colorB[0], colorB[1], charKey.substring(5,7), numberKey);

        //埋放下一點位置
        if(pos < algorithmList.size()){
            int index  = algorithmList.get(pos).indexOf(coordinateIndex);
            int length = algorithmList.get(pos).length();
            String Xs  = algorithmList.get(pos).substring(0, index);
            String Ys  = algorithmList.get(pos).substring(index+1, length);
            TV.append(coordinateList.get(pos+1) +"\n");

            TV.append(Xs +"\n");
            resetColor(type_red, colorR[0], colorR[2], Xs.substring(0, 5), numberX);
            resetColor(type_green, colorG[0], colorG[2], Xs.substring(5, 9), numberX);
            resetColor(type_blue, colorB[0], colorB[2], Xs.substring(9, 13), numberX);

            TV.append(Ys +"\n");
            resetColor(type_red, colorR[0], colorR[3], Ys.substring(0, 5), numberY);
            resetColor(type_green, colorG[0], colorG[3], Ys.substring(5, 9), numberY);
            resetColor(type_blue, colorB[0], colorB[3], Ys.substring(9, 13), numberY);
        }

        TV.append("----------------------------- \n");

        for(int j=0; j<4; j++){
            rgbValues[posX[j]][posY[j]] = Color.rgb(colorR[j], colorG[j], colorB[j]);

            TV.append("Cpos:"+ posX[j]+"_"+posY[j] +"\n");
            TV.append("Crgb:"+ colorR[j] +"_"+ colorG[j] +"_"+ colorB[j]  +"\n");
        }
    }

    public int[][] getARGBvalues(){
      return rgbValues;
    };

    private void resetColor(int typeColor, int currentP, int nextP, String binaryKey, int number){
        int to10Key = Integer.valueOf(binaryKey, 2);
        int diff = currentP - nextP;

        int compare = to10Key - Math.abs(diff);
        if(compare != 0){
            if(diff > 0){ //正，currentP > nextP
                if(compare > 0){ //需加大，原差距較小
                    if(nextP - Math.abs(compare) < 0){ //若nextP減少小於0，則currentP加入剩值
                        if(number < 2){
                            currentP = currentP + Math.abs(nextP - Math.abs(compare));
                            nextP = 0;
                        }else{
                            nextP = currentP + Math.abs(compare); //將下一點改比原點大，防止座標溢位
                        }
                    }else{
                        nextP = nextP - Math.abs(compare);
                    }
                }
                if(compare < 0){ //需縮小，原差距較大
                    if(nextP + Math.abs(compare) > 255){ //若next增加超過255，則currentP扣除剩值
                        if(number < 2) {
                            currentP = currentP - ((nextP + Math.abs(compare)) - 255);
                            nextP = 255;
                        }else{
                            nextP = currentP - Math.abs(compare); //將下一點改比原點小，防止座標溢位
                        }
                    }else{
                        nextP = nextP + Math.abs(compare);
                    }
                }
            }

            if(diff < 0){ //負，currentP < nextP (算法與前者相反)
                if(compare > 0){ //需加大，原差距較小
                    if(nextP + Math.abs(compare) > 255){ //若next增加超過255，則currentP扣除剩值
                        if(number < 2) {
                            currentP = currentP - ((nextP + Math.abs(compare)) - 255);
                            nextP = 255;
                        }else{
                            nextP = currentP - Math.abs(compare); //將下一點改比原點小，防止座標溢位
                        }
                    }else{
                        nextP = nextP + Math.abs(compare);
                    }
                }
                if(compare < 0){ //需縮小，原差距較大
                    if(nextP - Math.abs(compare) < 0){ //若nextP減少小於0，則currentP加入剩值
                        if(number < 2) {
                            currentP = currentP + Math.abs(nextP - Math.abs(compare));
                            nextP = 0;
                        }else{
                            nextP = currentP + Math.abs(compare); //將下一點改比原點大，防止座標溢位
                        }
                    }else{
                        nextP = nextP - Math.abs(compare);
                    }
                }
            }

            if(diff == 0) { //currentP = nextP
                if (nextP + Math.abs(compare) > 255) { //若next增加超過255，則currentP扣除剩值
                    if(number < 2) {
                        currentP = currentP - ((nextP + Math.abs(compare)) - 255);
                        nextP = 255;
                    }else{
                        nextP = currentP - Math.abs(compare); //將下一點改比原點小，防止座標溢位
                    }
                } else {
                    nextP = nextP + Math.abs(compare);
                }
            }
        }

        TV.append(binaryKey +":"+ to10Key +" -> " +currentP +" - "+ nextP +"\n");

        switch(typeColor){
            case type_alpha:
                colorA[0] = currentP;
                colorA[number] = nextP;
                break;
            case type_red:
                colorR[0] = currentP;
                colorR[number] = nextP;
                break;
            case type_green:
                colorG[0] = currentP;
                colorG[number] = nextP;
                break;
            case type_blue:
                colorB[0] = currentP;
                colorB[number] = nextP;
                break;
        }
    }


    //二維陣列中存放不重複的x,y座標 (2x2)
    private List<String> coordinateList;
    private String coordinateIndex = ",";
    public void randomPos(int rangeX, int rangeY, int keyLength) {
        coordinateList = new ArrayList<String>();
        HashSet<String> hset = new HashSet<String>();
        Random random = new Random();

        //長寬最大值奇偶數處理
        rangeX = (rangeX%2==0) ? rangeX-1:rangeX-2;
        rangeY = (rangeY%2==0) ? rangeY-1:rangeY-2;

        int x,y ;
        String coordinate ;
        if(startP==null){
            startP = (rangeX-1)+","+(rangeY-1);
        }
        TV.append("startP: "+ startP +"\n");
        hset.add(startP);
        while(hset.size()<(keyLength+1)){
            x=random.nextInt(rangeX)/2*2; //(2x2)為一區塊
            y=random.nextInt(rangeY)/2*2; //(2x2)為一區塊
            coordinate=x+ coordinateIndex +y;
            hset.add(coordinate);
        }

        //刪除埋放長度與下一點的起始點
        coordinateList.add(startP);
        hset.remove(startP);

        /* hset呼叫iterator()方法，回傳Iterator型態的物件，
         * 該物件包含所有setTest內所存放的值，將該物件存入iterator
         */
        Iterator<String> iterator = hset.iterator() ;
        /* hasNext()為使用游標走訪Iterator物件，檢查下一筆元素是包含物件，
         * 有包含物件則傳回true，否則false。
         * 游標的起始位置在Iterator第一筆元素之前，所以第一次執行hasNext()，
         * 會檢查Iterator的第一筆元素，使用此方式可以走訪Iterator內的所有物件
         */
        algorithmList = new ArrayList<String>();
        while(iterator.hasNext()){
            /* next()為使用游標走訪Iterator，取出下一筆的值。
             * 游標的起始位置在Iterator第一筆之前，所以第一次執行next()，會取出Iterator的第一筆資料
             */
            String nextP = iterator.next();
            coordinateList.add(nextP);
            algorithm(nextP);
        }

//        Collections.sort(coordinateList); //依照文字進行排序
//        for(int i=0; i<coordinateList.size(); i++){
//            TV.append((i)+". "+coordinateList.get(i) +"\n");
//            if(i>0){
//                TV.append(algorithmList.get(i-1) +"\n");
//            }
//        }
    }

    private List<String> algorithmList;
    private void algorithm(String position){
        int index = position.indexOf(coordinateIndex);
        int length = position.length();
        int Px = Integer.valueOf(position.substring(0, index));
        int Py = Integer.valueOf(position.substring(index+1, length));
        int digit = 1000;
        String Bx = Fillin0(Integer.toBinaryString((Px/digit)), 4) + Fillin0(Integer.toBinaryString((Px%digit)/2), 9);
        String By = Fillin0(Integer.toBinaryString((Py/digit)), 4) + Fillin0(Integer.toBinaryString((Py%digit)/2), 9);
        position = Bx+","+By;
        algorithmList.add(position);
    }


    /* 不重複亂數 (Collection移出法)
     * 此方法較快，尤其在產生大量亂數的時候更為明顯。分成三個部分來解釋.
     * 1. 產生一個ArrayList，並且利用for loop塞值進去，你想要產生0~99的亂數，就丟100進去，他就會依序把0~99塞到ArrayList裡面.
     * 2. 用Collection的remove method，隨機的取index，並且移出，直到 ArrayList 的size = 0。
     * 因為本來在ArrayList裡面的數字就沒有重複(用for loop塞的)，所以隨機取出的值也不會重複!
     * 3. 最後一個部份就是去呼叫上面的方法並且宣告一個array來接收上面產生的亂數
     */
    public static int[] GenerateNonDuplicateRan2(int range, int getSize)
    {
        Random mRandom = new Random();
        int randoms[] = new int[getSize];
        List<Integer> holeList = new ArrayList<Integer>();
        for(int i=0; i<range; i++){
            //holeList.add(new Integer(i));
            holeList.add(i);
        }
        int count=0;
        while (holeList.size()>0 && count<getSize) {
            int pv = holeList.remove(mRandom.nextInt(holeList.size()));
            randoms[count++] = pv;
        }
        return randoms;
    }

    /* 用Hash資料結構做不重複亂數 (HashSet的暴力比較法)
     * HashSet是實作Set介面的物件，Set容器中的物件都是唯一的
     * 所以再次新增重複物件時，會判定已有這個物件，而不會加入
     * 至於順序不一樣是因為HashSet的順序是利用Hash Table排序過的，所以會與當初輸入時不一樣
     * 如果要讓set順序與輸入時相同，可以使用LinkedHashSet，他的走訪就會跟LinkedList一樣
     */
    public static int[] GenerateNonDuplicateRan1(int range, int getSize)
    {
        int rnd;
        int[] result = new int[range];
        HashSet<Integer> rndSet = new HashSet<Integer>(range);
        for(int i=0; i<range; i++){
            rnd = (int)(range * Math.random());
            while (!rndSet.add(rnd)){
                rnd = (int)(range * Math.random());
            }
            result[i] = rnd;
        }
        return result;
    }
}
