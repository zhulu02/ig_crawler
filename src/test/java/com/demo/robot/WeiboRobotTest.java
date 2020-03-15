
package com.demo.robot;

import com.demo.util.ImgUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>微博机器人测试类。</p>
 *
 * 创建日期 2020年03月15日
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class WeiboRobotTest {

    @Autowired
    private WeiboRobot weiboRobot;
    @Autowired
    private ImgUtil imgUtil;

    String imgFilePath ="/Users/zhulu/Desktop/tem.jpg";

    @Test
    public void downloadImg(){
        String imgUrl = "https://scontent-lax3-1.cdninstagram.com/v/t51.2885-15/e35/p1080x1080/89748782_654680138408398_2887710732132514754_n.jpg?_nc_ht=scontent-lax3-1.cdninstagram.com&_nc_cat=1&_nc_ohc=C-jrZ6JwAhEAX93lvNU&oh=c69cb5e261faaaeec0ed671b5f528953&oe=5E9B84E4";

        String proxyIp = "192.168.0.101";
        int proxyPort = 1082;
        imgUtil.downloadImg(imgFilePath,imgUrl,proxyIp,proxyPort);
    }

    @Test
    public void publish() {
        CloseableHttpClient httpclient = HttpClients.custom().build();
        String imageBase64 = imgUtil.imgToBase64(imgFilePath);
        System.out.println(imageBase64);
//        String cookie = "SINAGLOBAL=2954786304336.305.1582791328678; UOR=www.dtxw.cn,widget.weibo.com,www.bokee.net; login_sid_t=526041e07e1e608405dc82dbddf6752f; cross_origin_proto=SSL; _s_tentry=passport.weibo.com; Apache=8209914602505.04.1583561497241; ULV=1583561497251:2:1:1:8209914602505.04.1583561497241:1582791329500; SSOLoginState=1583561514; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFR-leUlTc1y420J.zCs2eM5JpX5K2hUgL.Fo-Xe0e01KqESo52dJLoI0YLxK-L1K5LBoBLxK-LBKBLBKMLxKnL1heL1KqLxKML1-2L1hBLxKqL1-eLBo2LxK-LBo5L12qLxKnL1heL1Kqt; ALF=1615783087; SCF=AoQ3DOtDYNNIq1-Vl2pz-WQYXo6qswGfvRD6FWLsjqZ6uA5h-yQoRrVDZVbr-2hh5wpRgLSfPk-0YdY8sHQ89hA.; SUB=_2A25zacFgDeRhGeNK6FES-SjOzTyIHXVQHrWorDV8PUNbmtANLRHikW9NSX9v6A7S7hPZ-M8_g5HVMTYeUWjrspfD; SUHB=05iq0E08o9s7cm; un=2584979140@qq.com; wvr=6; webim_unReadCount=%7B%22time%22%3A1584247464133%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22allcountNum%22%3A1%2C%22msgbox%22%3A0%7D";
//        String picId = weiboRobot.publishImg(imageBase64,httpclient,cookie);
//        if(StringUtils.isNotBlank(picId)){
//            String text = "haha";
//            String uid  = "5433396260";
//            weiboRobot.publishPost(picId,text,uid,cookie,httpclient);
//        }


    }

}
