package com.bitmap.readrgb.lab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitmap.readrgb.mainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by 03070048 on 2017/8/24.
 */
public class DataDecoding2 {
    private String TAG = "DataDecoding2";
    private Context mContext;

    private Bitmap bmp;
//    private BitmapARGB mBitmapARGB;
//    private int[][] rgbValues;
    private int rows, columns; //[列(row)]寬, [行(column)]高
    private int keyCount;
    private int mSeed;
    private TextView TV, dTV;

    public static synchronized DataDecoding2 getInstance(Context context){
        return new DataDecoding2(context);
    }

    public DataDecoding2(Context context){
        mContext = context;
    }

    public void setData(ImageView iv, int seed, TextView tv){
        this.mSeed = seed;
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

        //使用亂數種子，先得到起始點座標
        Random random = new Random();
        random.setSeed(mSeed);
        randomStartPos(rows, columns, random);

        loadData(keySizeFlag); //取字數長度
        TV.append("key length:"+keyCount +"\n");
        TV.append("----------------------------- \n");

        //依照字數長度，以亂數種子隨機產生不重複座標
        randomPos(rows, columns, keyCount, random);

        try{
            for(int i=keyContentFlag; i<keyCount+1; i++){
                loadData(i);  //取字內容
            }
        }catch (Exception e){
            TV.append("error:"+e.toString() +"\n");
        }

        ((mainActivity)mContext).ProcessTime(mainActivity.endCount);
    }

    private final int keySizeFlag    = 0;
    private final int keyContentFlag = 1;
    private void loadData(int pos){
        String position = coordinateList.get(pos);
        int index = position.indexOf(coordinateIndex);
        int length = position.length();
        int Px = Integer.valueOf(position.substring(0, index));
        int Py = Integer.valueOf(position.substring(index+1, length));

        if(pos == keySizeFlag){
            keyCount = getKeyLength(Px, Py, Px+1, Py);
        }else{
            getARGBcolor(Px, Py, Px+1, Py);
        }
    }

    private int getKeyLength(int posX1, int posY1, int posX2, int posY2){
        TV.append("keySize pos:"+ posX1+"_"+posY1 +":"+ posX2+"_"+posY2 +"\n");

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
        TV.append("keyContent pos:"+ posX1+"_"+posY1+":"+posX2+"_"+posY2 +"\n");

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

        TV.append("----------------------------- \n");
//        Log.d(TAG, "-----------------------------");
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
