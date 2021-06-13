package com.mrliuli.currentlimit.limit;

import java.lang.annotation.*;

/**
 * @author liu.li
 * @date 2021/4/11
 * @description
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentLimit {

    String subject() default "ip";

}
