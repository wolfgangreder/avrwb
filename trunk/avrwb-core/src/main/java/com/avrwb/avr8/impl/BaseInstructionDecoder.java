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
package com.avrwb.avr8.impl;

import com.avrwb.annotations.InstructionImplementation;
import com.avrwb.annotations.InstructionImplementations;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionDecoder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.InstructionNotAvailableException;
import com.avrwb.avr8.helper.TriFunction;
import com.avrwb.schema.util.Converter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author wolfi
 */
@NotThreadSave
public class BaseInstructionDecoder implements InstructionDecoder
{

  private static final class CtorConstructor implements TriFunction<AvrDeviceKey, Integer, Integer, Instruction>
  {

    private final Constructor<?> ctor;

    public CtorConstructor(Constructor<?> ctor)
    {
      this.ctor = ctor;
    }

    @Override
    public Instruction apply(AvrDeviceKey deviceKey,
                             Integer opcode,
                             Integer nextOpcode)
    {
      try {
        return (Instruction) ctor.newInstance(deviceKey,
                                              opcode,
                                              nextOpcode);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        Exceptions.printStackTrace(ex);
      }
      return null;
    }

  }

  private static final class MethConstructor implements TriFunction<AvrDeviceKey, Integer, Integer, Instruction>
  {

    private final Method ctor;

    public MethConstructor(Method ctor)
    {
      this.ctor = ctor;
    }

    @Override
    public Instruction apply(AvrDeviceKey deviceKey,
                             Integer opcode,
                             Integer nextOpcode)
    {
      try {
        return (Instruction) ctor.invoke(null,
                                         deviceKey,
                                         opcode,
                                         nextOpcode);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        Exceptions.printStackTrace(ex);
      }
      return null;
    }

  }

  private static final class Descriptor
  {

    private final String instruction;
    private final int opcodeMask;
    private final int baseOpcode;
    private final TriFunction<AvrDeviceKey, Integer, Integer, Instruction> ctrFunc;

    public Descriptor(String instruction,
                      TriFunction<AvrDeviceKey, Integer, Integer, Instruction> ctrFunc,
                      int opcodeMask,
                      int baseOpcode)
    {
      this.instruction = instruction;
      this.ctrFunc = ctrFunc;
      this.opcodeMask = opcodeMask;
      this.baseOpcode = baseOpcode;
    }

    public Instruction constructInstruction(AvrDeviceKey deviceKey,
                                            int opcode,
                                            int nextOpcode)
    {
      return ctrFunc.apply(deviceKey,
                           opcode,
                           nextOpcode);
    }

    public boolean matches(int opcode)
    {
      return baseOpcode == (opcode & opcodeMask);
    }

    @Override
    public String toString()
    {
      return "Descriptor{" + instruction + ": mask=0x" + Integer.toHexString(opcodeMask) + ", opcode=0x" + Integer.toHexString(
              baseOpcode) + '}';
    }

  }
  private final Map<Integer, Instruction> instructionCache = new HashMap<>();
  private final List<Descriptor> instructionRepository = new ArrayList<>();

  public BaseInstructionDecoder()
  {
  }

  private void fillInstructionRepository()
  {
    try {
      Enumeration<URL> urlEnum = getClass().getClassLoader().getResources("META-INF/avr/" + Instruction.class.getName());
      while (urlEnum.hasMoreElements()) {
        processURL(urlEnum.nextElement());
      }
    } catch (IOException ex) {
      Exceptions.printStackTrace(ex);
    }
  }

