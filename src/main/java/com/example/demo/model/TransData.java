package com.example.demo.model;

import java.util.Date;
import java.util.List;

/**
 * @Auther: fujian
 * @Date: 2019/3/2 12:07
 * @Description: 封装转写结果信息
 */
public class TransData {
    private DataType  dataType;//0:合并的数据 1:来自坐席 2:来自客户
    private String extension;//分机号
    private String callerId;//主叫号码
    private String calledId;//被叫号码
    private int dir;//通话方向
    private Date startTime;//通话开始时间
    private long duration;//通话时长
    //private String role;//通话角色 AGENT:坐席 USER:客户
    private String callReference;//通话记录 流水号
    private List<Sentence> sentenceList;//通话内容语句

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCalledId() {
        return calledId;
    }

    public void setCalledId(String calledId) {
        this.calledId = calledId;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getCallReference() {
        return callReference;
    }

    public void setCallReference(String callReference) {
        this.callReference = callReference;
    }

    public List<Sentence> getSentenceList() {
        return sentenceList;
    }

    public void setSentenceList(List<Sentence> sentenceList) {
        this.sentenceList = sentenceList;
    }

    public enum DataType{
        AGENTDATA(1),USERDATA(2),MERGEDATA(0),OTHER(-1);
        private int code;
        DataType(int code){
            this.code=code;
        }
    }
}
