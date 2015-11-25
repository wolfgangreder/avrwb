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

import com.avrwb.annotations.InOut;
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
import com.avrwb.schema.util.Converter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
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

  protected static final class Descriptor implements Comparable<Descriptor>
  {

    private final Class<?> instruction;
    private final int opcodeMask;
    private final int baseOpcode;
    private final Method ctrFunc;

    public Descriptor(Class<?> instruction,
                      Method ctrFunc,
                      int opcodeMask,
                      int baseOpcode)
    {
      this.instruction = instruction;
      this.ctrFunc = ctrFunc;
      this.opcodeMask = opcodeMask;
      this.baseOpcode = baseOpcode;
    }

    public Class<?> getInstruction()
    {
      return instruction;
    }

    public int getOpcodeMask()
    {
      return opcodeMask;
    }

    public int getBaseOpcode()
    {
      return baseOpcode;
    }

    public Instruction constructInstruction(AvrDeviceKey deviceKey,
                                            int opcode,
                                            int nextOpcode)
    {
      try {
        return (Instruction) ctrFunc.invoke(null,
                                            deviceKey,
                                            opcode,
                                            nextOpcode);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        Exceptions.printStackTrace(ex);
      }
      return null;
    }

    public boolean matches(int opcode)
    {
      return baseOpcode == (opcode & opcodeMask);
    }

    @Override
    public int compareTo(Descriptor o)
    {
      int result = Integer.compare(Integer.bitCount(opcodeMask),
                                   Integer.bitCount(o.opcodeMask));
      if (result == 0) {
        result = Integer.compare(baseOpcode,
                                 o.baseOpcode);
      }
      return result;
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
    instructionRepository.clear();
    instructionCache.clear();
    try {
      Enumeration<URL> urlEnum = getClass().getClassLoader().getResources("META-INF/avr/" + Instruction.class.getName());
      while (urlEnum.hasMoreElements()) {
        processURL(urlEnum.nextElement());
      }
    } catch (IOException ex) {
      Exceptions.printStackTrace(ex);
    } finally {
      Collections.sort(instructionRepository);
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
              if (d != null && acceptDescriptor(d)) {
                instructionRepository.add(d);
              }
            }
          }
        }
      }
    }
  }

  protected boolean acceptDescriptor(Descriptor d)
  {
    return true;
  }

  private List<Descriptor> processLine(String line)
  {
    final Logger logger = AVRWBDefaults.LOGGER;
    try {
      Class<?> clazz = Class.forName(line);
      if (!Modifier.isFinal(clazz.getModifiers()) || !Modifier.isPublic(clazz.getModifiers())) {
        logger.log(Level.WARNING,
                   "class implementing a instruction must be final and public");
        return null;
      }
      Method method = clazz.getDeclaredMethod("getInstance",
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
        int om = i.opcodeMask();
        for (int bo : i.opcodes()) {
          result.add(new Descriptor(clazz,
                                    method,
                                    om,
                                    bo));
        }
      }
      return result;
    } catch (NoSuchMethodException | ClassNotFoundException ex) {
      AVRWBDefaults.LOGGER.log(Level.WARNING,
                               "error while processing instruction descriptor line " + line,
                               ex);
    }
    return null;
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
    Instruction result = instructionCache.computeIfAbsent(address,
                                                          (Integer t) -> {
                                                            Memory flash = device.getFlash();
                                                            int word = flash.getWordAt(address);
                                                            int nextWord = flash.getWordAt(address + 2);
                                                            return decodeInstruction(device.getDeviceKey(),
                                                                                     word,
                                                                                     nextWord);
                                                          });
    if (result == null) {
      Memory flash = device.getFlash();
      int word = flash.getWordAt(address);
      throw new InstructionNotAvailableException("Unknown opcode " + Converter.printHexString(word,
                                                                                              flash.getHexAddressStringWidth()));
    }
    return result;
  }

  protected boolean acceptOpcode(int opcode,
                                 int nextOpcode)
  {
    return true;
  }

  protected boolean filterCandicates(int opcode,
                                     int nextOpcode,
                                     @InOut List<? extends Descriptor> candicates)
  {
    return true;
  }

  private boolean checkDuplicates(List<? extends Descriptor> candidates)
  {
    ConcurrentHashMap<Integer, LongAdder> map = new ConcurrentHashMap<>();
    for (Descriptor d : candidates) {
      map.computeIfAbsent(d.baseOpcode,
                          k -> new LongAdder()).increment();
    }
    ListIterator<? extends Descriptor> iter = candidates.listIterator();
    while (iter.hasNext()) {
      Descriptor d = iter.next();
      LongAdder adder = map.get(d.baseOpcode);
      if (adder.sum() > 1) {
        iter.remove();
        adder.decrement();
      }
    }
    return candidates.size() == 1;
  }

  @Override
  public final Instruction decodeInstruction(AvrDeviceKey deviceKey,
                                             int opcode,
                                             int nextOpcode) throws NullPointerException
  {
    if (instructionRepository.isEmpty()) {
      fillInstructionRepository();
    }
    if (!acceptOpcode(opcode,
                      nextOpcode)) {
      return null;
    }
    List<Descriptor> candidates = new LinkedList<>();
    for (Descriptor d : instructionRepository) {
      if (d.matches(opcode)) {
        candidates.add(d);
      }
    }
    if (!filterCandicates(opcode,
                          nextOpcode,
                          candidates)) {
      return null;
    }
    if (candidates.isEmpty()) {
      return null;
    }
    if (candidates.size() > 1) {
      checkDuplicates(candidates);
      if (candidates.size() > 1) {
        AVRWBDefaults.LOGGER.log(Level.WARNING,
                                 "found multiple instruction descriptors for opcode 0x{0}",
                                 new Object[]{Integer.toHexString(opcode)});
      }
    }
    return candidates.get(0).constructInstruction(deviceKey,
                                                  opcode,
                                                  nextOpcode);
  }

}
