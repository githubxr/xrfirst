package org.first.product.config;

import org.first.product.api.fallback.ProductApiFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductFeignAutoConfiguration {
    @Bean
    public ProductApiFallbackFactory productApiFallbackFactory() {
        return new ProductApiFallbackFactory();
    }
}
