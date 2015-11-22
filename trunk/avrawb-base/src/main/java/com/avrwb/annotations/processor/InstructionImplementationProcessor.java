/*
 * $Id$
 *
 * Copyright (C) 2015 Wolfgang Reder <wolfgang.reder@aon.at>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *
 */
package com.avrwb.annotations.processor;

import com.avrwb.annotations.InstructionImplementation;
import com.avrwb.annotations.InstructionImplementations;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.helper.AvrDeviceKey;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author wolfi
 */
@ServiceProvider(service = Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.avrwb.annotations.InstructionImplementation", "com.avrwb.annotations.InstructionImplementations"})
public class InstructionImplementationProcessor extends AbstractProcessor
{

  private final Map<ProcessingEnvironment, Map<String, Set<String>>> outputFilesByProcessor = new WeakHashMap<>();
  private final Map<ProcessingEnvironment, Map<String, List<Element>>> originatingElementsByProcessor = new WeakHashMap<>();
  private final Map<TypeElement, Boolean> verifiedClasses = new WeakHashMap<>();
  private TypeMirror typeDeviceKey;
  private TypeMirror typeInt;

  /**
   * For access by subclasses.
   */
  public InstructionImplementationProcessor()
  {
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv)
  {
    super.init(processingEnv);
    TypeElement tmp = processingEnv.getElementUtils().getTypeElement(AvrDeviceKey.class.getName());
    typeDeviceKey = tmp.asType();
    typeInt = processingEnv.getTypeUtils().getPrimitiveType(TypeKind.INT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean process(Set<? extends TypeElement> annotations,
                               RoundEnvironment roundEnv)
  {
    if (roundEnv.errorRaised()) {
      return false;
    }
    if (roundEnv.processingOver()) {
      writeServices();
      outputFilesByProcessor.clear();
      originatingElementsByProcessor.clear();
      return true;
    } else {
      return handleProcess(annotations,
                           roundEnv);
    }
  }

  /**
   * <p>
   * handleProcess.</p>
   *
   * @param annotations a {@link java.util.Set} object.
   * @param roundEnv a {@link javax.annotation.processing.RoundEnvironment} object.
   * @return a boolean.
   */
  protected boolean handleProcess(Set<? extends TypeElement> annotations,
                                  RoundEnvironment roundEnv)
  {
    boolean result = true;
    final Set<TypeElement> elementsToTest = new HashSet<>();
    for (Element el : roundEnv.getElementsAnnotatedWith(InstructionImplementation.class)) {
      elementsToTest.add((TypeElement) el);
    }
    for (Element el : roundEnv.getElementsAnnotatedWith(InstructionImplementations.class)) {
      elementsToTest.add((TypeElement) el);
    }
    for (TypeElement clazz : elementsToTest) {
      InstructionImplementation sp = clazz.getAnnotation(InstructionImplementation.class);
      if (sp != null) {
        result &= register(clazz,
                           sp);
      } else {
        InstructionImplementations sps = clazz.getAnnotation(InstructionImplementations.class);
        if (sps != null) {
          result &= !register(clazz,
                              sps);
        }
      }
    }
    return result;
  }

  private boolean register(TypeElement clazz,
                           InstructionImplementation svc)
  {
    if (svc == null) {
      processingEnv.getMessager().printMessage(Kind.ERROR,
                                               "no itemprovider for clazz " + clazz.getQualifiedName());
      return false;
    }
    return register(clazz,
                    InstructionImplementation.class,
                    svc.factoryMethod());
  }

  private boolean register(TypeElement clazz,
                           InstructionImplementations svc)
  {
    if (svc == null) {
      processingEnv.getMessager().printMessage(Kind.ERROR,
                                               "no itemprovider for clazz " + clazz.getQualifiedName());
      return false;
    }
    return register(clazz,
                    InstructionImplementations.class,
                    svc.factoryMethod());
  }

  /**
   * <p>
   * register.</p>
   *
   * @param clazz a {@link javax.lang.model.element.TypeElement} object.
   * @param annotation a {@link java.lang.Class} object.
   * @param constr a {@link java.lang.String} object.
   * @return a boolean.
   */
  protected final boolean register(TypeElement clazz,
                                   Class<? extends Annotation> annotation,
                                   String constr)
  {
    Boolean verify = verifiedClasses.get(clazz);
    if (verify == null) {
      verify = verifyServiceProviderSignature(clazz,
                                              annotation,
                                              constr);
      verifiedClasses.put(clazz,
                          verify);
    }
    if (!verify) {
      return false;
    }
    TypeMirror instructionType = processingEnv.getElementUtils().getTypeElement("com.avrwb.avr8.api.Instruction").asType();
    final String className = processingEnv.getElementUtils().getBinaryName(clazz).toString();
    String impl = className;
    if (!(constr == null || constr.trim().isEmpty())) {
      impl += "=" + constr;
    }
    if (!processingEnv.getTypeUtils().isAssignable(clazz.asType(),
                                                   instructionType)) {
      AnnotationMirror ann = findAnnotationMirror(clazz,
                                                  annotation);
      processingEnv.getMessager().printMessage(Kind.ERROR,
                                               className + " is not assignable to " + instructionType,
                                               clazz,
                                               ann,
                                               findAnnotationValue(ann,
                                                                   "provider"));
      return false;
    }
    String path = "META-INF/avr/" + Instruction.class.getName();
    processingEnv.getMessager().printMessage(Kind.NOTE,
                                             className + " to be registered as a instruction");
    String rsrc = path;
    {
      Map<String, List<Element>> originatingElements = originatingElementsByProcessor.get(processingEnv);
      if (originatingElements == null) {
        originatingElements = new HashMap<>();
        originatingElementsByProcessor.put(processingEnv,
                                           originatingElements);
      }
      List<Element> origEls = originatingElements.get(rsrc);
      if (origEls == null) {
        origEls = new ArrayList<>();
        originatingElements.put(rsrc,
                                origEls);
      }
      origEls.add(clazz);
    }
    Map<String, Set<String>> outputFiles = outputFilesByProcessor.get(processingEnv);
    if (outputFiles == null) {
      outputFiles = new HashMap<>();
      outputFilesByProcessor.put(processingEnv,
                                 outputFiles);
    }
    Set<String> lines = outputFiles.get(rsrc);
    if (lines == null) {
      lines = new HashSet<>();
      try {
        try {
          FileObject in = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH,
                                                               "",
                                                               rsrc);
          in.openInputStream().close();
          processingEnv.getMessager().printMessage(Kind.ERROR,
                                                   "Cannot generate " + rsrc + " because it already exists in sources: " + in.
                                                   toUri());
          return false;
        } catch (FileNotFoundException x) {
          // Good.
        }
        try {
          FileObject in = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT,
                                                               "",
                                                               rsrc);
          try (InputStream is = in.openInputStream()) {
            BufferedReader r = new BufferedReader(new InputStreamReader(is,
                                                                        "UTF-8"));
            String line;
            while ((line = r.readLine()) != null) {
              lines.add(line);
            }
          }
        } catch (FileNotFoundException x) {
          // OK, created for the first time
        }
      } catch (IOException x) {
        processingEnv.getMessager().printMessage(Kind.ERROR,
                                                 x.toString());
        return false;
      }
      outputFiles.put(rsrc,
                      lines);
    }
    lines.add(impl);
    return true;
  }

  private boolean checkConstructingElement(ExecutableElement method,
                                           TypeElement clazz,
                                           boolean ctor)
  {
    if (!method.getModifiers().contains(Modifier.PUBLIC)) {
      return false;
    }
    if (method.getParameters().size() != 3) {
      return false;
    }
    if (!ctor) {
      TypeMirror type = method.getReturnType();
      if (!processingEnv.getTypeUtils().isAssignable(type,
                                                     clazz.asType())) {
        return false;
      }
    }
    VariableElement ve = method.getParameters().get(0);
    TypeMirror tm = ve.asType();
    if (!processingEnv.getTypeUtils().isAssignable(tm,
                                                   typeDeviceKey)) {
      return false;
    }
    ve = method.getParameters().get(1);
    tm = ve.asType();
    if (!processingEnv.getTypeUtils().isAssignable(tm,
                                                   typeInt)) {
      return false;
    }
    ve = method.getParameters().get(2);
    tm = ve.asType();
    return processingEnv.getTypeUtils().isAssignable(tm,
                                                     typeInt);
  }

  private boolean verifyServiceProviderSignature(TypeElement clazz,
                                                 Class<? extends Annotation> annotation,
                                                 String contrs)
  {
    AnnotationMirror ann = findAnnotationMirror(clazz,
                                                annotation);
    if (!clazz.getModifiers().contains(Modifier.PUBLIC)) {
      processingEnv.getMessager().printMessage(Kind.ERROR,
                                               clazz + " must be public",
                                               clazz,
                                               ann);
      return false;
    }
    if (!clazz.getModifiers().contains(Modifier.FINAL)) {
      processingEnv.getMessager().printMessage(Kind.ERROR,
                                               clazz + " must be final",
                                               clazz,
                                               ann);
      return false;
    }

    boolean hasCtor = false;
    for (ExecutableElement method : ElementFilter.methodsIn(clazz.getEnclosedElements())) {
      if (method.getModifiers().contains(Modifier.STATIC) && method.getSimpleName().contentEquals(contrs)) {
        if (hasCtor = checkConstructingElement(method,
                                               clazz,
                                               false)) {
          break;

        }
      }
    }
    if (!hasCtor) {
      for (ExecutableElement method : ElementFilter.constructorsIn(clazz.getEnclosedElements())) {
        if (hasCtor = checkConstructingElement(method,
                                               clazz,
                                               true)) {
          break;

        }
      }
    }
    if (!hasCtor) {
      processingEnv.getMessager().printMessage(Kind.ERROR,
                                               clazz + " must have a public constructor or static method named " + contrs
                                                       + " with signature (com.avrwb.avr8.helper.AvrDeviceKey,int,int) and a return type assignable to "
                                               + "com.avrawb.avr8.api.Instruction");
      return false;
    }

    return true;
  }

  /**
   * @param element a source element
   * @param annotation a type of annotation
   * @return the instance of that annotation on the element, or null if not found
   */
  private AnnotationMirror findAnnotationMirror(Element element,
                                                Class<? extends Annotation> annotation)
  {
    for (AnnotationMirror ann : element.getAnnotationMirrors()) {
      if (processingEnv.getElementUtils().getBinaryName((TypeElement) ann.getAnnotationType().asElement()).
              contentEquals(annotation.getName())) {
        return ann;
      }
    }
    return null;
  }

  /**
   * @param annotation an annotation instance (null permitted)
   * @param name the name of an attribute of that annotation
   * @return the corresponding value if found
   */
  private AnnotationValue findAnnotationValue(AnnotationMirror annotation,
                                              String name)
  {
    if (annotation != null) {
      for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
        if (entry.getKey().getSimpleName().contentEquals(name)) {
          return entry.getValue();
        }
      }
    }
    return null;
  }

  private void writeServices()
  {
    for (Map.Entry<ProcessingEnvironment, Map<String, Set<String>>> outputFiles : outputFilesByProcessor.entrySet()) {
      for (Map.Entry<String, Set<String>> entry : outputFiles.getValue().entrySet()) {
        try {
          FileObject out = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                                                                   "",
                                                                   entry.getKey(),
                                                                   originatingElementsByProcessor.get(outputFiles.getKey()).get(
                                                                   entry.
                                                                   getKey()).toArray(new Element[0]));
          try (OutputStream os = out.openOutputStream();
                  PrintWriter w = new PrintWriter(new OutputStreamWriter(os,
                                                                         "UTF-8"))) {
            for (String line : entry.getValue()) {
              w.println(line);
            }
            w.flush();
          }
        } catch (IOException x) {
          processingEnv.getMessager().printMessage(Kind.ERROR,
                                                   "Failed to write to " + entry.getKey() + ": " + x.toString());
        }
      }
    }
  }

}
