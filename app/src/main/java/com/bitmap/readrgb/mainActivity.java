package com.bitmap.readrgb;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText et;    //藏密文字
    private long startTime; //開始處理時間
    private long endTime;   //結束處理時間
    private long totTime;   //總處理時間
    private BitmapARGB mBitmapARGB;
    private DataHiding mDataHiding;
    private DataHiding2 mDataHiding2;
    private DataDecoding mDataDecoding;
    private DataDecoding2 mDataDecoding2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        iv    = (ImageView) findViewById(R.id.img);
        eiv   = (ImageView) findViewById(R.id.eimg);
        count = (TextView) findViewById(R.id.count);
        sbtn  = (Button) findViewById(R.id.btn);
        pb    = (ProgressBar) findViewById(R.id.progressBar);
        hbtn  = (Button) findViewById(R.id.hbtn);
        et    = (EditText) findViewById(R.id.ET);
        tv    = (TextView) findViewById(R.id.logTV);
        dbtn  = (Button) findViewById(R.id.dbtn);
        dTV   = (TextView) findViewById(R.id.dTV);
        pbtn  = (Button) findViewById(R.id.psnr);

        iv.setImageResource(R.mipmap.lenna512);
        //load the image and use the bmp object to access it
        options = new BitmapFactory.Options();
        options.inMutable = true;  //如果为true，则返回一个可以调用setpixel设置每个像素颜色的bitmap，否则调用setpixel会crash
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.lenna512, options);
//        bmp = BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.mipmap.lenna512, 512, 512);

        //define the array size
//        rgbValues = new int[bmp.getWidth()][bmp.getHeight()];

        mBitmapARGB = new BitmapARGB(this);
        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ProcessTime(loadCount);
                confirmPermission();
            }
        });

        mDataHiding = new DataHiding(this);
        mDataHiding2 = new DataHiding2(this);
        hbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessTime(hideCount);
            }
        });

        mDataDecoding = new DataDecoding(this);
        mDataDecoding2 = new DataDecoding2(this);
        dbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessTime(decodeCount);
//                mDataDecoding.setData(eiv, tv);
                mDataDecoding2.setData(eiv, tv);
            }
        });

        pbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessTime(PsnrCount);
            }
        });
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

            //亂數種子測試
//            Random mRandom1 = new Random();
//            Random mRandom2 = new Random();
//            int mSeed = 123456;
//            mRandom1.setSeed(mSeed);
//            mRandom2.setSeed(mSeed);
//            tv.append("\n");
//            for (int i=0; i<10; i++){
//                tv.append(mRandom1.nextInt(100)+",");
//            }
//            tv.append("\n");
//            for (int i=0; i<10; i++){
//                tv.append(mRandom2.nextInt(100)+",");
//            }
        }
    }

    public final static int loadCount   = 0;
    public final static int endCount    = 1;
    public final static int endSaveCount= 2;
    public final static int hideCount   = 3;
    public final static int hideStart   = 4;
    public final static int decodeCount = 5;
    public final static int decodeStart = 6;
    public final static int PsnrCount   = 7;

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
//            case loadCount:
//                //開始時間
//                startTime = System.currentTimeMillis();
//                totTime = 0;
//                mHandler.postDelayed(runnable, 100);
//                pb.setVisibility(View.VISIBLE);
//                mBitmapARGB.getARGB(bmp, rgbValues, BitmapARGB.load);
//                break;

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
                            BitmapUtils.saveBitmapToFile(mainActivity.this, mBitmapARGB.getBitmap());

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                };
                thread.start();

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
//                mBitmapARGB.getARGB(bmp, rgbValues, BitmapARGB.hide);
                mBitmapARGB.getARGB(bmp, BitmapARGB.hide);
                break;
            case hideStart:
                tv.setText("");
                String s = et.getText().toString();
//                mDataHiding.setData(s, mBitmapARGB.getARGBvalues(), mBitmapARGB.getBitmap().getWidth(), mBitmapARGB.getBitmap().getHeight(), tv);
//                mBitmapARGB.setRGB(mBitmapARGB.getBitmap(), mDataHiding.getARGBvalues(), BitmapARGB.save);
                mDataHiding2.setData(s, mBitmapARGB.getARGBvalues(), mBitmapARGB.getBitmap().getWidth(), mBitmapARGB.getBitmap().getHeight(), tv);
                mBitmapARGB.setRGB(mBitmapARGB.getBitmap(), mDataHiding2.getARGBvalues(), BitmapARGB.save);
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
//                mDataDecoding.decodeData(dTV);
                mDataDecoding2.decodeData(dTV);
                break;

            case PsnrCount:
                BitmapDrawable mDrawable2 = (BitmapDrawable) eiv.getDrawable();
                Bitmap bmp2 = mDrawable2.getBitmap();
                PSNR.calculator(bmp, mBitmapARGB.getBitmap(), tv);
                break;

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
}
