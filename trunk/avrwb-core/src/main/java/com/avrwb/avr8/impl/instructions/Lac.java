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
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x9206})
public final class Lac extends Instruction_Rd
{

  public static final int OPCODE = 0x9206;
  private int ptr;
  private int zval;

  public static Lac getInstance(AvrDeviceKey deviceKey,
                                int opcode,
                                int nextOpcode)
  {
    return new Lac(deviceKey,
                   opcode,
                   nextOpcode);
  }

  private Lac(AvrDeviceKey deviceKey,
              int opcode,
              int nextOpcode)
  {
    super(opcode,
          "lac");
  }

  @Override
  public int getCycleCount()
  {
    return 2;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      super.doPrepare(clockState,
                      device,
                      resultBuilder);
      ptr = computePointer(Pointer.Z,
                           device);
      zval = device.getSRAM().getByteAt(ptr);
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      int result = (0xff - rdVal) & zval;
      if (result != rdVal) {
        resultBuilder.addModifiedDataAddresses(getRdAddress());
        device.getSRAM().setByteAt(getRdAddress(),
                                   result);
      }
      if (result != zval) {
        resultBuilder.addModifiedDataAddresses(ptr);
        device.getSRAM().setByteAt(ptr,
                                   result);
      }
      resultBuilder.finished(true,
                             device.getCPU().getIP() + 1);
    }
  }

}
