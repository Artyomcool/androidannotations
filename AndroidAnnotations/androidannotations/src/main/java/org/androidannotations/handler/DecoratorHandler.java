package org.androidannotations.handler;

import org.androidannotations.annotations.Decorator;
import org.androidannotations.holder.DecoratorHolder;
import org.androidannotations.process.ProcessHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class DecoratorHandler extends BaseGeneratingAnnotationHandler<DecoratorHolder> {

    public DecoratorHandler(ProcessingEnvironment processingEnvironment) {
        super(Decorator.class, processingEnvironment);
    }

    @Override
    public DecoratorHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
        return new DecoratorHolder(processHolder, annotatedElement);
    }

    @Override
    public void process(Element element, DecoratorHolder holder) throws Exception {
    }
}
