package com.example.pet_platform.config;

import com.baidu.aip.contentcensor.AipContentCensor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AipContentCensorClientConfig {
    /**
     * 百度云审核的AppID
     */
    @Value("${baidu.examine.AppID}")
    String AppID;
    /**
     * 百度云审核的Api Key
     */
    @Value("${baidu.examine.API_Key}")
    String API_Key;
    /**
     * 百度云审核的Secret Key
     */
    @Value("${baidu.examine.Secret_Key}")
    String Secret_Key;

    @Bean(name = "commonTextCensorClient")
    AipContentCensor commonTextCensorClient() {
        /**
         * 可以选择在客户端中添加参数，参考 https://ai.baidu.com/ai-doc/ANTIPORN/ik3h6xdze
         * 如：
         *         // 可选：设置网络连接参数
         *         client.setConnectionTimeoutInMillis(2000);
         *         client.setSocketTimeoutInMillis(60000);
         *
         *         // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
         *         client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
         *         client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
         *
         *         // 可选：设置log4j日志输出格式，若不设置，则使用默认配置 System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
         *         // 也可以直接通过jvm启动参数设置此环境变量
         *
         */

        return new AipContentCensor(AppID, API_Key, Secret_Key);
    }

}
