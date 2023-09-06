package top.rgb39.shop.entities;

import top.rgb39.shop.tools.entity.annotation.*;
import top.rgb39.shop.tools.entity.BasicEntity;

@Entity("customer")
public class CustomerEntity extends BasicEntity {
    @Field(type = FieldTypes.STRING, primaryKey = true)
    public String id;

    @Field(type = FieldTypes.STRING)
    public String name;

    @Result
    CustomerEntity(
        @Prop("id") String id,
        @Prop("name") String name
    ) {
        this.id = id;
        this.name = name;
    }

    CustomerEntity() {}
}
