package org.first.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.first.product.entity.Inventory;


public interface InventoryMapper extends BaseMapper<Inventory> {

    //day3并发测试（原子递减版超卖）
    @Update("update xr_product_inventory set stock_count=stock_count-1 where id = #{id}")
    void day3B(String id);

    //通过商品编号查询
    @Select("select * from xr_product_inventory where goods_code=#{goodsCode}")
    Inventory selectByGoodsCode(@Param("goodsCode") String goodsCode);

    //通过乐观锁更新
    @Update("update xr_product_inventory set lock_count=lock_count+#{orderNum}, version_code=version_code+1 where goods_code = #{goodsCode} and version_code=#{versionCode}")
    int updateByLockCount(@Param("orderNum") int orderNum, @Param("goodsCode") String goodsCode, @Param("versionCode") int versionCode);
}
