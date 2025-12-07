package org.first.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.first.product.entity.Inventory;


public interface InventoryMapper extends BaseMapper<Inventory> {

    //day3并发测试（原子递减版超卖）
    @Update("update xr_product_inventory set stock_count=stock_count-1 where id = #{id}")
    void day3B(String id);

}
