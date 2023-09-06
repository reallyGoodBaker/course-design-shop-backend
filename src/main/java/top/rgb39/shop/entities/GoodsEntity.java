package top.rgb39.shop.entities;

import top.rgb39.shop.tools.entity.BasicEntity;
import top.rgb39.shop.tools.entity.annotation.*;

@Entity("goods")
public class GoodsEntity extends BasicEntity {
    @Field(type = FieldTypes.STRING, primaryKey = true)
    public String id;

    @Field(type = FieldTypes.STRING)
    public String name;

    @Field(type = FieldTypes.FLOAT)
    public Float price;

    @Field(type = FieldTypes.INT)
    public Integer stock;

    GoodsEntity() {}

    @Result
    GoodsEntity(
        @Prop("id") String id,
        @Prop("name") String name,
        @Prop("price") Float price,
        @Prop("stock") Integer stock
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
