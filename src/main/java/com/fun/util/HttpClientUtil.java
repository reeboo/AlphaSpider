/*
 * Copyright 2013 Qunar.com All right reserved. This software is the confidential and proprietary information of
 * Qunar.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Qunar.com.
 */
package com.fun.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 实现描述：http访问客户端工具类
 *
 * @author reeboo
 * @version v1.0.0
 * @see
 * @since 2013-8-2 下午5:21:09
 */
@SuppressWarnings("deprecation")
public class HttpClientUtil extends HttpClientBuilder {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private HttpClient client;

    private static PoolingClientConnectionManager connectionManager;

    public static HttpClientUtil getInstance() {
        return new HttpClientUtil(null, null);
    }

    public static HttpClientUtil getInstance(String ip, String port) {
        return new HttpClientUtil(ip, port);
    }

    private HttpClientUtil(String ip, String port) {
        if (StringUtils.isNotBlank(ip) && NumberUtils.isDigits(port)) {
            HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            this.client = this.setRoutePlanner(routePlanner)
                    .setMaxConnPerRoute(50)
                    .setMaxConnTotal(200)
                    .build();
            return;
        }

        connectionManager = new PoolingClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(50);
        connectionManager.setMaxTotal(200);
        this.client = new DefaultHttpClient(HttpClientUtil.connectionManager);
    }


    /**
     * 请求特定的url提交表单，使用post方法，返回响应的内容
     *
     * @param url
     * @param formData 表单的键值对
     * @return
     */
    public String post(String url, Map<String, String> formData) {
        HttpPost post = new HttpPost(url);
        String content = null;
        List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
        if (formData != null && !formData.isEmpty()) {
            for (Entry<String, String> entry : formData.entrySet()) {
                nameValues.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValues, Charset.forName("UTF-8"));
            post.setEntity(formEntity);
            HttpResponse response = this.client.execute(post);
            content = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            HttpClientUtil.logger.error("post {} happens error, params: {} ", url, formData, e);
        }
        return content;
    }

    /**
     * 请求特定的url提交Json字符串，使用post方法，返回响应的内容
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String post(String url, String jsonData) {
        String content = null;
        try {
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonData, Charset.forName("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(entity);
            HttpResponse response = this.client.execute(post);
            content = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            HttpClientUtil.logger.error(String.format("post [%s] happens error ", url), e);
        }
        return content;
    }

}
