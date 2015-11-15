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
import at.reder.avrwb.avr8.SREG;
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;
import at.reder.avrwb.avr8.helper.AVRWBDefaults;
import at.reder.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
public final class Asr extends Instruction_Rd
{

  public Asr(int opcode)
  {
    super(opcode,
          "asr");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    final SREG sreg = device.getCPU().getSREG();
    final int oldValue = rdVal;
    final int oldSREG = sreg.getValue();
    final int rdAddress = getRdAddress();
    rdVal = ((rdVal / 2) & 0x7f) | (oldValue & 0x80);
    sreg.setC((oldValue & 0x01) != 0);
    sreg.setZ(rdVal == 0);
    sreg.setN((rdVal & 0x80) != 0);
    sreg.setV(sreg.getN() ^ sreg.getC());
    sreg.setS(sreg.getN() ^ sreg.getV());
    if (oldValue != rdVal) {
      resultBuilder.addModifiedDataAddresses(rdAddress);
      device.getSRAM().setByteAt(rdAddress,
                                 rdVal);
    }
    if (oldSREG != sreg.getValue()) {
      resultBuilder.addModifiedRegister(sreg);
    }
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    if (AVRWBDefaults.isDebugLoggingActive()) {
      logExecutionResult(clockState,
                         device,
                         rdVal,
                         rdAddress);
    }
  }

}
