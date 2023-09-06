package top.rgb39.shop.entities;

import top.rgb39.shop.tools.entity.BasicEntity;
import top.rgb39.shop.tools.entity.annotation.*;

@Entity("admin")
public class AdminEntity extends BasicEntity {

    @Field(type = FieldTypes.STRING, primaryKey = true)
    public String name;

    @Field(type = FieldTypes.STRING)
    public String passwd;

    AdminEntity() {}

    @Result
    AdminEntity(
        @Prop("name") String name,
        @Prop("passwd") String passwd
    ) {
        this.name = name;
        this.passwd = passwd;
    }
}
