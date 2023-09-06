package top.rgb39.shop.services;

import com.google.gson.Gson;
import top.rgb39.shop.providers.DatabaseProvider;
import top.rgb39.shop.tools.injectable.annotation.Identifier;
import top.rgb39.shop.tools.injectable.annotation.Inject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class ShoppingService {

    Gson gson = new Gson();
    DatabaseProvider db;
    GoodsService goodsService;
    CustomerService customerService;
    DealService dealService;

    @Identifier(id = "ShoppingService")
    ShoppingService(
        @Inject("DatabaseProvider") DatabaseProvider db,
        @Inject("GoodsService") GoodsService goodsService,
        @Inject("CustomerService") CustomerService customerService,
        @Inject("DealService") DealService dealService
    ) {
        this.db = db;
        this.goodsService = goodsService;
        this.customerService = customerService;
        this.dealService = dealService;
    }

    public boolean doShopping(List<List<Object>> list, String cid)
            throws SQLException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException
    {
        String uuid = UUID.randomUUID().toString();
        List<Map<String, Object>> goodsList = new ArrayList<>();
        long date = new Date().getTime();
        double cost = 0;

        for (List<Object> entry: list) {
            Map<String, Object> info = new HashMap<>();
            var goods = goodsService.find((String) entry.get(0));
            var amount = ((Double) entry.get(1)).intValue();
            if (goods.stock < amount) {
                return false;
            }
            goodsService.update(goods.id, goods.name, goods.price, goods.stock - amount);
            info.put("item", goods);
            info.put("amount", amount);
            goodsList.add(info);
            cost += goods.price * amount;
        }

        String detail = gson.toJson(goodsList);
        if (!cid.isBlank()) {
            if (customerService.exists(cid)) {
                cost = Math.ceil(cost * 90) / 100;
            } else {
                customerService.add(cid, "unset");
            }
        }

        dealService.add(uuid, cid, detail, (float) cost, date);
        return true;
    }

}
