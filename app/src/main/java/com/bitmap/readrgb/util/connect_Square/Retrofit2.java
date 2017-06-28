package com.bitmap.readrgb.util.connect_Square;

import android.util.Log;

import com.bitmap.readrgb.datainfo.LaunchDataInfo;
import com.bitmap.readrgb.datainfo.SearchDataInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

                if (t != null && (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof ConnectException)) {
                    if (t instanceof SocketTimeoutException || t instanceof TimeoutException) {
                        Log.d(TAG, "onFailure: " + "Oops something went wrong");
                        //avsInterface.onError(call, new AvsException("Oops something went wrong, please try again later..."));
                    } else {
                        Log.d(TAG, "onFailure: " + "Please check your internet connection...");
                        //avsInterface.onError(call, new AvsException("Please check your internet connection..."));
                    }
                } else {
                    Log.d(TAG, "onFailure: " + "Oops something went wrong");
                }
            }
        });
    }

    public static void requestSearchData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://mars.iset.com.tw/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI repo = retrofit.create(retrofitAPI.class);
        Call<SearchDataInfo> call = repo.searchDataGetCall("海尼根", "0", "null", "20");

            call.enqueue(new Callback<SearchDataInfo>() {
            @Override
            public void onResponse(Call<SearchDataInfo> call, Response<SearchDataInfo> response) {
                SearchDataInfo mSearchDataInfo = response.body();

                Log.d(TAG, "total_result_count: " + mSearchDataInfo.getTotalResultCount());
                Log.d(TAG, "result_count: " + mSearchDataInfo.getResultCount());
                if (mSearchDataInfo.getResultCount() > 0) {
                    Log.d(TAG, "results First NewsID: " + mSearchDataInfo.getResults().get(0).getNewsID());
                    Log.d(TAG, "results First pic: " + mSearchDataInfo.getResults().get(0).getPic());
                    Log.d(TAG, "results First title: " + mSearchDataInfo.getResults().get(0).getTitle());
                    Log.d(TAG, "results First date: " + mSearchDataInfo.getResults().get(0).getDate());
                }
            }

            @Override
            public void onFailure(Call<SearchDataInfo> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

                if (t != null && (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof ConnectException)) {
                    if (t instanceof SocketTimeoutException || t instanceof TimeoutException) {
                        Log.d(TAG, "onFailure: " + "Oops something went wrong");
                        //avsInterface.onError(call, new AvsException("Oops something went wrong, please try again later..."));
                    } else {
                        Log.d(TAG, "onFailure: " + "Please check your internet connection...");
                        //avsInterface.onError(call, new AvsException("Please check your internet connection..."));
                    }
                } else {
                    Log.d(TAG, "onFailure: " + "Oops something went wrong");
                }
            }
        });
    }
}
