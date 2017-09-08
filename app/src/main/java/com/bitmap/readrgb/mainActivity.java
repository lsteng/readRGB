package com.bitmap.readrgb;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bitmap.readrgb.lab.DataDecoding;
import com.bitmap.readrgb.lab.DataDecoding2;
import com.bitmap.readrgb.lab.DataDecoding3;
import com.bitmap.readrgb.lab.DataHiding;
import com.bitmap.readrgb.lab.DataHiding2;
import com.bitmap.readrgb.lab.DataHiding3;
import com.bitmap.readrgb.lab.LaunchPageInfo;
import com.bitmap.readrgb.lab.PSNR;
import com.bitmap.readrgb.util.connect_Square.Retrofit2;
import com.bitmap.readrgb.util.image.BitmapARGB;
import com.bitmap.readrgb.util.image.BitmapUtils;
import com.bitmap.readrgb.util.image.SelectImage;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by 03070048 on 2016/9/9.
 */
public class mainActivity extends Activity {
    private final static String TAG = "mainActivity";

    //a Bitmap that will act as a handle to the image
    private Bitmap bmp;
    private BitmapFactory.Options options;

    //an integer array that will store ARGB pixel values
//    private int[][] rgbValues;

    private ImageView liv;  //launch page
    private ImageView iv;   //原始選擇圖片
    private ImageView eiv;  //藏密後圖片
    private TextView tv;    //處理log
    private TextView count; //處理時間
    private TextView dTV;   //解密內容
    private Button sbtn;    //選取圖片按鈕
    private Button hbtn;    //開始藏密按鈕
    private Button dbtn;    //開始取密按鈕
    private Button pbtn;    //計算psnr按鈕
    private ProgressBar pb; //藏密圖片，處理進度條
    private ProgressBar pbl;//藏密launchPhoto，處理進度條
    private EditText et;    //藏密文字
    private RadioGroup mRG; //選擇藏解密方法
    private long startTime; //開始處理時間
    private long endTime;   //結束處理時間
    private long totTime;   //總處理時間
    private BitmapARGB mBitmapARGB;
    private DataHiding mDataHiding;
    private DataHiding2 mDataHiding2;
    private DataHiding3 mDataHiding3;
    private DataDecoding mDataDecoding;
    private DataDecoding2 mDataDecoding2;
    private DataDecoding3 mDataDecoding3;
    private PSNR mPSNR;
    private int mSeed = 123456;
    private LaunchPageInfo mLaunchPageInfo;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        liv   = (ImageView) findViewById(R.id.launchIV);
        iv    = (ImageView) findViewById(R.id.img);
        eiv   = (ImageView) findViewById(R.id.eimg);
        count = (TextView) findViewById(R.id.count);
        sbtn  = (Button) findViewById(R.id.btn);
        pb    = (ProgressBar) findViewById(R.id.progressBar);
        pbl   = (ProgressBar) findViewById(R.id.pgBar);
        hbtn  = (Button) findViewById(R.id.hbtn);
        et    = (EditText) findViewById(R.id.ET);
        tv    = (TextView) findViewById(R.id.logTV);
        dbtn  = (Button) findViewById(R.id.dbtn);
        dTV   = (TextView) findViewById(R.id.dTV);
        pbtn  = (Button) findViewById(R.id.psnr);
        mRG   = (RadioGroup) findViewById(R.id.mRG);

        iv.setImageResource(R.mipmap.lenna512);
        //load the image and use the bmp object to access it
        options = new BitmapFactory.Options();
        options.inMutable = true;  //如果为true，则返回一个可以调用setpixel设置每个像素颜色的bitmap，否则调用setpixel会crash
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.lenna512, options);
//        bmp = BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.mipmap.lenna512, 512, 512);

        //define the array size
//        rgbValues = new int[bmp.getWidth()][bmp.getHeight()];

        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ProcessTime(loadCount);
                confirmPermission();
            }
        });


        mRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                setMethod(checkedId);
            }
        });
        setMethod(mRG.getCheckedRadioButtonId());

        hbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBitmapARGB = new BitmapARGB(mainActivity.this);
                ProcessTime(hideCount);
                switch(methodID){
                    case method1:
                        mDataHiding = new DataHiding(mainActivity.this);
                        break;
                    case method2:
                        mDataHiding2 = new DataHiding2(mainActivity.this);
                        break;
                    case method3:
                        mDataHiding3 = new DataHiding3(mainActivity.this);
                        break;
                }
            }
        });

        dbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessTime(decodeCount);
                switch(methodID){
                    case method1:
                        mDataDecoding = new DataDecoding(mainActivity.this);
                        mDataDecoding.setData(eiv, tv);
                        break;
                    case method2:
                        mDataDecoding2 = new DataDecoding2(mainActivity.this);
                        mDataDecoding2.setData(eiv, mSeed, tv);
                        break;
                    case method3:
                        mDataDecoding3 = new DataDecoding3(mainActivity.this);
                        mDataDecoding3.setData(eiv, tv);
                        break;
                }
            }
        });

        pbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPSNR= new PSNR(mainActivity.this);
                ProcessTime(PsnrCount);
            }
        });

        chkDataHidingImg();

        // OkHttp3
