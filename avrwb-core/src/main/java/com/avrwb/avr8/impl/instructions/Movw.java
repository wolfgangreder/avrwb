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
@InstructionImplementation(opcodeMask = 0xff00, opcodes = 0x0100)
public final class Movw extends Instruction_Rdh_Rrh
{

  public static final int OPCODE = 0x0100;

  public static Movw getInstance(AvrDeviceKey deviceKey,
                                 int opcode,
                                 int nextOpcode)
  {
    return new Movw(deviceKey,
                    opcode,
                    nextOpcode);
  }

  private final int rdlAddress;
  private final int rrlAddress;
  private int rrVal;
  private int rdVal;

  private Movw(AvrDeviceKey deviceKey,
               int opcode,
               int nextOpcode)
  {
    super(opcode,
          "movw");
    rdlAddress = (opcode & 0x0f0) >> 3;
    rrlAddress = (opcode & 0xf) << 1;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      rdVal = device.getSRAM().getWordAt(rdlAddress);
      rrVal = device.getSRAM().getWordAt(rrlAddress);
      finishCycle = clockState.getCycleCount();
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      if (rdVal != rrVal) {
        resultBuilder.addModifiedDataAddresses(rdlAddress);
        resultBuilder.addModifiedDataAddresses(rdlAddress + 1);
        device.getSRAM().setWordAt(rdlAddress,
                                   rrVal);
      }
      resultBuilder.finished(true,
                             device.getCPU().getIP() + 1);
    }
  }

}
