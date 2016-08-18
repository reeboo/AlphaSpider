package com.fun.crawler.ChinaSoftIVote;

import com.fun.util.vote.VoteUtil;
import com.google.common.collect.Maps;

import java.util.Map;


/**
 * 实现描述: ChinaSoftIVote
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-16 14:45
 */
public class ChinaSoftIVoteHttpClient {

    public static void main(String[] args) {
        final Map<String, String> para = Maps.newHashMap();
        para.put("optionid", "111");
        para.put("tpid", "16");

        VoteUtil.vote("http://enterprises.chinasourcing.org.cn/Vote/AnswerSave"
                ,para,10000,100,VoteResult.class);
    }


}
