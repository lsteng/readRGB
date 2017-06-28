package com.bitmap.readrgb.datainfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 03070048 on 2017/6/23.
 */

public class SearchDataInfo {

    @SerializedName("total_result_count")
    @Expose
    private Integer totalResultCount;
    @SerializedName("result_count")
    @Expose
    private Integer resultCount;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public Integer getTotalResultCount() {
        return totalResultCount;
    }

    public void setTotalResultCount(Integer totalResultCount) {
        this.totalResultCount = totalResultCount;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public class Result {

        @SerializedName("NewsID")
        @Expose
        private Integer newsID;
        @SerializedName("pic")
        @Expose
        private String pic;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("date")
        @Expose
        private String date;

        public Integer getNewsID() {
            return newsID;
        }

        public void setNewsID(Integer newsID) {
            this.newsID = newsID;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

    }

//    public class PostQueryInfo {
        private String keyword = "海尼根";
        private String FromDevice = "0";
        private String device_id = "null";
        private String count = "20";
//    }

}
