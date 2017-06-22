package com.bitmap.readrgb.util.OkHttp3;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by 03070048 on 2017/6/22.
 */

public interface retrofitAPI {

    @GET("public/AppServices/version/newsApp2/stage/android_1/version.json")
    Call<ResponseBody> launchDataGetCall();
}
