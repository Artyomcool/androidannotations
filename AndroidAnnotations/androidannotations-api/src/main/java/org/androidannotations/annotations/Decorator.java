package org.androidannotations.annotations;

import org.androidannotations.api.decorator.DecoratorHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Decorator {
    Class<? extends DecoratorHandler> value();
}
