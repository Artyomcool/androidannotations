package org.androidannotations.custom;

import org.androidannotations.annotations.Decorator;
import org.androidannotations.api.decorator.DecoratorHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Decorator(IgnoreExceptionsHandler.class)
public @interface IgnoreExceptions {

}
