package com.example.demo.model;

/**
 * Created by 86182 on 2019/8/6.
 */
public class ReceiveVoice {
    String sessionid;
    String callerNbr;
    String calledNbr;
    String chargeNbr;
    String voiceAddress;

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getCallerNbr() {
        return callerNbr;
    }

    public void setCallerNbr(String callerNbr) {
        this.callerNbr = callerNbr;
    }

    public String getCalledNbr() {
        return calledNbr;
    }

    public void setCalledNbr(String calledNbr) {
        this.calledNbr = calledNbr;
    }

    public String getChargeNbr() {
        return chargeNbr;
    }

    public void setChargeNbr(String chargeNbr) {
        this.chargeNbr = chargeNbr;
    }

    public String getVoiceAddress() {
        return voiceAddress;
    }

    public void setVoiceAddress(String voiceAddress) {
        this.voiceAddress = voiceAddress;
    }
}