  private void processURL(URL u) throws IOException
  {
    try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(u.openStream(),
                                                                              "utf-8"))) {
      Set<String> processedLines = new HashSet<>();
      String line;
      while ((line = reader.readLine()) != null) {
        if (processedLines.add(line)) {
          List<Descriptor> lineResult = processLine(line);
          if (lineResult != null && !lineResult.isEmpty()) {
            for (Descriptor d : lineResult) {
              if (d != null) {
                instructionRepository.add(d);
              }
            }
          }
        }
      }
    }
  }

  private List<Descriptor> processLine(String line)
  {
    String clazzName;
    String factoryMethodName;
    int equalIndex = line.indexOf('=');
    if (equalIndex > 0) {
      clazzName = line.substring(0,
                                 equalIndex);
      if (equalIndex < line.length() - 1) {
        factoryMethodName = line.substring(equalIndex + 1);
      } else {
        factoryMethodName = null;
      }
    } else {
      clazzName = line;
      factoryMethodName = null;
    }
    final Logger logger = AVRWBDefaults.LOGGER;
    try {
      Class<?> clazz = Class.forName(clazzName);
      if (!Instruction.class.isAssignableFrom(clazz)) {
        logger.log(Level.WARNING,
                   "Class {0}is not assignable from {1}",
                   new Object[]{clazzName,
                                Instruction.class.getName()});
        return null;
      }
      if (!Modifier.isFinal(clazz.getModifiers()) || !Modifier.isPublic(clazz.getModifiers())) {
        logger.log(Level.WARNING,
                   "class implementing a instruction must be final and public");
        return null;
      }
      TriFunction<AvrDeviceKey, Integer, Integer, Instruction> construcingFunction;
      if (factoryMethodName == null || factoryMethodName.trim().isEmpty()) {
        Constructor<?> ctor = clazz.getConstructor(AvrDeviceKey.class,
                                                   Integer.TYPE,
                                                   Integer.TYPE);
        if (!Modifier.isPublic(ctor.getModifiers())) {
          logger.log(Level.WARNING,
                     "class implementig a instruction must have a public constructor");
          return null;
        }
        construcingFunction = new CtorConstructor(ctor);
      } else {
        Method method = clazz.getDeclaredMethod(factoryMethodName,
                                                AvrDeviceKey.class,
                                                Integer.TYPE,
                                                Integer.TYPE);
        if (!Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers())) {
          logger.log(Level.WARNING,
                     "instruction factory method must be public and static");
          return null;
        }
        if (!Instruction.class.isAssignableFrom(method.getReturnType())) {
          logger.log(Level.WARNING,
                     "return type of factory method must be assignable to {0}",
                     new Object[]{Instruction.class.getName()});
          return null;
        }
        construcingFunction = new MethConstructor(method);
      }
      List<InstructionImplementation> annotations = new LinkedList<>();
      InstructionImplementations implementations = clazz.getAnnotation(InstructionImplementations.class);
      if (implementations != null && implementations.value() != null) {
        for (InstructionImplementation i : implementations.value()) {
          if (i != null) {
            annotations.add(i);
          }
        }
      }
      InstructionImplementation implementation = clazz.getAnnotation(InstructionImplementation.class);
      if (implementation != null) {
        annotations.add(implementation);
      }
      if (annotations.isEmpty()) {
        logger.log(Level.WARNING,
                   "no InstructionImplementation annotation found on class {0}",
                   new Object[]{clazz.getName()});
        return null;
      }
      List<Descriptor> result = new LinkedList<>();
      for (InstructionImplementation i : annotations) {
        if (acceptInstruction((Class<? extends Instruction>) clazz,
                              i)) {
          int om = i.opcodeMask();
          for (int bo : i.opcodes()) {
            result.add(new Descriptor(clazz.getSimpleName(),
                                      construcingFunction,
                                      om,
                                      bo));
          }
        }
      }
      return result;
    } catch (NoSuchMethodException | ClassNotFoundException ex) {
      AVRWBDefaults.LOGGER.log(Level.WARNING,
                               "error while processing instruction descriptor line " + line,
                               ex);
      Exceptions.printStackTrace(ex);
    }
    return null;
  }

  /**
   * Hier können abgeleitete Klassen einen Filter implementieren
   *
   * @param clazz class
   * @param i annotation
   * @return {@code true} wenn die instruction implementiert ist.
   */
  protected boolean acceptInstruction(Class<? extends Instruction> clazz,
                                      InstructionImplementation i)
  {
    return true;
  }

  @Override
  public final Instruction getInstruction(Device device,
                                          int address) throws NullPointerException, IllegalArgumentException,
                                                              InstructionNotAvailableException
  {
    Objects.requireNonNull(device);
    if ((address % 2) != 0) {
      throw new IllegalArgumentException("address mod 2 !=0");
    }
    Memory flash = device.getFlash();
    int word = flash.getWordAt(address);
    int nextWord = flash.getWordAt(address + 2);
    Instruction result = instructionCache.computeIfAbsent(word,
                                                          (Integer opcode) -> decodeInstruction(device.getDeviceKey(),
                                                                                                opcode,
                                                                                                nextWord));
    if (result == null) {
      throw new InstructionNotAvailableException("Unknown opcode " + Converter.printHexString(word,
                                                                                              4));
    }
    return result;
  }

  @Override
  public final Instruction decodeInstruction(AvrDeviceKey deviceKey,
                                             int opcode,
                                             int nextOpcode) throws NullPointerException
  {
    if (instructionRepository.isEmpty()) {
      fillInstructionRepository();
    }
    List<Descriptor> candidates = new LinkedList<>();
    for (Descriptor d : instructionRepository) {
      if (d.matches(opcode)) {
        candidates.add(d);
      }
    }
    if (candidates.isEmpty()) {
      return null;
    }
    if (candidates.size() > 1) {
      AVRWBDefaults.LOGGER.log(Level.WARNING,
                               "found multiple instruction descriptors for opcode 0x{0}",
                               new Object[]{Integer.toHexString(opcode)});
    }
    return candidates.get(0).constructInstruction(deviceKey,
                                                  opcode,
                                                  nextOpcode);
  }

}
