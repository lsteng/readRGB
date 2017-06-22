package com.bitmap.readrgb.datainfo;

import java.io.Serializable;

/**
 * Created by kennethyeh on 16/2/17.
 */
public class LaunchDataInfo {
    private static final String TAG         = "LaunchDataInfo";
    public static Launch mLaunchInfo = null;

    public static class Launch implements Serializable {
        public VersionCheckInfo version;
        public LaunchPhotoInfo launchPhoto;
        public LaunchAdInfo launchAd;
        public NewsContentRule newsContentRule;
        public DFPContentRule dfpContentRule;
        public SubsRule subsRule;
        public NewsListRule newsListRule;
//        public static int customizeSubsLimit = 10;
    }

    public static class VersionCheckInfo implements Serializable{
        public String android = "";
        public String androidVersionCode = "";
        public String androidBehavior = "";
    }
    public static class LaunchPhotoInfo implements Serializable{
        public String seasonFile ;
        public String seasonEditedKey ;
        public String seasonStart ;
        public String seasonEnd ;
        public String defaultFile ;
        public String defaultEditedKey ;
        public int launchMillisTime;
    }

    public static class LaunchAdInfo implements Serializable{
        public String resource ;
        public int delay ;
    }

    /*
    * 新聞內頁版型
    * */
    public static class NewsContentRule implements Serializable{
        public String adResourceTop ;
        public String adResourceBottom ;
        public String flurryAdUnit ;
        public int displayAdPosition;
    }

    /*
    * DFP 內頁版型
    * */
    public static class DFPContentRule implements Serializable{
        public String adResourceTop ="" ;
    }

    /*
    * 訂閱規則
    * */
    public static class SubsRule implements Serializable{
        public int customizeSubsLimit ;
    }

    /*
    * 新聞列表版型
    * */
    public static class NewsListRule implements Serializable{
        public int displayAdBetweenCount ;
        public String adPlatform            ="";
        public String flurryAdUnit          ="";
        public String applauseAdApiKey      ="";
        public String applauseAdPlacementId ="";
        public String dfpBanner300x250      ="";
    }

}
