package com.fun.crawler.ChinaSoftIVote;

/**
 * 实现描述: VoteResult
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-16 19:25
 */
public class VoteResult {private Integer Errcode;
    private String Errmsg;
    private String RetValue;

    public VoteResult(){

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
