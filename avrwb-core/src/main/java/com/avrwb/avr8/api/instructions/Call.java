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
package com.avrwb.avr8.api.instructions;

import com.avrwb.atmelschema.util.HexIntAdapter;
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Stack;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.SimulationException;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
public final class Call extends AbstractInstruction
{

  public static final int OPCODE = 0x940e0000;
  private final int callTarget;
  private boolean longCall;
  private final String toStringValue;

  public Call(int opcode,
              int nextOpcode)
  {
    super(opcode << 16 | nextOpcode,
          0xfe0effff,
          "call");
    int tmp = getOpcode();
    callTarget = (tmp & 0xffff) | ((tmp & 0x1f00000) >> 3) | (tmp & 0x10000);
    toStringValue = "call 0x" + Integer.toHexString(callTarget);
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

  public int getCallTarget()
  {
    return callTarget;
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
        resultBuilder.addModifiedDataAddresses(stack.getSP() - 1);
      }
      toPush = (ipToPush & 0xff00) >> 8;
      oldByte = stack.push(toPush);
      if (oldByte != toPush) {
        resultBuilder.addModifiedDataAddresses(stack.getSP() - 1);
      }
      if (longCall) {
        toPush = (ipToPush & 0x3f0000) >> 16;
        oldByte = stack.push(toPush);
        if (oldByte != toPush) {
          resultBuilder.addModifiedDataAddresses(stack.getSP() - 1);
        }
      }
      resultBuilder.finished(true,
                             callTarget);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} calling ip {1}",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          HexIntAdapter.toHexString(callTarget,
                                                                                    device.getFlash().getHexAddressStringWidth())));
      }

    }
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
