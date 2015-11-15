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
package at.reder.avrwb.avr8.api.instructions;

import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;

/**
 *
 * @author wolfi
 */
public final class Mov extends Instruction_Rd_Rr
{

  public static final int OPCODE = 0x2c00;

  public Mov(int opcode)
  {
    super(opcode,
          "mov");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    Memory sram = device.getSRAM();
    int oldValue = sram.getByteAt(rdAddress);
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    if (oldValue != rrVal) {
      sram.setByteAt(rdAddress,
                     rrVal);
      resultBuilder.addModifiedDataAddresses(rdAddress);
    }
    logExecutionResult(clockState,
                       device,
                       rrVal,
                       rdAddress);
  }

}
