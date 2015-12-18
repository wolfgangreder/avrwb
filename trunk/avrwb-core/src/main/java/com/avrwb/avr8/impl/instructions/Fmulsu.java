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
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xff88, opcodes = {0x0388})
public final class Fmulsu extends Instruction_Rdh23_Rrh23
{

  public static final int OPCODE = 0x0388;

  public static Fmulsu getInstance(AvrDeviceKey deviceKey,
                                   int opcode,
                                   int nextOpcode)
  {
    return new Fmulsu(deviceKey,
                      opcode,
                      nextOpcode);
  }

  private Fmulsu(AvrDeviceKey deviceKey,
                 int opcode,
                 int nextOpcode)
  {
    super(opcode,
          "fmulsu");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
