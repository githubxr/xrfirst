package org.first.basefeign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.first.basefeign.exception.CommonException;
import org.first.basefeign.model.CommonResponse;

import java.io.IOException;
import java.io.InputStream;

public class GlobalFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        try (InputStream bodyStream = response.body().asInputStream()) {

            // 尝试解析成 CommonResponse
            CommonResponse<?> error = objectMapper.readValue(bodyStream, CommonResponse.class);

            return new CommonException(error.getCode(), error.getMessage());

        } catch (IOException e) {

            // 如果不是我们定义的格式，兜底处理
            return new CommonException(response.status(),
                    "Feign调用失败: " + response.reason());
        }
    }
}
