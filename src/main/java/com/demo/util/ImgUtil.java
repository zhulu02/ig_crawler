/*
 * Copyright © 2018 Hunan Antdu Software Co., Ltd.. All rights reserved.
 * 版权所有©2018湖南蚁为软件有限公司。保留所有权利。
 */

package com.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.*;

/**
 * <p>图片工具类。</p>
 *
 * 创建日期 2020年03月15日
 * @author Lu Zhu(zhulu@eefung.com)
 * @since $version$
 */
@Slf4j
@Component
public class ImgUtil {


    /**
     * 下载图片到指定目录
     * @param imgFilePath
     * @param imgUrl
     * @param proxyIp
     * @param proxyPort
     * @return
     */
    public boolean downloadImg(String imgFilePath, String imgUrl, String proxyIp, Integer proxyPort) {
        try {
            File file = new File(imgFilePath);
            if (file.exists()) {
                return true;
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            URL url = new URL(imgUrl);
            URLConnection conn ;
            //根据代理参数获取一个连接
            if (StringUtils.isNotBlank(proxyIp) && proxyPort != null) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, proxyPort));
                conn = url.openConnection(proxy);
            } else {
                conn = url.openConnection();
            }
            conn.setConnectTimeout(5000);
            DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(imgFilePath));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            return true;
        } catch (MalformedURLException e) {
            log.error("下载ig图片失败", e);
        } catch (IOException e) {
            log.error("下载ig图片失败", e);
        }
        return false;
    }

    /**
     * 将图片转成base64
     *
     * @param imgFilePath
     * @return
     */
    public String imgToBase64(String imgFilePath) {
        InputStream inputStream = null;
        byte[] data = null;
        String base64 = null;
        try {
            inputStream = new FileInputStream(imgFilePath);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // 加密
            base64 = new BASE64Encoder().encode(data);
        } catch (Exception e) {
            log.error("图片转成base64异常", e);
        }
        return base64;
    }
}
