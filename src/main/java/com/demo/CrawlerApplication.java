/*
 * Copyright © 2018 Hunan Antdu Software Co., Ltd.. All rights reserved.
 * 版权所有©2018湖南蚁为软件有限公司。保留所有权利。
 */

package com.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * <p>采集服务启动类。</p>
 *
 * 创建日期 2020年03月15日
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
@Slf4j
@SpringBootApplication
public class CrawlerApplication {

    public static void main(String[] args) {
        String className = CrawlerApplication.class.getSimpleName();
        log.info("{} starting。。。", className);
        SpringApplication app = new SpringApplicationBuilder(CrawlerApplication.class)
                .bannerMode(Banner.Mode.OFF).build();
        app.run(args);

        log.info("{} started successful。。。", className);
    }
}
