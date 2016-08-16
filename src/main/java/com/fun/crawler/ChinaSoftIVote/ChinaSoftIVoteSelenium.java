package com.fun.crawler.ChinaSoftIVote;

import com.google.common.io.Files;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 实现描述: ChinaSoftIVote
 *
 * @version v1.0.0
 * @author: reeboo
 * @since: 2016-08-16 14:45
 */
public class ChinaSoftIVoteSelenium {
    public static void main(String[] args) throws Exception {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5,
                60L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                executor.shutdownNow();
            }
        });

        List<String> ipFile = Files.readLines(new File("/Users/reeboo/ip"), Charset.forName("UTF-8"));
        for (final String ipPort : ipFile) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(ipPort);
                    //init profile
                    Proxy proxy = new Proxy();
                    proxy.setHttpProxy(ipPort);
                    DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                    capabilities.setCapability(CapabilityType.PROXY, proxy);

                    // open driver
                    FirefoxDriver driver = new FirefoxDriver(capabilities);
                    try {
                        driver.get("http://enterprises.chinasourcing.org.cn/Vote/voteindex?id=16&from=timeline&isappinstalled=0");
                        driver.manage().window().maximize();
                        WebElement element = driver.findElementByXPath("//div[@data=111]");
                        for (int i = 0; i < 200; i++) {
                            element.click();
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        // ignore
                    } finally {
                        driver.close();
                    }
                }
            });
        }

    }
}
