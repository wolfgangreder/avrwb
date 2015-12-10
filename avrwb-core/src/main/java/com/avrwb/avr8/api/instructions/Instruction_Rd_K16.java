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
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.schema.util.Converter;

/**
 *
 * @author wolfi
 */
public abstract class Instruction_Rd_K16 extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xfe0f;

  private final int rdAddress;
  private final int k16;
  private final String toStringValue;
  protected int rdVal;
  protected int pointer;
  protected int pointee;

  protected Instruction_Rd_K16(int opcode,
                               String mnemonic)
  {
    super(opcode,
          mnemonic);
    rdAddress = (opcode >> 20) & 0x1f;
    k16 = opcode & 0xffff;
    toStringValue = mnemonic + " r" + rdAddress + ", " + Converter.printHexString(k16,
                                                                                  4);
  }

  public final int getRdAddress()
  {
    return rdAddress;
  }

  public final int getK16()
  {
    return k16;
  }

  @Override
  public int getSize()
  {
    return 4;
  }

  @Override
  public int getCycleCount()
  {
    return 2;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      SRAM sram = device.getSRAM();
      rdVal = sram.getByteAt(rdAddress);
      Register rampd = device.getCPU().getRAMPD();
      if (rampd != null) {
        pointer = (rampd.getValue() << 16) + k16;
      } else {
        pointer = k16;
      }
      pointee = sram.getByteAt(pointer);
      finishCycle = clockState.getCycleCount() + getCycleCount() - 1;
    }
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
