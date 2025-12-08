package org.first.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.first.comm.model.CommonResponse;
import org.first.product.entity.Inventory;
import org.first.product.request.CreateOrderRequest;


public interface IInventoryService extends IService<Inventory> {

    CommonResponse<String>  deductStock(CreateOrderRequest req);
}
