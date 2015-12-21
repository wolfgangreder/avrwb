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
package com.avrwb.avr8.impl.instructions;

import com.avrwb.annotations.InstructionImplementation;
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.Stack;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.api.AVRWBDefaults;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xf000, opcodes = 0xd000)
public final class Rcall extends Instruction_k12
{

  public static final int OPCODE = 0xd000;
  private boolean longCall;

  public static Rcall getInstance(AvrDeviceKey deviceKey,
                                  int opcode,
                                  int nextOpcode)
  {
    return new Rcall(deviceKey,
                     opcode,
                     nextOpcode);
  }

  private Rcall(AvrDeviceKey deviceKey,
                int opcode,
                int nextOpcode)
  {
    super(opcode,
          "rcall");
  }

  @Override
  protected void doPrepare(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == -1) {
      longCall = device.getFlash().getHexAddressStringWidth() > 4;
      if (longCall) {
        finishCycle = clockState.getCycleCount() + 3;
      } else {
        finishCycle = clockState.getCycleCount() + 2;
      }
    }
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == clockState.getCycleCount()) {
      final Stack stack = device.getStack();
      final CPU cpu = device.getCPU();
      final int ipToPush = cpu.getIP() + 1;
      final Register sp = cpu.getStackPointer();
      final int spOld = sp.getValue();
      int toPush = ipToPush & 0xff;
      int oldByte = stack.push(toPush);
      if (oldByte != toPush) {
        resultBuilder.addModifiedDataAddresses(stack.getSP() + 1);
      }
      toPush = (ipToPush & 0xff00) >> 8;
      oldByte = stack.push(toPush);
      if (oldByte != toPush) {
        resultBuilder.addModifiedDataAddresses(stack.getSP() + 1);
      }
      if (longCall) {
        toPush = (ipToPush & 0x3f0000) >> 16;
        oldByte = stack.push(toPush);
        if (oldByte != toPush) {
          resultBuilder.addModifiedDataAddresses(stack.getSP() + 1);
        }
      }
      final int spNew = sp.getValue();
      resultBuilder.addModifiedDataAddresses(sp.getMemoryAddress());
      if (sp.getSize() > 1 && (spNew & 0xff00) != (spOld & 0xff00)) {
        resultBuilder.addModifiedDataAddresses(sp.getMemoryAddress() + 1);
      }
      if (sp.getSize() > 2 && (spNew & 0xff0000) != (spOld & 0xff0000)) {
        resultBuilder.addModifiedDataAddresses(sp.getMemoryAddress() + 2);
      }
      if (sp.getSize() > 3 && (spNew & 0xff000000) != (spOld & 0xff000000)) {
        resultBuilder.addModifiedDataAddresses(sp.getMemoryAddress() + 3);
      }
      final int targetIP = device.getCPU().getIP() + k12 + 1;
      resultBuilder.finished(true,
                             targetIP);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} calling ip {1}",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          Converter.printHexString(targetIP,
                                                                                   device.getFlash().getHexAddressStringWidth())));
      }
    }
  }

}
