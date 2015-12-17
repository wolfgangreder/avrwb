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

import com.avrwb.annotations.InstructionImplementation;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.Stack;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xfe0f, opcodes = 0x920f)
public final class Push extends Instruction_Rd
{

  public static final int OPCODE = 0x920f;

  public static Push getInstance(AvrDeviceKey deviceKey,
                                 int opcode,
                                 int nextOpcode)
  {
    return new Push(deviceKey,
                    opcode,
                    nextOpcode);
  }

  private Push(AvrDeviceKey deviceKey,
               int opcode,
               int nextOpcode)
  {
    super(opcode,
          "push");
  }

  @Override
  public int getCycleCount()
  {
    return 2;
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      final Stack stack = device.getStack();
      final Register sp = device.getCPU().getStackPointer();
      final int spOld = sp.getValue();
      int oldValue = stack.push(rdVal);
      final int spNew = sp.getValue();
      resultBuilder.addModifiedDataAddresses(sp.getMemoryAddress());
      if (sp.getSize() > 1 && (spOld & 0xff00) != (spNew & 0xff00)) {
        resultBuilder.addModifiedDataAddresses(sp.getMemoryAddress() + 1);
      }
      if (sp.getSize() > 2 && (spOld & 0xff0000) != (spNew & 0xff0000)) {
        resultBuilder.addModifiedDataAddresses(sp.getMemoryAddress() + 2);
      }
      if (sp.getSize() > 3 && (spOld & 0xff000000) != (spNew & 0xff000000)) {
        resultBuilder.addModifiedDataAddresses(sp.getMemoryAddress() + 3);
      }
      if (oldValue != rdVal) {
        resultBuilder.addModifiedDataAddresses(sp.getValue() + 1);
      }
      resultBuilder.finished(true,
                             device.getCPU().getIP() + 1);
      logExecutionResult(clockState,
                         device,
                         rdVal,
                         getRdAddress());
    }
  }

}
