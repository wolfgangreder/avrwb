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

import com.avrwb.avr8.Device;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
public final class Sbr extends Instruction_Rd_K8
{

  public static final int OPCODE = 0x6000;

  public Sbr(int opcode)
  {
    super(opcode,
          "sbr");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    final SREG sreg = device.getCPU().getSREG();
    final SRAM sram = device.getSRAM();
    final int oldRd = rdVal;
    final int oldSREG = sreg.getValue();
    rdVal |= k8;
    sreg.setZ(rdVal == 0);
    sreg.setN((rdVal & 0x80) != 0);
    sreg.setV(false);
    sreg.fixSignBit();
    if (oldSREG != sreg.getValue()) {
      resultBuilder.addModifiedRegister(sreg);
    }
    if (oldRd != rdVal) {
      sram.setByteAt(rdAddress,
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
