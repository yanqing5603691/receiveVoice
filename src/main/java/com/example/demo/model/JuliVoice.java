package com.example.demo.model;

/**
 * Created by yanqing on 2019/11/6.
 */
public class JuliVoice {
    String callid;
    String callerNbr;
    String calledNbr;
    String tenantAccount;
    String voiceAddress;

    public String getCallid() {
        return callid;
    }

    public void setCallid(String callid) {
        this.callid = callid;
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

    public String getTenantAccount() {
        return tenantAccount;
    }

    public void setTenantAccount(String tenantAccount) {
        this.tenantAccount = tenantAccount;
    }

    public String getVoiceAddress() {
        return voiceAddress;
    }

    public void setVoiceAddress(String voiceAddress) {
        this.voiceAddress = voiceAddress;
    }
}
