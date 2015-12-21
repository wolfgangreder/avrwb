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
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xfe0f, opcodes = 0x9204)
public final class Xch extends Instruction_Rd
{

  public static final int OPCODE = 0x9204;
  private int pointeeVal;
  private int pointer;

  public static Xch getInstance(AvrDeviceKey deviceKey,
                                int opcode,
                                int nextOpcode)
  {
    return new Xch(deviceKey,
                   opcode,
                   nextOpcode);
  }

  private Xch(AvrDeviceKey deviceKey,
              int opcode,
              int nextOpcode)
  {
    super(opcode,
          "xch");
  }

  @Override
  public int getCycleCount()
  {
    return 2;
  }

  @Override
  protected void doPrepare(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == -1) {
      super.doPrepare(clockState,
                      device,
                      resultBuilder);
      pointer = computePointer(Pointer.Z,
                               device);
      pointeeVal = device.getSRAM().getByteAt(pointer);
      // finishCycle wird bereits durch super.doPrepare gesetzt
    }
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == clockState.getCycleCount()) {
      if (pointeeVal != rdVal) {
        final SRAM sram = device.getSRAM();
        resultBuilder.addModifiedDataAddresses(pointer);
        resultBuilder.addModifiedDataAddresses(getRdAddress());
        sram.setByteAt(pointer,
                       rdVal);
        sram.setByteAt(getRdAddress(),
                       pointeeVal);
      }
      resultBuilder.finished(true,
                             device.getCPU().getIP() + 1);
    }
  }

}
