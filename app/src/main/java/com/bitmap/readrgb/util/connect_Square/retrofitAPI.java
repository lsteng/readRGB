package com.bitmap.readrgb.util.connect_Square;

import com.bitmap.readrgb.datainfo.SearchDataInfo;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by 03070048 on 2017/6/22.
 */

public interface retrofitAPI {
    @POST()
    Call<ResponseBody> post(@Url String url, @QueryMap Map<String, String> map);
    @GET()
    Call<ResponseBody> get(@Url String url);

    @GET("public/AppServices/version/newsApp2/stage/android_1/version.json")
    Call<ResponseBody> launchDataGetCall();

    @FormUrlEncoded
    @POST("setn_search/get_news_list.php")
    Call<SearchDataInfo> searchDataGetCall(@Field("keyword") String keyword,
                                           @Field("FromDevice") String FromDevice,
                                           @Field("device_id") String device_id,
                                           @Field("count") String count);
}
