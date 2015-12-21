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
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.InstructionResultBuilder;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xffff, opcodes = 0x9588)
public final class Sleep extends AbstractInstruction
{

  public static final Sleep INSTANCE = new Sleep(null,
                                                 0,
                                                 0);
  public static final int OPCODE = 0x9588;

  public static Sleep getInstance(AvrDeviceKey deviceKey,
                                  int opcode,
                                  int nextOpcode)
  {
    return INSTANCE;
  }

  private Sleep(AvrDeviceKey deviceKey,
                int opcode,
                int nextOpcode)
  {
    super(opcode,
          "sleep");
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
  }

}
