package com.fun.crawler.ChinaSoftIVote;

import com.fun.util.vote.VoteObject;

/**
 * 实现描述: VoteResult
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-16 19:25
 */
public class VoteResult implements VoteObject {
    private Integer Errcode;
    private String Errmsg;
    private String RetValue;


    @Override
    public boolean validateRquestResult() {
        return this == null || this.getErrcode() < 0;
    }

    public VoteResult() {

    }

    public Integer getErrcode() {
        return Errcode;
    }

    public void setErrcode(Integer errcode) {
        Errcode = errcode;
    }

    public String getErrmsg() {
        return Errmsg;
    }

    public void setErrmsg(String errmsg) {
        Errmsg = errmsg;
    }

    public String getRetValue() {
        return RetValue;
    }

    public void setRetValue(String retValue) {
        RetValue = retValue;
    }
}
