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
public class DataDecoding2 {
    private String TAG = "DataDecoding2";
    private Context mContext;

    private Bitmap bmp;
//    private BitmapARGB mBitmapARGB;
//    private int[][] rgbValues;
    private int rows, columns; //[列(row)]寬, [行(column)]高
    private int keyCount;
    private TextView TV, dTV;

    public static synchronized DataDecoding2 getInstance(Context context){
        return new DataDecoding2(context);
    }

    public DataDecoding2(Context context){
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

        //長寬最大值奇偶數處理
        int startX = (rows%2==0) ? rows-2:rows-3;
        int startY = (columns%2==0) ? columns-2:columns-3;

        loadData(keySize, startX, startY, 0); //取字數長度
        TV.append("key length:"+keyCount +"\n");

        try{
            for(int i=0; i<keyCount; i++){
                loadData(keyContent, 0, 0, i);  //取字內容
            }
        }catch (Exception e){
            TV.append("error:"+e.toString() +"\n");
        }


        ((mainActivity)mContext).ProcessTime(mainActivity.endCount);
    }

    private int[] posX = new int[4];
    private int[] posY = new int[4];
    private void getPos2x2(int Px, int Py){
        posX[0] = Px;
        posY[0] = Py;
        posX[1] = posX[0] + 1;
        posY[1] = posY[0];
        posX[2] = posX[0];
        posY[2] = posY[0] + 1;
        posX[3] = posX[0] + 1;
        posY[3] = posY[0] + 1;
    }

//    private List<Integer> coordinateX = new ArrayList<Integer>();
//    private List<Integer> coordinateY = new ArrayList<Integer>();
    private void algorithm(String X, String Y){
        TV.append("pos:"+ X+"_"+Y +"\n");
        int Xt = (Integer.valueOf(X.substring(0, 3), 2))*2;
        int Yt = (Integer.valueOf(Y.substring(0, 3), 2))*2;
        int Xo = (Integer.valueOf(X.substring(3, 12), 2))*2;
        int Yo = (Integer.valueOf(Y.substring(3, 12), 2))*2;
        int digit = 1000;
        int Px = (Xt*digit)+Xo;
        int Py = (Yt*digit)+Yo;

//        coordinateX.add(Px);
//        coordinateY.add(Py);
        getPos2x2(Px, Py);
        TV.append("pos:"+ Px+"_"+Py +"\n");
    }

    private final static int keySize    = 0;
    private final static int keyContent = 1;
    private void loadData(int type, int px, int py, int pos){
        switch(type){
            case keySize:
                getPos2x2(px, py);
                keyCount = getKeyLength();
                break;
            case keyContent:
                getRGBcolor();
                break;
        }

    }

    private int[] colorA = new int[4];
    private int[] colorR = new int[4];
    private int[] colorG = new int[4];
    private int[] colorB = new int[4];
    private int getKeyLength(){
        for(int i=0; i<4; i++){
            int color = bmp.getPixel(posX[i], posY[i]);
//            colorA[i] = (int)Color.alpha(color);
            colorR[i] = (int)Color.red(color);
            colorG[i] = (int)Color.green(color);
            colorB[i] = (int)Color.blue(color);

            TV.append("pos:"+ posX[i]+"_"+posY[i] +"\n");
            TV.append("rgb:"+ colorR[i] +"_"+ colorG[i] +"_"+ colorB[i]  +"\n");
        }

        String r = FillIn0(Integer.toBinaryString(Math.abs(colorR[0]-colorR[1])), 3);
        String g = FillIn0(Integer.toBinaryString(Math.abs(colorG[0]-colorG[1])), 2);
        String b = FillIn0(Integer.toBinaryString(Math.abs(colorB[0]-colorB[1])), 2);
        String binaryKey = r + g + b;
        TV.append("-> "+ binaryKey +"\n");

        String Xr = FillIn0(Integer.toBinaryString(Math.abs(colorR[0]-colorR[2])), 4);
        String Xg = FillIn0(Integer.toBinaryString(Math.abs(colorG[0]-colorG[2])), 4);
        String Xb = FillIn0(Integer.toBinaryString(Math.abs(colorB[0]-colorB[2])), 4);
        String X  = Xr + Xg + Xb;
        String Yr = FillIn0(Integer.toBinaryString(Math.abs(colorR[0]-colorR[3])), 4);
        String Yg = FillIn0(Integer.toBinaryString(Math.abs(colorG[0]-colorG[3])), 4);
        String Yb = FillIn0(Integer.toBinaryString(Math.abs(colorB[0]-colorB[3])), 4);
        String Y  = Yr + Yg + Yb;
        algorithm(X, Y);

        return Integer.valueOf(binaryKey, 2);
    }

    private void getRGBcolor(){
        for(int i=0; i<4; i++){
            int color = bmp.getPixel(posX[i], posY[i]);
//            colorA[i] = (int)Color.alpha(color);
            colorR[i] = (int)Color.red(color);
            colorG[i] = (int)Color.green(color);
            colorB[i] = (int)Color.blue(color);

            TV.append("pos:"+ posX[i]+"_"+posY[i] +"\n");
            TV.append("rgb:"+ colorR[i] +"_"+ colorG[i] +"_"+ colorB[i]  +"\n");
        }

        String r = FillIn0(Integer.toBinaryString(Math.abs(colorR[0]-colorR[1])), 3);
        String g = FillIn0(Integer.toBinaryString(Math.abs(colorG[0]-colorG[1])), 2);
        String b = FillIn0(Integer.toBinaryString(Math.abs(colorB[0]-colorB[1])), 2);
        String binaryKey = r + g + b;
        TV.append("-> "+ binaryKey +"\n");


        String Xr = FillIn0(Integer.toBinaryString(Math.abs(colorR[0]-colorR[2])), 4);
        String Xg = FillIn0(Integer.toBinaryString(Math.abs(colorG[0]-colorG[2])), 4);
        String Xb = FillIn0(Integer.toBinaryString(Math.abs(colorB[0]-colorB[2])), 4);
        String X  = Xr + Xg + Xb;
        String Yr = FillIn0(Integer.toBinaryString(Math.abs(colorR[0]-colorR[3])), 4);
        String Yg = FillIn0(Integer.toBinaryString(Math.abs(colorG[0]-colorG[3])), 4);
        String Yb = FillIn0(Integer.toBinaryString(Math.abs(colorB[0]-colorB[3])), 4);
        String Y  = Yr + Yg + Yb;
        algorithm(X, Y);


        int ascii = Integer.valueOf(binaryKey, 2);
        String key = Character.toString((char)ascii);

        TV.append("-> "+ binaryKey +" : "+ ascii +"\n");
        dTV.append(key);

//        Log.d(TAG, "----------------------------- \n");
    }

    //預設二進位長度，長度小於則補0
    private String FillIn0(String s, int length){
        StringBuffer outBinaryStrBuf = new StringBuffer();
        for(int j=0; j<(length - s.length()); j++){
            outBinaryStrBuf.append("0");
        }
        return outBinaryStrBuf + s;
    }
}
