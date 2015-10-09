package com.xinghao.TestHarnEss;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
	public String name();
	public Class<?> type();
    public String value();
}
