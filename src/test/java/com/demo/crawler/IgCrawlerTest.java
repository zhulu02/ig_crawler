

package com.demo.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Instagram爬虫测试类。</p>
 *
 * 创建日期 2020年03月15日
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
public class IgCrawlerTest {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    /**
     * 爬取个人主页
     */
    @Test
    public void crawlTimeline() {

        String addr = "https://www.instagram.com/katyperry/?__a=1";
        String proxyIp = "192.168.0.101";
        int proxyPort = 1082;

        //获取ig用户时间线源码
        String source = null;
        try {
            Connection.Response response = Jsoup.connect(addr)
                    .timeout(20000)
                    .method(Connection.Method.GET)
                    .proxy(proxyIp, proxyPort)
                    .validateTLSCertificates(false)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
            if (response != null) {
                source = response.body();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (source == null) {
            return;
        }


        //解析源码
        JsonObject user = new JsonParser().parse(source).getAsJsonObject().getAsJsonObject("graphql").getAsJsonObject("user");
        JsonArray timeline = user.getAsJsonObject("edge_owner_to_timeline_media").getAsJsonArray("edges");
        for (JsonElement jsonElement : timeline) {
            JsonObject node = jsonElement.getAsJsonObject().getAsJsonObject("node");
            long time = node.get("taken_at_timestamp").getAsLong()*1000;//发布时间
            JsonArray jsonArray = node.get("edge_media_to_caption").getAsJsonObject().getAsJsonArray("edges");
            StringBuffer textSb = new StringBuffer();
            for (JsonElement element : jsonArray) {
                String text = element.getAsJsonObject().getAsJsonObject("node").get("text").getAsString();
                textSb.append(text);
            }
            String text = textSb.toString();//文本
            String imgUrl = node.get("display_url").getAsString();//图片地址
            String postUrl = "https://www.instagram.com/p/" + node.get("shortcode").getAsString();//帖子地址
            System.out.println();
            System.out.println(simpleDateFormat.format(new Date(time)));
            System.out.println(postUrl);
            System.out.println(text);
            System.out.println(imgUrl);
        }
    }
}
