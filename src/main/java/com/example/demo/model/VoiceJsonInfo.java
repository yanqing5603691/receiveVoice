package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 86182 on 2019/8/7.
 */
public class VoiceJsonInfo {
    public String callreference;


    public int channelid;


    public int direction;


    public String callerid;


    public String calledid;


    public int deviceid;


    public String extension;


    public long createtick;


    public String createtime;


    public long connecttick;


    public String connecttime;


    public long cleartick;

    public String cleartime;

    public int voiceid;

    public int duration;


    public int calltype;

    public String customer1;
    public String customer2;
    public String customer3;
    public String customer4;
    public String customer5;
    public String customer6;
    public String customer7;
    public String customer8;
    public String customer9;
    public String customer10;


    public List<Audio> audioList;


    public List<Video> videoList;

    public List<Photo> photoList;

    public static class Audio {

        public String rootdir;


        public String singleName;

        public String shortName0;

        public String shortName1;


        public long startRecordtime;


        public long stopRecordtime;


        public int type;

        public Audio(){
            rootdir="";
            shortName0 = "";
            singleName = "";
            shortName1 = "";

        }
    }
    public static class Video {

        public String party;


        public String rootdir;


        public String pathFilename;


        public long startRecordtime;

        public long stopRecordtime;

        public int type;

        public Video(){
            party = "APP";
        }
    }

    public static class Photo{

        public String rootdir;

        public String front;

        public String side;

        public int type;
    }


    public VoiceJsonInfo() {
        voiceid=0;
        callreference = "";
        channelid = 128;
        direction = 0;
        callerid = "";
        calledid = "";
        deviceid = 0;
        extension = "";
        createtick = 0;
        createtime = "";
        connecttick = 0;
        connecttime = "";
        cleartick = 0;
        cleartime = "";
        duration = 0;
        calltype = 1;
        audioList = new ArrayList<>(  );
        videoList = new ArrayList<>(  );
        photoList = new ArrayList<>(  );
    }
}
