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
public abstract class Instruction_Rdh23_Rrh23 extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xff88;

  public static int composeOpcode(int baseOpcode,
                                  int rdh,
                                  int rrh)
  {
    if ((baseOpcode & ~OPCODE_MASK) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (rdh < 16 || rdh > 23) {
      throw new IllegalArgumentException("invalid rdh");
    }
    if (rrh < 16 || rrh > 23) {
      throw new IllegalArgumentException("invalid rrh");
    }
    return baseOpcode | ((rdh & 0x7) << 4) | (rrh & 0x7);
  }

  private final int rdhAddress;
  private final int rrhAddress;
  private final String toStringValue;
  private int rdh;
  private int rdl;

  protected Instruction_Rdh23_Rrh23(int opcode,
                                    String mnemonic)
  {
    super(opcode,
          mnemonic);
    rdhAddress = ((opcode & 0x70) >> 4) + 16;
    rrhAddress = (opcode & 0x7) + 16;
    toStringValue = mnemonic + " r" + rdhAddress + ", r" + rrhAddress;
  }

  public final int getRdhAddress()
  {
    return rdhAddress;
  }

  public final int getRrhAddress()
  {
    return rrhAddress;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    throw new UnsupportedOperationException("not implemented yet");
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
