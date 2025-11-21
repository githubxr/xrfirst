package org.first.order.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "order-service")
public interface OrderFeign {

    @GetMapping("/order/hello")
    String hello();
}
