package org.first.order.entity;

import lombok.Data;

@Data
public class OrderItem {
    private String id;
    private String goodsCode;
    private int goodsNum;
}
