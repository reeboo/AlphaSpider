package com.fun.crawler.ChinaSoftIVote;

import com.fun.util.HttpClientUtil;
import com.fun.util.JsonUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 实现描述: ChinaSoftIVote
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-16 14:45
 */
public class ChinaSoftIVoteHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(ChinaSoftIVoteHttpClient.class);

    public static void main(String[] args) throws Exception {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 20
                , Runtime.getRuntime().availableProcessors() * 20,
                60L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                executor.shutdownNow();
            }
        });

        final AtomicInteger totalVote = new AtomicInteger(0);
        List<String> ipFile = Files.readLines(new File("/Users/reeboo/ip"), Charset.forName("UTF-8"));
        for (final String ipPort : ipFile) {
            final Map<String, String> para = Maps.newHashMap();
            para.put("optionid", "111");
            para.put("tpid", "16");

            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        String response = HttpClientUtil.getInstance(Splitter.on(":").splitToList(ipPort).get(0), Splitter.on(":").splitToList(ipPort).get(1))
                                .post("http://enterprises.chinasourcing.org.cn/Vote/AnswerSave", para);
                        VoteResult result = JsonUtil.unmarshalFromString(response, VoteResult.class);
                        if (result == null || result.getErrcode() < 0) {
                            System.err.println(String.format("%s is fired", ipPort));
                            break;
                        }
                        totalVote.getAndIncrement();
                        if (totalVote.get() % 100 == 0)
                            System.out.println(String.format("%s request %s次", ipPort, totalVote.get()));
                    }
                }
            });
        }
    }

}
