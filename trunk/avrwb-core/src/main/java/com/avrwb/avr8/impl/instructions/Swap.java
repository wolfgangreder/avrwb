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
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xfe0f, opcodes = 0x9402)
public final class Swap extends Instruction_Rd
{

  public static final int OPCODE = 0x9402;

  public static Swap getInstance(AvrDeviceKey deviceKey,
                                 int opcode,
                                 int nextOpcode)
  {
    return new Swap(deviceKey,
                    opcode,
                    nextOpcode);
  }

  private Swap(AvrDeviceKey deviceKey,
               int opcode,
               int nextOpcode)
  {
    super(opcode,
          "swap");
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    final int rdAddress = getRdAddress();
    final int oldVal = rdVal;
    rdVal = ((rdVal & 0xf0) >> 4) | ((rdVal & 0x0f) << 4);
    if (rdVal != oldVal) {
      device.getSRAM().setByteAt(rdAddress,
                                 rdVal);
      resultBuilder.addModifiedDataAddresses(rdAddress);
    }
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    logExecutionResult(clockState,
                       device,
                       rdVal,
                       rdAddress);
  }

}