//        OkHttp3 mOkHttp3 = new OkHttp3(mainActivity.this);
        // Retrofit2
        Retrofit2 mRetrofit2 = Retrofit2.getInstance(mainActivity.this);
        mRetrofit2.setLaunchPageListener(new Retrofit2.LaunchPageListener(){
            @Override
            public void setImage(String url) {
                mainApplication.imageLoader.displayImage(url, liv, mainApplication.options, new LaunchPageDisplayListener());
                tv.setText("getLaunchPage: " + url +"\n");
            }
        });
        mRetrofit2.requestLaunchData();
    }

    class LaunchPageDisplayListener extends SimpleImageLoadingListener {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
//                ImageView imageView = (ImageView) view;
                mLaunchPageInfo = new LaunchPageInfo(mainActivity.this);
                loadLaunchPageInfo(loadedImage);
            }
        }
    }

    private boolean waitDouble = true;
    private static final int DOUBLE_CLICK_TIME = 350; //二次click時間間隔
    private void loadLaunchPageInfo(final Bitmap loadedImage){
        //儲存來源圖片
        BitmapUtils.saveBitmapToFile(mainActivity.this, loadedImage, "originLP.png");

        liv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (waitDouble == true){
                    waitDouble = false;
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(DOUBLE_CLICK_TIME);
                                if (waitDouble == false){
                                    waitDouble = true;
                                    //singleClick();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }else{
                    waitDouble = true;
                    //doubleClick();
                    //解密 launch photo
                    ProcessTime(CountStart);
                    tv.setText("");
                    mLaunchPageInfo.setData(liv, tv, LaunchPageInfo.decodeDataType, null);
                }
            }
        });

        liv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //加密 launch photo
                ProcessTime(CountStart);
                pbl.setVisibility(View.VISIBLE);

                mBitmapARGB = new BitmapARGB(mainActivity.this);
                mBitmapARGB.getARGB(loadedImage, BitmapARGB.hideLP);
                return false;
            }
        });
    }

    private final int method1 = 1;
    private final int method2 = 2;
    private final int method3 = 3;
    private int methodID = method2;
    private void setMethod(int checkedId){
        switch(checkedId){
            case R.id.opt1:
                methodID = method1;
                break;
            case R.id.opt2:
                methodID = method2;
                break;
            case R.id.opt3:
                methodID = method3;
                break;
        }
//        Toast.makeText(getApplicationContext(), ""+methodID, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Uri imageUri = SelectImage.getPickImageResultUri(data, this);
            iv.setImageURI(imageUri);
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }catch (IOException ex){
                Log.e(TAG, ex.toString());
            }

            //獲取圖片的路徑
            String[] column = {MediaStore.Images.Media.DATA};
            //好像是android多媒體數據庫的封裝介面，具體的看Android文檔
//            Cursor cursor = managedQuery(imageUri, column, null, null, null);
            Cursor cursor = getContentResolver().query(imageUri, column, null, null, null);
            //按筆者個人理解 這個是獲得用戶選擇的圖片的索引值
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //將光標移至開頭 ，這個很重要，不小心很容易引起越界
//            cursor.moveToFirst();
            //最後根據索引值獲取圖片路徑
            String imgPath = null;
//            String imgPath = cursor.getString(column_index);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    imgPath = cursor.getString(column_index);
                }
                cursor.close();
            } else {
                tv.append("\n"+"Cursor is null");
                imgPath = imageUri.getPath();
            }
            tv.setText("img path: "+imgPath);

//            bmp = BitmapFactory.decodeFile(imgPath, options);
            tv.append("\n"+"bmp size: "+bmp.getWidth()+"_"+bmp.getHeight());

            //判斷副檔名
//            String extension = imgPath.substring(imgPath.length()-3, imgPath.length());
//            boolean isGIF = extension.equalsIgnoreCase("gif");

            //get mime type from Uri
