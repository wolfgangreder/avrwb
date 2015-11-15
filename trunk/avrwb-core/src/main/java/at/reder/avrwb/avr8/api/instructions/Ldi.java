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
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;
import at.reder.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
public final class Ldi extends Instruction_Rd_K8
{

  public static final int OPCODE = 0xe000;

  public Ldi(int opcode)
  {
    super(opcode,
          "ldi");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (rdVal != k8) {
      device.getSRAM().setByteAt(rdAddress,
                                 k8);
      resultBuilder.addModifiedDataAddresses(rdAddress);
    }
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    logExecutionResult(clockState,
                       device,
                       k8,
                       rdAddress);
  }

}
