package org.first.product.request;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description 前端下单传来的订单信息 & 库存检查完毕后，填入实时单价和计算的总价
 * @remark 快速练习模式，后续需要改为 前端只传标识符，实际的订单商品数量折扣价格都得后台查询计算
 * @since 2025/12/07
 * */
@Data
public class CreateOrderRequest {
    private String orderCode;
    private String userCode;
    private List<OrderItemReq> items;
    private BigDecimal totalAmount;//返回时携带的计算总价

    @Data
    public static class OrderItemReq {
        private String goodsCode;
        private Integer goodsNum;
        private BigDecimal priceSnapshot;//返回时携带的实时价格
    }

}
