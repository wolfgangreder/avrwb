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
import com.avrwb.avr8.Stack;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xfe0e, opcodes = 0x940e)
public final class Call extends Instruction_K22
{

  public static final int OPCODE = 0x940e;
  private boolean longCall;

  public static Call getInstance(AvrDeviceKey deviceKey,
                                 int opcode,
                                 int nextOpcode)
  {
    return new Call(deviceKey,
                    opcode,
                    nextOpcode);
  }

  private Call(AvrDeviceKey deviceKey,
               int opcode,
               int nextOpcode)
  {
    super(opcode,
          nextOpcode,
          "call");
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      longCall = device.getFlash().getHexAddressStringWidth() > 4;
      if (longCall) {
        finishCycle = clockState.getCycleCount() + 4;
      } else {
        finishCycle = clockState.getCycleCount() + 3;
      }
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      final Stack stack = device.getStack();
      final CPU cpu = device.getCPU();
      final int ipToPush = cpu.getIP() + 2;
      int toPush = ipToPush & 0xff;
      int oldByte = stack.push(toPush);
      resultBuilder.addModifiedRegister(cpu.getStackPointer());
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
      final int callTarget = getK22();
      resultBuilder.finished(true,
                             callTarget);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} calling ip {1}",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          Converter.printHexString(callTarget,
                                                                                   device.getFlash().getHexAddressStringWidth())));
      }

    }
  }

}
