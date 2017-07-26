package com.bitmap.readrgb.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kennethyeh on 16/5/3.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    // GA
//    private Tracker tracker1;
//    private Tracker tracker2;
    private String versionName;
    private String versionCode;
    //CrashHandler case making
    private static CrashHandler INSTANCE = new CrashHandler();

    //Program Context object making
    private Context mContext;

    //System default UncaughtException class making
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    //Used to store information and abnormal information making equipment
    private Map<String, String> infos = new HashMap<String, String>();

    //Used to format the date, the name of the log file as part of making
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


    /**That only one instance of CrashHandler*/
    private CrashHandler() {
    }

    /**Gets CrashHandler examples, singleton pattern*/
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     Making making * initialization
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;

        //Acquisition system default UncaughtException processor making
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        //The CrashHandler settings for the default processor making program
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     Making making * when UncaughtException occurs to the function to process the
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            Log.d(TAG, "uncaughtException DefaultHandler");
            //The exception handler if the user does not have the processing allows the system default to deal making
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            Log.d(TAG, "uncaughtException CrashHandler");
            Log.e("AndroidRuntime", "FATAL EXCEPTION: " + thread.getName(), ex);

            String parsedException = new StandardExceptionParser(mContext, null).getDescription(thread.getName(), ex);
            Log.i(TAG, "parsedException: " + parsedException);

            Map<String, String> hitParameters = MapBuilder.createException(parsedException, true)
                    .set("versionName", versionName)
                    .set("versionCode", versionCode)
                    .build();
//            GATracker.getInstance(mContext).trackUncaughtException(hitParameters);

            //Exit the program, startup program code to restart the following
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);

            // Restart the program, notes above the exit procedures
//           Intent intent = new Intent();
//           intent.setClass(mContext,MainActivity.class);
//           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//           mContext.startActivity(intent);
//           android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     Making making * custom error handling, collect error information, send a bug report and other operations are completed in this
     *
     * @param ex
     * @return true: If the exception information; otherwise it returns false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        //Use Toast to display the abnormal information making
//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                Toast.makeText(mContext, "I'm sorry, the program is abnormal, will exit. ", Toast.LENGTH_LONG).show();
//                Looper.loop();
//            }
//        }.start();

        //Collection device parameter information making
        collectDeviceInfo(mContext);
        //Save log documentation
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     Making making * collection device parameter information
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                versionName = pi.versionName == null ? "null" : pi.versionName;
                versionName = "Develop_Test_"+versionName;
                versionCode = pi.versionCode + "";
                versionCode = "Develop_Test_"+versionCode;
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
                infos.put("isDebug", true+"");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }

//        Field[] fields = Build.class.getDeclaredFields();
//        for (Field field : fields) {
//            try {
//                field.setAccessible(true);
//                infos.put(field.getName(), field.get(null).toString());
//                Log.d(TAG, field.getName() + " : " + field.get(null));
//            } catch (Exception e) {
//                Log.e(TAG, "an error occured when collect crash info", e);
//            }
//        }
    }

    /**
     Making making * save error information to a file
     *
     * @param ex
     * @Return making returns the file name, the file is transferred to the server
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        sb.append("--- DeviceInfo ---\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append("\n--- Exception Log ---\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
//        Throwable cause = ex.getCause();
//        while (cause != null) {
//            cause.printStackTrace(printWriter);
//            cause = cause.getCause();
//        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        try {
            String logStr = sb.toString();
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                String path = "/sdcard/crash/";
                String path = mContext.getExternalCacheDir().getAbsolutePath()+"/crash/";
                Log.i(TAG, "@@ path : " + path);
                Log.i(TAG, "@@ fileName : "+fileName);
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(logStr.getBytes());
                fos.close();
                Log.e(TAG, "@@ saveCrashInfo2File :\n"+logStr);

            }

            if(sb!=null){
                try {
                    sb.delete(0, sb.length());
                    sb=null;
                } catch (Exception e) {}
            }

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }

        return null;
    }
}
