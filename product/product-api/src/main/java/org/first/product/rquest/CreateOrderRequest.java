package org.first.product.rquest;


import lombok.Data;

import java.util.List;

/**
 * @description 前端下单传来的订单信息
 * @remark 快速练习模式，后续需要改为 前端只传标识符，实际的订单商品数量折扣价格都得后台查询计算
 * @since 2025/12/07
 * */
@Data
public class CreateOrderRequest {
    private String orderCode;
    private String userCode;
    private List<OrderItemReq> items;

    @Data
    public static class OrderItemReq {
        private String goodsCode;
        private Integer num;
    }

}
