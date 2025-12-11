package org.first.product.api;


import org.first.comm.constant.ServerConstants;
import org.first.comm.model.CommonResponse;
import org.first.product.api.fallback.ProductApiFallbackFactory;
import org.first.product.request.CreateOrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


//！！fallbackFacotry需要sentinel依赖
@FeignClient(name = "product", value = ServerConstants.SERVER_PRODUCT, fallbackFactory = ProductApiFallbackFactory.class)
public interface ProductApi {

    @PostMapping("product/deductStock")
    CommonResponse<CreateOrderRequest> deductStock(CreateOrderRequest req);



//    @GetMapping("/{id}")
//    Order getOrderById(@PathVariable("id") Integer id);
}
