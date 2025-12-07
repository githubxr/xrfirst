package org.first.product.api.fallback;


import org.first.comm.model.CommonResponse;
import org.first.product.api.ProductApi;
import org.first.product.rquest.CreateOrderRequest;
import org.springframework.cloud.openfeign.FallbackFactory;

public class ProductApiFallbackFactory implements FallbackFactory<ProductApi> {

    @Override
    public ProductApi create(Throwable cause) {
        return new ProductApi() {
            @Override
            public CommonResponse<String> deductStock(CreateOrderRequest req) {
                String err = "Feign调用失败：" + cause.getMessage();
                //后面改为log4j打印日志
                //log.error(err);
                System.out.println(err);
                return CommonResponse.error(err, null);
            }
        };
    }

}
