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
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xf000, opcodes = 0x6000)
public final class Ori extends Instruction_Rd_K8
{

  public static final int OPCODE = 0x6000;

  public static Ori getInstance(AvrDeviceKey deviceKey,
                                int opcode,
                                int nextOpcode)
  {
    return new Ori(deviceKey,
                   opcode,
                   nextOpcode);
  }

  private Ori(AvrDeviceKey deviceKey,
              int opcode,
              int nextOpcode)
  {
    super(opcode,
          "ori");
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    final SREG sreg = device.getCPU().getSREG();
    final int oldValue = rdVal;
    final int oldSREG = sreg.getValue();

    rdVal = rdVal | k8;
    sreg.setV(false);
    sreg.setN((rdVal & 0x80) != 0);
    sreg.setZ(rdVal == 0);
    sreg.fixSignBit();
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    if (oldValue != rdVal) {
      device.getSRAM().setByteAt(rdAddress,
                                 rdVal);
      resultBuilder.addModifiedDataAddresses(rdAddress);
    }
    if (oldSREG != sreg.getValue()) {
      resultBuilder.addModifiedRegister(sreg);
    }
    logExecutionResult(clockState,
                       device,
                       rdVal,
                       oldSREG);

  }

}
