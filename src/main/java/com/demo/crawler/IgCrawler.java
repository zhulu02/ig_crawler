
package com.demo.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Instagram 爬虫。</p>
 *
 * 创建日期 2020年03月15日
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
@Slf4j
@Service
public class IgCrawler {

    @Value("${proxy.alive}")
    private boolean alive;

    @Value("${proxy.ip}")
    private String proxyIp;

    @Value("${proxy.port}")
    private Integer proxyPort;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    /**
     * 获取源码
     * @param address
     * @param proxyIp
     * @param proxyPort
     * @return
     */
    private String crawlSource(String address, String proxyIp, Integer proxyPort) {
        //根据代理参数获取一个连接
        Connection.Response response = null;
        try {
            if (alive) {
                log.info("使用代理 {}:{} 采集 {}", proxyIp, proxyPort, address);
                response = Jsoup.connect(address).timeout(20000).method(Connection.Method.GET).proxy(proxyIp, proxyPort).validateTLSCertificates(false).ignoreContentType(true).ignoreHttpErrors(true).execute();
            } else {
                response = Jsoup.connect(address).timeout(20000).method(Connection.Method.GET).validateTLSCertificates(false).ignoreContentType(true).ignoreHttpErrors(true).execute();
            }
        } catch (IOException e) {
            log.error("请求异常，请求地址:" + address, e);
        }
        if (response != null) {
            return response.body();
        }
        return null;
    }


    /**
     * 爬取用户时间线
     * @param userName
     * @return
     */
    public String crawlTimeline(String userName) {
        String address = "https://www.instagram.com/" + userName + "/?__a=1";
        String source = crawlSource(address, proxyIp, proxyPort);

        StringBuffer html = new StringBuffer();
        if (StringUtils.isBlank(source)) {
            log.info("用户不存在：{}", address);
            html.append("用户不存在");
            return html.toString();
        }

        log.info("解析用户:{}", address);
        //解析源码
        JsonObject user = new JsonParser().parse(source).getAsJsonObject().getAsJsonObject("graphql").getAsJsonObject("user");
        JsonArray timeline = user.getAsJsonObject("edge_owner_to_timeline_media").getAsJsonArray("edges");
        for (JsonElement jsonElement : timeline) {
            JsonObject node = jsonElement.getAsJsonObject().getAsJsonObject("node");
            long time = node.get("taken_at_timestamp").getAsLong() * 1000;//发布时间
            JsonArray jsonArray = node.get("edge_media_to_caption").getAsJsonObject().getAsJsonArray("edges");
            StringBuffer textSb = new StringBuffer();
            for (JsonElement element : jsonArray) {
                String text = element.getAsJsonObject().getAsJsonObject("node").get("text").getAsString();
                textSb.append(text);
            }
            String text = textSb.toString();//文本
            String imgUrl = node.get("display_url").getAsString();//图片地址
            String postUrl = "https://www.instagram.com/p/" + node.get("shortcode").getAsString();//帖子地址
            html.append("<ul>");
            html.append("<li> 时间：").append(simpleDateFormat.format(new Date(time))).append("</li>");
            html.append("<li> 内容：").append(text).append("</li>");
            html.append("<li> <img text-align:center width=\"300\" overflow=\"hidden\" src=\"" + imgUrl + "\">").append("</li>");
            html.append("</ul>");
        }
        return html.toString();
    }


}
