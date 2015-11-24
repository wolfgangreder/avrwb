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
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xffff, opcodes = 0x95a8)
public final class Wdr extends AbstractInstruction
{

  public static final int OPCODE = 0x95a8;

  public static Wdr getInstance(AvrDeviceKey deviceKey,
                                int opcode,
                                int nextOpcode)
  {
    return new Wdr(deviceKey,
                   opcode,
                   nextOpcode);
  }

  private Wdr(AvrDeviceKey deviceKey,
              int opcode,
              int nextOpcode)
  {
    super(opcode,
          "wdr");
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
