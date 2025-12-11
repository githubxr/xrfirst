package org.first.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.first.comm.model.CommonResponse;
import org.first.product.entity.Goods;
import org.first.product.entity.Inventory;
import org.first.product.request.CreateOrderRequest;

import java.util.List;


public interface IGoodsService extends IService<Goods> {

    //获取实时价格
    List<Goods> queryCurrPriceByGoodsCode(List<String> codeList);
}
