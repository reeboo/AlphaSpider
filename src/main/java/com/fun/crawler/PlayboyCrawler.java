package com.fun.crawler;

import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 实现描述:
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-03 19:26
 */
public class PlayboyCrawler implements PageProcessor {
    @Override
    public void process(Page page) {
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

        if (page.getUrl().get().contains("http://cl.axjqv.com/thread")) {
            page.addTargetRequests(page.getHtml().links().regex("http://cl\\.axjqv\\.com/thread.*page=.*").all());
            page.addTargetRequests(page.getHtml().links().regex(".*/htm_data/.*").all());
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(30).setCharset("UTF-8").setTimeOut(60 * 1000).setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31").setSleepTime(0);
    }

    public static void main(String[] args) throws Exception {
        Spider.create(new PlayboyCrawler()).thread(100).addUrl("http://cl.axjqv.com/thread0806.php?fid=22").start();
    }
}
