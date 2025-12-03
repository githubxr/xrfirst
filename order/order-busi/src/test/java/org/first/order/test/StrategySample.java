package org.first.order.test;

import java.util.*;
//import java.util.List;


/**
 * @since 2025/08/25
 * 最简策略模式核心实现
 * */

//菜单
class MenuItem {
    private String name;
    private int price;

    public MenuItem(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}



//套餐
class ComboMeal {
    private String name;
    private List<MenuItem> items;
    private int comboPrice;

    public ComboMeal(String name, List<MenuItem> items, int comboPrice) {
        this.name = name;
        this.items = items;
        this.comboPrice = comboPrice;
    }

    public int getPrice() {
        return comboPrice;
    }

    public String getName() {
        return name;
    }
}


//订单
class Order {
    private List<MenuItem> items = new ArrayList<>();
    private List<ComboMeal> combos = new ArrayList<>();

    public void addItem(MenuItem item) {
        items.add(item);
    }

    public void addCombo(ComboMeal combo) {
        combos.add(combo);
    }

    public int calculateRawPrice() {
        int total = 0;
        for (MenuItem item : items) total += item.getPrice();
        for (ComboMeal combo : combos) total += combo.getPrice();
        return total;
    }

    public int calculateFinalPrice(PricingStrategy strategy) {
        return strategy.calculatePrice(this);
    }
}


//策略接口
interface PricingStrategy {
    int calculatePrice(Order order);
}

//工作日策略
class WeekdayPricingStrategy implements PricingStrategy {
    public int calculatePrice(Order order) {
        return order.calculateRawPrice(); // 原价无折扣
    }
}

//节假日策略
class HolidayPricingStrategy implements PricingStrategy {
    public int calculatePrice(Order order) {
        int raw = order.calculateRawPrice();
        if (raw >= 100) {
            return raw - 20; // 举个栗子：满100减20
        } else if (raw >= 50) {
            return raw - 10; // 满50减10
        }
        return raw;
    }
}





//测试程序入口
public class Test {

    public static void main(String[] args) {
        //RedisTemplate<String, String> r = null;

        // 菜单单品
        MenuItem niurouBing = new MenuItem("牛肉饼", 10);
        MenuItem naicha = new MenuItem("奶茶", 12);
        MenuItem feichangMianZhong = new MenuItem("中碗肥肠面", 18);

        // 套餐1（大碗牛肉面 + 牛肉饼 + 奶茶）38元
        ComboMeal combo1 = new ComboMeal("套餐1", Arrays.asList(
                new MenuItem("大碗牛肉面", 18), niurouBing, naicha), 38);

        // 张三的订单：套餐1 + 一个牛肉饼
        Order zhangsan = new Order();
        zhangsan.addCombo(combo1);
        zhangsan.addItem(niurouBing);

        // 李四的订单：中碗肥肠面 + 两杯奶茶
        Order lisi = new Order();
        lisi.addItem(feichangMianZhong);
        lisi.addItem(naicha);
        lisi.addItem(naicha);

        // 使用工作日策略（节假日的话可替换为 HolidayPricingStrategy）
        PricingStrategy strategy = new WeekdayPricingStrategy();

        System.out.println("张三需支付：" + zhangsan.calculateFinalPrice(strategy) + " 元");
        System.out.println("李四需支付：" + lisi.calculateFinalPrice(strategy) + " 元");
    }

}
