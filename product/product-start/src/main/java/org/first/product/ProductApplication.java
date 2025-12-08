package org.first.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @since 25/12/04
 * @author xr
 * @description
 * */
@EnableFeignClients(basePackages = {"org.first"})//注意这个得包括别的模块的！
@SpringBootApplication(scanBasePackages = {
        "org.first.product" //自己的bean
        ,"org.first.comm" //统一添加comm的包范围
        //,"org.first.user.api.fallback" //要feign user的fallback bean
})
@EnableDiscoveryClient
//启动类/配置类开启暴露代理
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("org.first.product.mapper")
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}



