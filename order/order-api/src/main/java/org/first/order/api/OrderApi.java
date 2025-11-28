package org.first.order.api;


import org.first.order.api.fallback.OrderApiFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.first.basecore.constant.OrderConstants;


//！！fallbackFacotry需要sentinel依赖
@FeignClient(name = "order", value = OrderConstants.SERVICE_ORDER, fallbackFactory = OrderApiFallbackFactory.class)
public interface OrderApi {

    @GetMapping("/order/demoBusi/hello")
    String hello();



//    @GetMapping("/{id}")
//    Order getOrderById(@PathVariable("id") Integer id);
}
