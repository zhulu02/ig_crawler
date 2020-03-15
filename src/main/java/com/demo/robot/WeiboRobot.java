/*
 * Copyright © 2018 Hunan Antdu Software Co., Ltd.. All rights reserved.
 * 版权所有©2018湖南蚁为软件有限公司。保留所有权利。
 */

package com.demo.robot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>微博机器人。</p>
 *
 * 创建日期 2020年03月15日
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
@Slf4j
@Component
public class WeiboRobot {

    /**
     * 发布图片到微博
     *
     * @param imageBase64
     * @return
     */
    public String publishImg(String imageBase64, CloseableHttpClient httpclient, String cookie) {

        //构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        headers.put("Cookie", cookie);
        headers.put("Host", "picupload.service.weibo.com");
        headers.put("Referer", "http://js.t.sinajs.cn/t5/home/static/swf/MultiFilesUpload.swf?version=02930bfb0c03c58f");
        headers.put("Origin", "http://js.t.sinajs.cn");
        headers.put("Content-Type", "application/octet-stream");

        //设置图片参数
        ByteArrayEntity image = new ByteArrayEntity(Base64.decodeBase64(imageBase64));
        image.setContentType("application/octet-stream");

        //构建地址
        StringBuffer addr = new StringBuffer();
        addr.append("http://picupload.service.weibo.com/interface/pic_upload.php?app=miniblog&data=1")
                .append("&url=0&markpos=1&logo=&nick=0&marks=1")
                .append("&mime=image/jpeg&ct=" + Math.random()); //图片去水印地址
        //执行图片上传
        String repose = post(addr.toString(), headers, image, httpclient);
        //处理上传结果
        if (StringUtils.isNotBlank(repose) && repose.contains("{") && repose.contains("}")) {
            String json = repose.substring(repose.indexOf("{"), repose.lastIndexOf("}") + 1);
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            String code = jsonObject.get("code").getAsString();
            if ("A00006".equals(code)) {
                String pid = jsonObject.getAsJsonObject("data").getAsJsonObject("pics").getAsJsonObject("pic_1").get("pid").getAsString();
                return pid;
            } else if ("A20001".equals(code)) {
                log.warn("cookie过期,需要重新登录");
            }
        }
        log.info("图片发布结果:{}", repose);
        return null;
    }


    /**
     * 发布微博帖子
     *
     * @param picId
     * @param text
     */
    public boolean publishPost(String picId, String text, String uid, String cookie, CloseableHttpClient httpclient) {

        //设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "weibo.com");
        headers.put("Origin", "https://weibo.com");
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        headers.put("Accept", "*/*");
        headers.put("Referer", "https://weibo.com/u/" + uid + "/home?topnav=1&wvr=6");
        headers.put("Cookie", cookie);


        //设置请求参数
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("appkey", ""));
        nvps.add(new BasicNameValuePair("style_type", "1"));
        nvps.add(new BasicNameValuePair("pic_id", ""));
        if (StringUtils.isNotBlank(text)) {
            nvps.add(new BasicNameValuePair("text", text));
        }
        nvps.add(new BasicNameValuePair("pdetail", ""));
        nvps.add(new BasicNameValuePair("rank", "0"));
        nvps.add(new BasicNameValuePair("rankid", ""));
        nvps.add(new BasicNameValuePair("module", "stissue"));
        nvps.add(new BasicNameValuePair("pub_type", "dialog"));
        nvps.add(new BasicNameValuePair("pic_id", picId));
        nvps.add(new BasicNameValuePair("pub_source", "main_"));
        nvps.add(new BasicNameValuePair("_t", "0"));


        String addr = "https://weibo.com/p/aj/v6/mblog/add?domain=100505&ajwvr=6&__rnd=" + System.currentTimeMillis();
        String repose = null;
        try {
            Object entity = new UrlEncodedFormEntity(nvps, "UTF-8");
            repose = post(addr, headers, entity, httpclient);//执行请求
        } catch (UnsupportedEncodingException e) {
            log.error("设置参数失败", e);
        }

        if (StringUtils.isNotBlank(repose) && repose.startsWith("{") && repose.endsWith("}")) {
            String code = new JsonParser().parse(repose).getAsJsonObject().get("code").getAsString();
            if ("100001".equals(code)) {// 发太多微博
            } else if ("100000".equals(code)) {
                return true;
            }
        }
        return false;
    }


    /**
     * post请求
     *
     * @param postUrl
     * @param headers
     * @param entity
     * @return
     */
    private String post(String postUrl, Map<String, String> headers, Object entity, CloseableHttpClient httpclient) {


        // 设置请求超时
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)                        // 设置连接超时时间,单位毫秒。
                .setConnectionRequestTimeout(1000)            // 从连接池获取到连接的超时,单位毫秒。
                .setSocketTimeout(5000).build();        // 请求获取数据的超时时间,单位毫秒; 如果访问一个接口,多少时间内无法返回数据,就直接放弃此次调用。

        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            httpPost = new HttpPost(postUrl);
            httpPost.setConfig(requestConfig);
            if (headers != null) {// 设置请求头参数
                for (String s : headers.keySet()) {
                    httpPost.setHeader(s, headers.get(s));
                }
            }
            if (entity instanceof UrlEncodedFormEntity) {
                httpPost.setEntity((UrlEncodedFormEntity) entity);
            } else if (entity instanceof ByteArrayEntity) {
                httpPost.setEntity((ByteArrayEntity) entity);
            }
            httpPost.setConfig(requestConfig);
            response = httpclient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());

        } catch (Exception e) {
            log.error("请求异常", e);
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭响应异常", e);
                }
            }
        }
        return result;

    }
}
