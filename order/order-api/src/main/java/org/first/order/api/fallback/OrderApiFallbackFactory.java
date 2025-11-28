package org.first.order.api.fallback;

import org.first.order.api.OrderApi;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

//@Component
public class OrderApiFallbackFactory implements FallbackFactory<OrderApi> {

    @Override
    public OrderApi create(Throwable cause) {
        return new OrderApi() {
            @Override
            public String hello() {
                System.out.println("Feign调用失败：" + cause.getMessage());
                return "feign失败默认返回数据";
            }
        };
    }
}