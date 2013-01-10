package com.bowlong.netty.bio2g;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface B2Method {
	String type() default B2G.SERVER;

	String[] params();

	String remark() default "";
}
