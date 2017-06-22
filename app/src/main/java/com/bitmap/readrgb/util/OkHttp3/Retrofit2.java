package com.bitmap.readrgb.util.OkHttp3;

import android.util.Log;

import com.bitmap.readrgb.datainfo.LaunchDataInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by 03070048 on 2017/6/22.
 */

public class Retrofit2 {
    private final static String TAG = "retrofit2";

    public static void requestLaunchData(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://sun.iset.com.tw/")
                .build();
        retrofitAPI repo = retrofit.create(retrofitAPI.class);

        Call<ResponseBody> call = repo.launchDataGetCall();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Gson gson = new Gson();
                    LaunchDataInfo.Launch mLaunchDataInfo = gson.fromJson(response.body().string(), new TypeToken<LaunchDataInfo.Launch>(){}.getType());

                    Log.d(TAG, "android v" + mLaunchDataInfo.version.android);
                    Log.d(TAG, "launchPhoto default: " + mLaunchDataInfo.launchPhoto.defaultFile);
                    Log.d(TAG, "launchPhoto season: " + mLaunchDataInfo.launchPhoto.seasonFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
