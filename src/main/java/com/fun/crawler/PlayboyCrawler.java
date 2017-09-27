package com.fun.crawler;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

/**
 * 实现描述: just play for fun,take care of ur body.
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-03 19:26
 */
public class PlayboyCrawler implements PageProcessor {

    @Override
    public void process(Page page) {
        extractURL(page);
        addRequest(page);
    }

    @Override
    public Site getSite() {
        List<String> ips = Lists.newArrayList();
        try{
            ips = Files.readLines(new File("/Users/reeboo/ip"), Charsets.UTF_8);
        }catch (Exception e){

        }
        Random random = new Random();
        String[] ip = ips.get(random.nextInt(180)).split(":");
        return Site.me().setRetryTimes(100).setCharset("UTF-8").setTimeOut(60 * 1000)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
                //.setHttpProxy(new HttpHost(ip[0], Integer.getInteger(ip[1])))
                .setSleepTime(0);
    }

    /**
     * 添加后续请求
     *
     * @param page
     */
    private void addRequest(Page page) {
        if (page.getUrl().get().contains("http://cl.d5j.xyz/thread0806")) {
            page.addTargetRequests(page.getHtml().links().regex("http://cl\\.d5j\\.xyz/thread0806.*page=.*").all());
            page.addTargetRequests(page.getHtml().links().regex(".*/htm_data/.*").all());
        }
    }

    /**
     * 提取url
     *
     * @param page
     */
    private void extractURL(Page page) {
        if (page.getUrl().get().contains("htm_data")) {
            File file = new File("/Users/reeboo/download");
            String url = page.getHtml().xpath("//a[@style='cursor:pointer']/@onclick").get();
            if (StringUtils.isNotBlank(url) && url.contains("=")) {
                url = url.split("=")[1].replaceAll("'", "");
                if (url.contains("iframeload") && url.contains("www")) {
                    try {
                        Files.append(url + "\r\n", file, Charset.forName("UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Spider.create(new PlayboyCrawler()).thread(100).addUrl("http://cl.d5j.xyz/thread0806.php?fid=22").start();
    }
}
