package org.first.product.controller;

import org.first.comm.model.CommonResponse;
import org.first.product.rquest.CreateOrderRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("product")
public class ProductController {

    @PostMapping("deductStock")
    public CommonResponse<String> deductStock(CreateOrderRequest req) {
        return null;
    }
}
