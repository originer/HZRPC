package hzr.common.spi.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {
    /**
     * 缺省扩展点名。
     */
    String value() default "";
}
