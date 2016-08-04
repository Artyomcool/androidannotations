/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;

import com.sun.codemodel.JCodeModel;
import org.androidannotations.exception.ProcessingException;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElements.AnnotatedAndRootElements;

public class ModelProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelProcessor.class);

	public static class ProcessResult {

		public final JCodeModel codeModel;
		public final OriginatingElements originatingElements;
		public final Set<Class<?>> apiClassesToGenerate;

		public ProcessResult(//
				JCodeModel codeModel, //
				OriginatingElements originatingElements, //
				Set<Class<?>> apiClassesToGenerate) {

			this.codeModel = codeModel;
			this.originatingElements = originatingElements;
			this.apiClassesToGenerate = apiClassesToGenerate;
		}
	}

	private final ProcessingEnvironment processingEnv;
	private final AnnotationHandlers annotationHandlers;

	private final List<Runnable> generatingPostProcessors = new ArrayList<Runnable>();

	public ModelProcessor(ProcessingEnvironment processingEnv, AnnotationHandlers annotationHandlers) {
		this.processingEnv = processingEnv;
		this.annotationHandlers = annotationHandlers;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ProcessResult process(AnnotationElements validatedModel) throws ProcessingException, Exception {
		ProcessHolder processHolder = new ProcessHolder(processingEnv);

		annotationHandlers.setProcessHolder(processHolder);

		LOGGER.info("Processing root elements");

		/*
		 * We generate top classes then inner classes, then inner classes of
		 * inner classes, etc... until there is no more classes to generate.
		 */
		while (generateElements(validatedModel, processHolder)) {
			// CHECKSTYLE:OFF
			;
			// CHECKSTYLE:ON
		}

		LOGGER.info("Processing enclosed elements");

		for (AnnotationHandler annotationHandler : annotationHandlers.getDecorating()) {
			String annotationName = annotationHandler.getTarget();

			/*
			 * For ancestors, the annotationHandler manipulates the annotated
			 * elements, but uses the holder for the root element
			 */
			Set<AnnotatedAndRootElements> ancestorAnnotatedElements = validatedModel.getAncestorAnnotatedElements(annotationName);

			if (!ancestorAnnotatedElements.isEmpty()) {
				LOGGER.debug("Processing enclosed elements with {}: {}", annotationHandler.getClass().getSimpleName(), ancestorAnnotatedElements);
			}

			for (AnnotatedAndRootElements elements : ancestorAnnotatedElements) {
				GeneratedClassHolder holder = processHolder.getGeneratedClassHolder(elements.rootTypeElement);
				/*
				 * Annotations coming from ancestors may be applied to root
				 * elements that are not validated, and therefore not available.
				 */
				if (holder != null) {
					preProcessThrowing(annotationHandler, elements.annotatedElement, holder);
					processThrowing(annotationHandler, elements.annotatedElement, holder);
				}
			}

			Set<? extends Element> rootAnnotatedElements = validatedModel.getRootAnnotatedElements(annotationName);

			processAnnotatedElements(processHolder, annotationHandler, rootAnnotatedElements, new ElementProcessor() {
				@Override
				public void process(AnnotationHandler handler, Element element, GeneratedClassHolder generatedClassHolder) throws ProcessingException {
					preProcessThrowing(handler, element, generatedClassHolder);
				}
			});

			processAnnotatedElements(processHolder, annotationHandler, rootAnnotatedElements, new ElementProcessor() {
				@Override
				public void process(AnnotationHandler handler, Element element, GeneratedClassHolder generatedClassHolder) throws ProcessingException {
					processThrowing(handler, element, generatedClassHolder);
				}
			});

			processAnnotatedElements(processHolder, annotationHandler, rootAnnotatedElements, new ElementProcessor() {
				@Override
				public void process(AnnotationHandler handler, Element element, GeneratedClassHolder generatedClassHolder) throws ProcessingException {
					postProcessThrowing(handler, element, generatedClassHolder);
				}
			});


			for (AnnotatedAndRootElements elements : ancestorAnnotatedElements) {
				GeneratedClassHolder holder = processHolder.getGeneratedClassHolder(elements.rootTypeElement);
				/*
				 * Annotations coming from ancestors may be applied to root
				 * elements that are not validated, and therefore not available.
				 */
				if (holder != null) {
					postProcessThrowing(annotationHandler, elements.annotatedElement, holder);
				}
			}
		}

		for (Runnable postProcessor : generatingPostProcessors) {
			postProcessor.run();
		}

		return new ProcessResult(//
				processHolder.codeModel(), //
				processHolder.getOriginatingElements(), //
				processHolder.getApiClassesToGenerate());
	}

	private <T extends GeneratedClassHolder>
	void processAnnotatedElements(ProcessHolder processHolder, AnnotationHandler<T> annotationHandler,
								  Set<? extends Element> rootAnnotatedElements, ElementProcessor preprocessor) throws ProcessingException {
		for (Element annotatedElement : rootAnnotatedElements) {

			Element enclosingElement;
			if (annotatedElement instanceof TypeElement) {
				enclosingElement = annotatedElement;
			} else {
				enclosingElement = annotatedElement.getEnclosingElement();
			}

            /*
             * We do not generate code for elements belonging to abstract
             * classes, because the generated classes are final anyway
             */
			if (!isAbstractClass(enclosingElement)) {
				GeneratedClassHolder holder = processHolder.getGeneratedClassHolder(enclosingElement);

                /*
                 * The holder can be null if the annotated holder class is
                 * already invalidated.
                 */
				if (holder != null) {
					preprocessor.process(annotationHandler, annotatedElement, holder);
				}
			} else {
				LOGGER.trace("Skip element {} because enclosing element {} is abstract", annotatedElement, enclosingElement);
			}
		}
	}

	private <T extends GeneratedClassHolder> void preProcessThrowing(AnnotationHandler<T> handler, Element element, T generatedClassHolder) throws ProcessingException {
		try {
			handler.preProcess(element, generatedClassHolder);
		} catch (Exception e) {
			throw new ProcessingException(e, element);
		}
	}

	private <T extends GeneratedClassHolder> void processThrowing(AnnotationHandler<T> handler, Element element, T generatedClassHolder) throws ProcessingException {
		try {
			handler.process(element, generatedClassHolder);
		} catch (Exception e) {
			throw new ProcessingException(e, element);
		}
	}

	private <T extends GeneratedClassHolder> void postProcessThrowing(AnnotationHandler<T> handler, Element element, T generatedClassHolder) throws ProcessingException {
		try {
			handler.postProcess(element, generatedClassHolder);
		} catch (Exception e) {
			throw new ProcessingException(e, element);
		}
	}

	private boolean isAbstractClass(Element annotatedElement) {
		if (annotatedElement instanceof TypeElement) {
			TypeElement typeElement = (TypeElement) annotatedElement;

			return typeElement.getKind() == ElementKind.CLASS && typeElement.getModifiers().contains(Modifier.ABSTRACT);
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean generateElements(AnnotationElements validatedModel, ProcessHolder processHolder) throws Exception {
		boolean isElementRemaining = false;
		for (final GeneratingAnnotationHandler generatingAnnotationHandler : annotationHandlers.getGenerating()) {
			String annotationName = generatingAnnotationHandler.getTarget();
			Set<? extends Element> annotatedElements = validatedModel.getRootAnnotatedElements(annotationName);

			if (!annotatedElements.isEmpty()) {
				LOGGER.debug("Processing root elements {}: {}", generatingAnnotationHandler.getClass().getSimpleName(), annotatedElements);
			}

			for (final Element annotatedElement : annotatedElements) {
				/*
				 * We do not generate code for abstract classes, because the
				 * generated classes are final anyway (we do not want anyone to
				 * extend them).
				 */
				if (!isAbstractClass(annotatedElement)) {
					if (processHolder.getGeneratedClassHolder(annotatedElement) == null) {
						TypeElement typeElement = (TypeElement) annotatedElement;
						Element enclosingElement = annotatedElement.getEnclosingElement();

						if (typeElement.getNestingKind() == NestingKind.MEMBER && processHolder.getGeneratedClassHolder(enclosingElement) == null) {
							isElementRemaining = true;
						} else {
							final GeneratedClassHolder generatedClassHolder = generatingAnnotationHandler.createGeneratedClassHolder(processHolder, typeElement);
							processHolder.put(annotatedElement, generatedClassHolder);
							generatingAnnotationHandler.preProcess(annotatedElement, generatedClassHolder);
							generatingAnnotationHandler.process(annotatedElement, generatedClassHolder);
							generatingPostProcessors.add(new Runnable() {
								@Override
								public void run() {
									try {
										generatingAnnotationHandler.postProcess(annotatedElement, generatedClassHolder);
									} catch (Exception e) {
										throw new IllegalStateException(e);
									}
								}
							});
						}
					}
				} else {
					LOGGER.trace("Skip element {} because it's abstract", annotatedElement);
				}
			}
			/*
			 * We currently do not take into account class annotations from
			 * ancestors. We should careful design the priority rules first.
			 */
		}
		return isElementRemaining;
	}

	private interface ElementProcessor {
		void process(AnnotationHandler handler, Element element, GeneratedClassHolder generatedClassHolder) throws ProcessingException;
	}

}
