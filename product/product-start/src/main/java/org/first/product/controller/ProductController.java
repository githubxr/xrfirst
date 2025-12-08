package org.first.product.controller;

import org.first.comm.model.CommonResponse;
import org.first.product.request.CreateOrderRequest;
import org.first.product.service.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("product")
public class ProductController {
    @Autowired
    private IInventoryService inventoryService;

    @PostMapping("deductStock")
    @ResponseBody
    public CommonResponse<String> deductStock(@RequestBody CreateOrderRequest req) {
        CommonResponse res = inventoryService.deductStock(req);

        return res;
    }
}
