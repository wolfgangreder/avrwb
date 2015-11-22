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
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x9400})
public final class Com extends Instruction_Rd
{

  public static final int OPCODE = 0x9400;

  public Com(AvrDeviceKey deviceKey,
             int opcode,
             int nextOpcode)
  {
    super(opcode,
          "com");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    final SREG sreg = device.getCPU().getSREG();
    final int rdAddress = getRdAddress();
    final int oldRd = rdVal;
    final int oldSREG = sreg.getValue();
    rdVal = (~rdVal) & 0xff;
    sreg.setC(true);
    sreg.setV(false);
    sreg.setZ(rdVal == 0);
    sreg.setN((rdVal & 0x80) != 0);
    sreg.setS(sreg.getV() ^ sreg.getN());
    if (rdVal != oldRd) {
      resultBuilder.addModifiedDataAddresses(rdAddress);
      device.getSRAM().setByteAt(rdAddress,
                                 rdVal);
    }
    if (oldSREG != sreg.getValue()) {
      resultBuilder.addModifiedRegister(sreg);
    }
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    logExecutionResult(clockState,
                       device,
                       rdVal,
                       rdAddress);
  }

}
