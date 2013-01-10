package com.bowlong.netty.bio2g;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface B2Class {
	String type() default B2G.DATA; // Server, Client, Data

	String namespace() default "";

	String remark() default "";

	boolean constant() default false;
}
