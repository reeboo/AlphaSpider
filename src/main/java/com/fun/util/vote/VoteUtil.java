package com.fun.util.vote;

import com.fun.util.HttpClientUtil;
import com.fun.util.JsonUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 实现描述: 刷票工具
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-16 14:45
 */
public class VoteUtil {

    static Logger logger = LoggerFactory.getLogger(VoteUtil.class);
    static final ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 20
            , Runtime.getRuntime().availableProcessors() * 20,
            60L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                executor.shutdownNow();
            }
        });
    }

    /**
     * 刷票
     *
     * @param url
     * @param paras
     * @param voteTotalCount
     * @param maxVotePerIp
     * @param voteClass
     * @return
     */
    public static <T extends VoteObject> int vote(final String url, final Map<String, String> paras, final int voteTotalCount, final int maxVotePerIp, final Class<T> voteClass) {
        final AtomicInteger totalVote = new AtomicInteger(0);
        List<String> ipFile = getProxyIpList();
        for (final String ipPort : ipFile) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < maxVotePerIp; i++) {
                        if (totalVote.get() < voteTotalCount) {
                            String response = HttpClientUtil.getInstance(Splitter.on(":").splitToList(ipPort).get(0), Splitter.on(":").splitToList(ipPort).get(1))
                                    .post(url, paras);
                            if (!JsonUtil.unmarshalFromString(response, voteClass).validateRquestResult()) {
                                logger.info(String.format("IP:%s无效,被剔除", ipPort));
                                break;
                            }
                            totalVote.getAndIncrement();
                            if (totalVote.get() / 100 == 0) {
                                logger.info(String.format("%s请求%次", url, totalVote.get()));
                            }
                        }
                    }
                }
            });
        }
        return totalVote.get();
    }

    /**
     * 获取代理ip
     *
     * @return
     * @throws java.io.IOException
     */
    private static List<String> getProxyIpList() {
        List<String> ipList = Lists.newArrayList();
        try {
            ipList = Files.readLines(new File("/Users/reeboo/ip"), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ipList;
    }

}
