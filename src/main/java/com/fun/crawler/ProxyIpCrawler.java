package com.fun.crawler;

import com.fun.util.TesseractUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.PatternMatcherInput;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * 实现描述: ProxyIp
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-16 11:41
 */
public class ProxyIpCrawler implements PageProcessor {
    private ConcurrentMap<String, String> snap = Maps.newConcurrentMap();

    @Override
    public void process(Page page) {

        if (page.getUrl().get().contains("http://www.kxdaili.com")) {
            List<String> ips = page.getHtml().xpath("//tbody/tr/td[1]/text()").all();
            List<String> ports = page.getHtml().xpath("//tbody/tr/td[2]/text()").all();
            saveRecord(ips, ports);
            addRequest(page, Lists.newArrayList("http://www\\.kxdaili\\.com/ipList/.*\\.html"));
        }

        if (page.getUrl().get().contains("http://proxy.mimvp.com/free.php")) {
            //page.addTargetRequests(page.getHtml().links().regex("http://proxy\\.mimvp\\.com/.*pageindex=\\d*").all());
            List<String> ips = page.getHtml().xpath("//tbody/tr/td[2]/text()").all();
            List<String> portImgUrls = page.getHtml().xpath("//tbody/tr/td[3]/img/@src").all();
            List<String> ports = Lists.newArrayList();
            for (String port : portImgUrls) {
                try {
                    ports.add(TesseractUtil.recognize(new URL("http://proxy.mimvp.com/" + port)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            saveRecord(ips, ports);
        }

        if (page.getUrl().get().contains("http://www.youdaili.net/Daili/http/")) {
            addRequest(page, Lists.newArrayList("http://www\\.youdaili\\.net/Daili/http/\\d+\\.html"
                    , "http://www\\.youdaili\\.net/Daili/http/list_\\d\\.html"));

            if (!page.getUrl().get().contains("list")) {
                List<String> ips = Lists.newArrayList();
                List<String> ports = Lists.newArrayList();
                String content = page.getHtml().smartContent().get();
                String s = "/(\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+)/";
                Perl5Util plUtil = new Perl5Util();
                PatternMatcherInput matcherInput = new PatternMatcherInput(content);
                while (plUtil.match(s, matcherInput)) {
                    MatchResult result = plUtil.getMatch();
                    ips.add(Splitter.on(":").splitToList(result.toString()).get(0));
                    ports.add(Splitter.on(":").splitToList(result.toString()).get(1));
                }

                saveRecord(ips, ports);
            }
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
                    String key = String.format("%s:%s%s", ip.get(i), port.get(i), "\r\n");
                    if (!snap.containsKey(key)) {
                        snap.putIfAbsent(key, StringUtils.EMPTY);
                        Files.append(String.format("%s:%s%s", ip.get(i), port.get(i), "\r\n"), new File("/Users/reeboo/ip"), Charset.forName("UTF-8"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加请求
     *
     * @param page
     * @param xpaths
     */
    private void addRequest(Page page, List<String> xpaths) {
        if (snap.size() > 100 * 10000) return;
        for (String xpath : xpaths) {
            page.addTargetRequests(page.getHtml().links().regex(xpath).all());
        }
    }

    public static void main(String[] args) throws Exception {
        Files.write("", new File("/Users/reeboo/ip"), Charset.forName("UTF-8"));
        Spider.create(new ProxyIpCrawler()).thread(10)
                .addUrl("http://www.kxdaili.com/ipList/1.html#ip")
                .addUrl("http://proxy.mimvp.com/free.php?proxy=in_tp")
                .addUrl("http://proxy.mimvp.com/free.php?proxy=in_hp")
                .addUrl("http://proxy.mimvp.com/free.php?proxy=out_tp")
                .addUrl("http://proxy.mimvp.com/free.php?proxy=out_hp")
                .addUrl("http://www.youdaili.net/Daili/http/")
                .start();
    }

}
