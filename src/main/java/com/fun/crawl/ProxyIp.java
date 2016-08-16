package com.fun.crawl;

import com.google.common.io.Files;
import org.apache.commons.lang.math.NumberUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 实现描述: ProxyIp
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-16 11:41
 */
public class ProxyIp implements PageProcessor {
    @Override
    public void process(Page page) {

        if (page.getUrl().get().contains("http://www.kxdaili.com")) {
            List<String> ip = page.getHtml().xpath("//tbody/tr/td[1]/text()").all();
            List<String> port = page.getHtml().xpath("//tbody/tr/td[2]/text()").all();
            saveRecord(ip, port);
            page.addTargetRequests(page.getHtml().links().regex("http://www\\.kxdaili\\.com/ipList/.*\\.html").all());
        }

        if (page.getUrl().get().contains("http://proxy.mimvp.com/free.php")) {
            page.addTargetRequests(page.getHtml().links().regex("http://proxy\\.mimvp\\.com/.*pageindex=\\d*").all());
            List<String> ip = page.getHtml().xpath("//tbody/tr/td[2]/text()").all();
            List<String> port = page.getHtml().xpath("//tbody/tr/td[3]/text()").all();
            saveRecord(ip, port);
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setCharset("UTF-8").setTimeOut(60 * 1000)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
                .setSleepTime(0);
    }

    /**
     * 保存记录
     *
     * @param ip
     * @param port
     */
    private void saveRecord(List<String> ip, List<String> port) {
        for (int i = 0; i < ip.size(); i++) {
            if (NumberUtils.isDigits(port.get(i))) {
                try {
                    Files.append(String.format("%s:%s%s", ip.get(i), port.get(i), "\r\n"), new File("/Users/reeboo/ip"), Charset.forName("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Spider.create(new ProxyIp()).thread(10)
                .addUrl("http://www.kxdaili.com/ipList/1.html#ip")
                .addUrl("http://proxy.mimvp.com/free.php?proxy=out_tp&sort=&pageindex=1")
                .start();
    }

}
