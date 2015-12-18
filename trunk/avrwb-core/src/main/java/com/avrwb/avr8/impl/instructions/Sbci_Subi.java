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
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xf000, opcodes = {0x4000, 0x5000})
public final class Sbci_Subi extends Instruction_Rd_K8
{

  public static final int OPCODE_SUBI = 0x5000;
  public static final int OPCODE_SBCI = 0x4000;
  private final boolean withCarry;

  public static Sbci_Subi getInstance(AvrDeviceKey deviceKey,
                                      int opcode,
                                      int nextOpcode)
  {
    return new Sbci_Subi(deviceKey,
                         opcode,
                         nextOpcode);
  }

  private Sbci_Subi(AvrDeviceKey deviceKey,
                    int opcode,
                    int nextOpcode)
  {
    super(opcode,
          (opcode & 0x1000) != 0 ? "subi" : "sbci");
    withCarry = (opcode & 0x1000) == 0;
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    final SREG sreg = device.getCPU().getSREG();
    final SRAM sram = device.getSRAM();
    int oldSREG = sreg.getValue();
    int oldRdVal = rdVal;
    rdVal = performSub(sreg,
                       rdVal,
                       k8,
                       withCarry);
    if (rdVal != oldRdVal) {
      sram.setByteAt(rdAddress,
                     rdVal);
      resultBuilder.addModifiedDataAddresses(rdAddress);
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
