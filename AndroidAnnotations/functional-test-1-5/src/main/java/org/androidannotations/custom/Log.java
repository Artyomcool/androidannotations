package org.androidannotations.custom;

import org.androidannotations.annotations.Decorator;
import org.androidannotations.test15.decorator.LogDecoratorHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ConcurrentHashMap;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Decorator(LogDecoratorHandler.class)
public @interface Log {

    boolean logReturnValue() default true;

    String tag() default "";

    Class<?> c() default ConcurrentHashMap.class;

}
