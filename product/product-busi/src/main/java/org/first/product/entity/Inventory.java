package org.first.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 库存表
 * */
@Data
@TableName(value = "xr_product_inventory")
public class Inventory {
    //@TableId(type = IdType.AUTO)数据库自增
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String invCode;
    private String goodsCode;
    private int stockCount;
    private int lockCount;
    private String createTime;
    private String createBy;
    private String updateTime;
    private int deleteFlag;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(100);
        sb.append("id=").append(this.id)
                .append(", invCode=").append(this.invCode)
                .append(", goodsCode=").append(this.goodsCode)
                .append(", stockCount=").append(this.stockCount)
                .append(", lockCount=").append(this.lockCount)
                .append(", createTime=").append(this.createTime);
        return sb.toString();
    }

}
