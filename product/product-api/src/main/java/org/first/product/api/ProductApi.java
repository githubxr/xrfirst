package org.first.product.api;


import org.first.comm.constant.ServerConstants;
import org.first.comm.model.CommonResponse;
import org.first.product.api.fallback.ProductApiFallbackFactory;
import org.first.product.rquest.CreateOrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


//！！fallbackFacotry需要sentinel依赖
@FeignClient(name = "product", value = ServerConstants.SERVER_PRODUCT, fallbackFactory = ProductApiFallbackFactory.class)
public interface ProductApi {

    @GetMapping("/product/product/deductStock")
    CommonResponse<String> deductStock(CreateOrderRequest req);



//    @GetMapping("/{id}")
//    Order getOrderById(@PathVariable("id") Integer id);
}
