package org.first.user.test.temp;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String orderId;
    private List<Goods> goodsList;
    private String ofUserCode;
    private String applyDate;
    private String orderStatusCode;

}
