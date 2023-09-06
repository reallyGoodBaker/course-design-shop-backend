package top.rgb39.shop.tools.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {
    FieldTypes type() default FieldTypes.STRING;
    String field() default "unset";
    boolean primaryKey() default false;
}