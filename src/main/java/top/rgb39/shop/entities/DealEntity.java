package top.rgb39.shop.entities;

import top.rgb39.shop.tools.entity.BasicEntity;
import top.rgb39.shop.tools.entity.annotation.*;

@Entity("deal")
public class DealEntity extends BasicEntity {
    @Field(type = FieldTypes.STRING, primaryKey = true)
    public String did;

    @Field(type = FieldTypes.STRING)
    public String cid;

    @Field(type = FieldTypes.STRING)
    public String detail;

    @Field(type = FieldTypes.FLOAT)
    public float cost;

    @Field(type = FieldTypes.LONG)
    public long date;

    @Result
    DealEntity(
        @Prop("did") String did,
        @Prop("cid") String cid,
        @Prop("detail") String detail,
        @Prop("cost") Float cost,
        @Prop("date") long date
    ) {
        this.cid = cid;
        this.did = did;
        this.detail = detail;
        this.cost = cost;
        this.date = date;
    }

    DealEntity() {}
}
