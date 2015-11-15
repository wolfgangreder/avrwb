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
import at.reder.avrwb.avr8.SREG;
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;
import at.reder.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
public final class Adiw extends Instruction_Rdl_K6
{

  public static final int OPCODE = 0x9600;

  public Adiw(int opcode)
  {
    super(opcode,
          "adiw");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (clockState.getCycleCount() == finishCycle) {
      Memory sram = device.getSRAM();
      SREG sreg = device.getCPU().getSREG();
      int oldSREG = sreg.getValue();
      int oldRdl = sram.getByteAt(getRdlAddress());
      int oldRdh = sram.getByteAt(getRdlAddress() + 1);
      rdVal += getK6();
      sreg.setC(((rdVal & 0x8000) == 0) && ((oldRdh & 0x80) != 0));
      sreg.setZ(rdVal == 0);
      sreg.setN((rdVal & 0x8000) != 0);
      sreg.setV(((oldRdh & 0x80) == 0) && (rdVal & 0x8000) != 0);
      sreg.fixSignBit();
      if (oldSREG != sreg.getValue()) {
        resultBuilder.addModifiedRegister(sreg);
      }
      if (oldRdl != sram.getByteAt(getRdlAddress())) {
        resultBuilder.addModifiedDataAddresses(getRdlAddress());
      }
      if (oldRdh != sram.getByteAt(getRdlAddress() + 1)) {
        resultBuilder.addModifiedDataAddresses(getRdlAddress() + 1);
      }
      resultBuilder.finished(true,
                             device.getCPU().getIP() + 1);
      logExecutionResult(clockState,
                         device,
                         rdVal,
                         getRdlAddress());
    }
  }

}