//            ContentResolver cR = this.getContentResolver();
//            MimeTypeMap mime = MimeTypeMap.getSingleton();
//            String type = mime.getExtensionFromMimeType(cR.getType(imageUri));
            tv.append("\n"+"mime type: "+ getContentResolver().getType(imageUri));


            //以512x512為標準，做等比例縮放。若為GIF檔，也一併處理
            int size = 1000;
//            if(bmp.getWidth()>size || bmp.getHeight()>size || isGIF){
            if(bmp.getWidth()>size || bmp.getHeight()>size){
                int rate;
                if (bmp.getWidth() > bmp.getHeight()){
                    rate = bmp.getWidth()/size;
                }else{
                    rate = bmp.getHeight()/size;
                }
                rate = (rate<1) ? 1:rate;

                bmp = BitmapUtils.decodeSampledBitmapFromFile(imgPath, bmp.getWidth()/rate, bmp.getHeight()/rate);
                tv.append("\n"+"bmp scale: "+bmp.getWidth()+"_"+bmp.getHeight());
            }

//            bmp = BitmapUtils.setCompress(bmp);
//            tv.append("\n"+"bmp scale: "+bmp.getWidth()+"_"+bmp.getHeight());

            iv.setImageBitmap(bmp);
        }
    }

    public final static int CountStart  = 0;
    public final static int endCount    = 1;
    public final static int endSaveCount= 2;
    public final static int hideCount   = 3;
    public final static int hideStart   = 4;
    public final static int decodeCount = 5;
    public final static int decodeStart = 6;
    public final static int PsnrCount   = 7;
    public final static int hideLP      = 8;
    public final static int endSaveLP   = 9;
    public final static int decodeLP    = 10;

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            totTime = totTime + 100;
            count.setText("Using Time: " + ((float)totTime/1000));
            mHandler.postDelayed(this, 100);
        }
    };
    public void ProcessTime(int type){
        switch (type){
            case CountStart:
                //開始時間
                startTime = System.currentTimeMillis();
                totTime = 0;
                mHandler.postDelayed(runnable, 100);
//                pb.setVisibility(View.VISIBLE);
//                mBitmapARGB.getARGB(bmp, rgbValues, BitmapARGB.load);
                break;

            case endCount:
                //結束時間
                endTime = System.currentTimeMillis();
                mHandler.removeCallbacks(runnable);
                pb.setVisibility(View.GONE);
                //執行時間
//                totTime = startTime - System.currentTimeMillis();
                totTime = endTime - startTime;
                //印出執行時間
                count.setText("Using Time: " + ((float)totTime/1000));
                break;

            case endSaveCount:
                endTime = System.currentTimeMillis();
                mHandler.removeCallbacks(runnable);
                pb.setVisibility(View.GONE);
                eiv.setImageBitmap(mBitmapARGB.getBitmap());

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
//                            BitmapUtils.saveBitmapToFile(mainActivity.this, mBitmapARGB.getBitmap(), tv);
                            BitmapUtils.saveBitmapToFile(mainActivity.this, mBitmapARGB.getBitmap(), "datahiding.png");

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                };
                thread.start();

                totTime = endTime - startTime;
                count.setText("Using Time: " + ((float)totTime/1000));
                break;

            case endSaveLP:
                endTime = System.currentTimeMillis();
                mHandler.removeCallbacks(runnable);
                pbl.setVisibility(View.GONE);
//                eiv.setImageBitmap(mBitmapARGB.getBitmap());
                liv.setImageBitmap(mBitmapARGB.getBitmap());

                Thread threadLP = new Thread(){
                    @Override
                    public void run() {
                        try {
//                            BitmapUtils.saveBitmapToFile(mainActivity.this, mBitmapARGB.getBitmap(), tv);
                            BitmapUtils.saveBitmapToFile(mainActivity.this, mBitmapARGB.getBitmap(), "hideLP.png");

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                };
                threadLP.start();

                chkColor(mBitmapARGB.getBitmap().getPixel(0, 0));
                chkColor(mBitmapARGB.getBitmap().getPixel(0, 242));
                chkColor(mBitmapARGB.getBitmap().getPixel(0, 431));
                chkColor(mBitmapARGB.getBitmap().getPixel(242, 431));

                totTime = endTime - startTime;
                count.setText("Using Time: " + ((float)totTime/1000));
                break;

            case hideCount:
                //開始時間
                startTime = System.currentTimeMillis();
                totTime = 0;
                mHandler.postDelayed(runnable, 100);
                pb.setVisibility(View.VISIBLE);

//                BitmapDrawable mDrawable =  (BitmapDrawable) iv.getDrawable();
//                Bitmap mBitmap = mDrawable.getBitmap();
                mBitmapARGB.getARGB(bmp, BitmapARGB.hide);
                break;
            case hideStart:
                tv.setText("");
                String s = et.getText().toString();
                switch(methodID){
                    case method1:
                        mDataHiding.setData(s, mBitmapARGB.getARGBvalues(), bmp.getWidth(), bmp.getHeight(), tv);
                        mBitmapARGB.setRGB(mDataHiding.getARGBvalues(), BitmapARGB.save);
                        break;
                    case method2:
                        mDataHiding2.setData(s, mBitmapARGB.getARGBvalues(), bmp.getWidth(), bmp.getHeight(), mSeed, tv);
                        mBitmapARGB.setRGB(mDataHiding2.getARGBvalues(), BitmapARGB.save);
                        break;
                    case method3:
                        mDataHiding3.setData(s, mBitmapARGB.getARGBvalues(), bmp.getWidth(), bmp.getHeight(), tv);
                        mBitmapARGB.setRGB(mDataHiding3.getARGBvalues(), BitmapARGB.save);
                        break;
                }
                break;

            case hideLP:
                tv.setText("");
                mLaunchPageInfo.setData(liv, tv, LaunchPageInfo.hideDataType, mBitmapARGB.getARGBvalues());
                mBitmapARGB.setRGB(mLaunchPageInfo.getARGBvalues(), BitmapARGB.saveLP);

                chkColor(mLaunchPageInfo.getARGBvalues(), 0, 0);
                chkColor(mLaunchPageInfo.getARGBvalues(), 0, 242);
                chkColor(mLaunchPageInfo.getARGBvalues(), 0, 431);
                chkColor(mLaunchPageInfo.getARGBvalues(), 242, 431);
                break;

            case decodeCount:
                //開始時間
                startTime = System.currentTimeMillis();
                totTime = 0;
                mHandler.postDelayed(runnable, 100);
                pb.setVisibility(View.VISIBLE);
                tv.setText("");
                break;
            case decodeStart:
                dTV.setText("");
                switch(methodID){
                    case method1:
                        mDataDecoding.decodeData(dTV);
                        break;
                    case method2:
                        mDataDecoding2.decodeData(dTV);
                        break;
                    case method3:
                        mDataDecoding3.decodeData(dTV);
                        break;
                }
                break;

            case PsnrCount:
                //開始時間
                startTime = System.currentTimeMillis();
                totTime = 0;
                mHandler.postDelayed(runnable, 100);
                pb.setVisibility(View.VISIBLE);

                BitmapDrawable mDrawable1 = (BitmapDrawable) iv.getDrawable();
                Bitmap bmp1 = mDrawable1.getBitmap();
                BitmapDrawable mDrawable2 = (BitmapDrawable) eiv.getDrawable();
                Bitmap bmp2 = mDrawable2.getBitmap();
                mPSNR.calculator(bmp1, bmp2, tv);
                break;

        }
    }

    //檢查是否有藏密圖片
    private void chkDataHidingImg(){
        try{
            String pathName = getExternalCacheDir().getAbsolutePath()+"/pic/datahiding.png";
            File f=new File(pathName);
            if(f.exists()){
                Bitmap stegobmp = BitmapFactory.decodeFile(pathName);
                eiv.setImageBitmap(stegobmp);
            }
        }catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    //6.0以後版本，針對危險及別的權限，需要另外詢問使用者同意，才能處理
    //例如存取外部儲存空間(相簿圖片)，屬於危險級別的權限，就要另外詢問處理
    private final static int CODE_FOR_WRITE_PERMISSION = 777;
    private void confirmPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_FOR_WRITE_PERMISSION);
                return;
            }

            SelectImage.pickImage(mainActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CODE_FOR_WRITE_PERMISSION){
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //用户同意使用write
                //startGetImageThread();
                SelectImage.pickImage(mainActivity.this);
            }else{
                //用户不同意，自行处理即可
                //finish();
                Toast.makeText(getApplicationContext(), "未取得授權！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void chkColor(int[][] rgbValues, int x, int y){
        int color = rgbValues[x][y];
        //int a = (int)Color.alpha(color);
        int r = (int)Color.red(color);
        int g = (int)Color.green(color);
        int b = (int)Color.blue(color);
        tv.append(x+","+y+" -> "+r +"_"+ g +"_"+ b +"\n");
    }
    private void chkColor(int color){
        //int a = (int)Color.alpha(color);
        int r = (int)Color.red(color);
        int g = (int)Color.green(color);
        int b = (int)Color.blue(color);
        tv.append("bitmap color: "+r +"_"+ g +"_"+ b +"\n");
    }
}
