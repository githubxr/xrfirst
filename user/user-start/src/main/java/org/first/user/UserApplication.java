package org.first.user;

import org.first.order.api.fallback.OrderApiFallbackFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @since 2025/11/21
 * @description 账户和鉴权管理
 * @author 25054
 * */


@EnableFeignClients(
        basePackages = {
                "org.first"
                //,"org.first.order.api.fallback"
        }
        // 显式指定需要加载的FallbackFactory（关键！）
        // basePackageClasses = {
        //         OrderApiFallbackFactory.class
        //         //,用逗号隔离要调用的所有 微服务的 fallback； 自己不会调用自己 的微服务，所以不用添加自己的fallback
        // }
)//注意这个得包括别的模块的！
@SpringBootApplication//(scanBasePackages = {"org.first.user","org.first.order.api.fallback"})
// 精准扫描FallbackFactory所在包，不扫描其他无关包
@ComponentScan(
        basePackages = {
                "org.first.user"
                ,"org.first.comm" //统一添加comm的包范围
                //,"org.first.order.config" //测试
                //,"org.first.order.api.fallback"
        } // 仅扫描Order的Fallback包
        // 若有多个服务，追加其他Fallback包："org.first.system.api.fallback"
        ,lazyInit = true // 可选：懒加载，减少启动资源占用
)
public class UserApplication {
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
