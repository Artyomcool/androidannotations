package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.Collections;

import static com.sun.codemodel.JExpr.dotclass;

public class DecoratorHolder extends BaseGeneratedClassHolder {
    public DecoratorHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
        super(processHolder, annotatedElement);
        setConstructor();
    }

    @Override
    protected void addAnnotations() {
    }

    @Override
    protected void setExtends() {
        String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
        JClass annotatedComponent = codeModel().directClass(annotatedComponentQualifiedName);
        generatedClass._implements(annotatedComponent);
    }

    private void setConstructor() {
        JDefinedClass generatedClass = getGeneratedClass();
        JMethod constructor = generatedClass.constructor(JMod.PUBLIC);
        for (ExecutableElement method : ElementFilter.methodsIn(annotatedElement.getEnclosedElements())) {
            String name = method.getSimpleName().toString();
            JType returnType = codeModelHelper.getReturnType(method, this, Collections.<String, TypeMirror>emptyMap());
            JMethod override = generatedClass.method(JMod.PUBLIC, returnType, name);
            override.annotate(Override.class);
            JFieldVar field = generatedClass.field(JMod.PRIVATE | JMod.FINAL, returnType, name);
            JVar param = constructor.param(returnType, name + ModelConstants.GENERATION_SUFFIX);
            constructor.body().assign(field, param);
            override.body()._return(field);
        }

        JMethod annotationType = generatedClass.method(JMod.PUBLIC, codeModel().ref(Class.class).narrow(getAnnotatedClass()), "annotationType");
        annotationType
                .body()
                ._return(dotclass(getAnnotatedClass()));
        annotationType.annotate(Override.class);
    }
}
