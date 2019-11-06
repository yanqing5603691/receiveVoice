package com.example.demo.model;

/**
 * Created by yanqing on 2019/10/15.
 */
public class AnalyzeJsonInfo {
    public String voicePath;
    public String callerId;
    public String calledId;
    public String extension;
    public int duration;
    public String startTime;
    public String callReference;
    public String role;
    public String dir;
    public AnalyzeJsonInfo(){
        voicePath = "";
        calledId = "";
        callerId = "";
        extension = "";
        duration = 0;
        startTime = "";
        callReference = "";
        role = "";
        dir = "0";
    }
}
