package org.first.order.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String id;
    private String ownerUserCode;
    private List<OrderItem> goodsList;
    private String createTime;


}
