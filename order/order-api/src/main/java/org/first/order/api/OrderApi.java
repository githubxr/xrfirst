package org.first.order.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.first.common.constant.OrderConstants;

@FeignClient(name = "order-api", value = OrderConstants.SERVICE_ORDER, fallbackFactory = SysBaseAPIFallbackFactory.class)
public interface OrderApi {

    @GetMapping("/order/hello")
    String hello();
}
