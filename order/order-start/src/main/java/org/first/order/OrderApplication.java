package org.first.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @since 2025/11/21
 * @description 订单程序
 * @author 25054
 * */

@EnableFeignClients(basePackages = {"org.first"})//注意这个得包括别的模块的！
@SpringBootApplication(scanBasePackages = {
        "org.first.order" //自己的bean
        //,"org.first.user.api.fallback" //要feign user的fallback bean
})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
