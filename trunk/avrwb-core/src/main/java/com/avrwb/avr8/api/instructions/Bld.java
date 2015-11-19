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
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
public final class Bld extends Instruction_Rd_b
{

  public static final int OPCODE = 0xf800;

  public Bld(int opcode)
  {
    super(opcode,
          "bld");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    final int oldRdVal = rdVal;
    rdVal = (rdVal & ~bitMask);
    if (device.getCPU().getSREG().getT()) {
      rdVal |= bitMask;
    }
    if (oldRdVal != rdVal) {
      resultBuilder.addModifiedDataAddresses(rdAddress);
      device.getSRAM().setByteAt(rdAddress,
                                 rdVal);
    }
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    logExecutionResult(clockState,
                       device,
                       rdVal,
                       rdAddress);
  }

}
