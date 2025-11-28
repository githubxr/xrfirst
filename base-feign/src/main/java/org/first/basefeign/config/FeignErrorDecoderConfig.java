package org.first.basefeign.config;

import feign.codec.ErrorDecoder;
import org.first.basefeign.decoder.GlobalFeignErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignErrorDecoderConfig {

    @Bean
    public ErrorDecoder globalFeignErrorDecoder() {
        return new GlobalFeignErrorDecoder();
    }
}
