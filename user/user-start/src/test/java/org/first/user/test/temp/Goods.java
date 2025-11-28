package org.first.user.test.temp;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class Goods {

    private String goodsId;
    private String goodsCode;
    private BigDecimal originPrice;
    private String goodsName;

}
