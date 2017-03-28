/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Ahmed basyouni
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ark.androidkvo.processor;

import com.ark.androidkvo.annotations.AndroidKVO;
import com.ark.androidkvo.annotations.KVOField;
import com.ark.androidkvo.models.AnnotatedClass;
import com.ark.androidkvo.utils.ClassValidator;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

import static java.util.Collections.singleton;
import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class KVOProcessor extends AbstractProcessor {

    private Messager messager;
    private static final String ANNOTATION = "@" + AndroidKVO.class.getSimpleName();
    private static final String FIELDS_ANNOTATION = "@" + KVOField.class.getSimpleName();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return singleton(AndroidKVO.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        ArrayList<AnnotatedClass> annotatedClasses = new ArrayList<>();

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(AndroidKVO.class)) {

            TypeElement annotatedClass = (TypeElement) annotatedElement;

            if (!isValidClass(annotatedClass)) {
                return true;
            }

            if (!checkFields(annotatedClass)) {
                return true;
            }

            annotatedClasses.add(buildAnnotatedClass(annotatedClass));
        }

        generateClasses(annotatedClasses);

        return false;
    }

    private void generateClasses(ArrayList<AnnotatedClass> annotatedClasses) {

        for (AnnotatedClass annotatedClass : annotatedClasses) {

            try { // write the file
                JavaFileObject source = processingEnv.getFiler().createSourceFile(processingEnv.getElementUtils().getPackageOf(annotatedClass.annotatedClass).getQualifiedName().toString() + ".kvo." + annotatedClass.annotatedClassName);


                Writer writer = source.openWriter();
                writer.write(ClassesGenerator.generateClass(annotatedClass).toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private AnnotatedClass buildAnnotatedClass(TypeElement annotatedClass) {

        List<VariableElement> fields = new ArrayList<>();

        for (Element element : annotatedClass.getEnclosedElements()) {

            if (!(element instanceof VariableElement)) {
                continue;
            }
            VariableElement variableElement = (VariableElement) element;

            if (variableElement.getAnnotation(KVOField.class) != null) {

                fields.add(variableElement);
            }
        }

        return new AnnotatedClass(annotatedClass.getSimpleName() + "KVO", fields, annotatedClass
                , processingEnv.getElementUtils().getPackageOf(annotatedClass).getQualifiedName().toString());
    }

    private boolean checkFields(TypeElement annotatedClass) {

        for (Element element : annotatedClass.getEnclosedElements()) {

            if (element.getAnnotation(KVOField.class) != null) {

                if (!ClassValidator.isFieldProtected(element)) {
                    String message = String.format("Fields annotated with %s must be Protected.",
                            FIELDS_ANNOTATION);
                    messager.printMessage(ERROR, message, annotatedClass);
                    return false;
                }

            }
        }

        return true;
    }

    private boolean isValidClass(TypeElement annotatedClass) {

        if (!ClassValidator.isPublic(annotatedClass)) {
            String message = String.format("Classes annotated with %s must be public.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (ClassValidator.isAbstract(annotatedClass)) {
            String message = String.format("Classes annotated with %s must not be abstract.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        return true;
    }
}
