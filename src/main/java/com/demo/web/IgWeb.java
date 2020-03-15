
package com.demo.web;

import com.demo.crawler.IgCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>功能描述,该部分必须以中文句号结尾。</p>
 *
 * 创建日期 2020年03月15日
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
@Slf4j
@RestController
@SpringBootApplication
public class IgWeb {

    @Autowired
    public IgCrawler igCrawler;

    @RequestMapping(value = "crawlTimeline", method = RequestMethod.GET)
    public String crawlTimeline(@RequestParam(value = "username", required = true) String username) {
        return igCrawler.crawlTimeline(username);
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test() {
        log.info("收到测试");
        return "ok";
    }
}
