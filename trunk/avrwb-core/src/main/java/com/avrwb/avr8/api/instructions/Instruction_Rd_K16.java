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

import com.avrwb.atmelschema.util.HexIntAdapter;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.SimulationException;

/**
 *
 * @author wolfi
 */
public abstract class Instruction_Rd_K16 extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xfe0f0000;

  public static int composeOpcode(int baseOpcode,
                                  int rdAddress,
                                  int k16)
  {
    if ((baseOpcode & ~OPCODE_MASK) != 0) {
      throw new IllegalArgumentException("illegal base opcode");
    }
    if (rdAddress < 0 || rdAddress > 31) {
      throw new IllegalArgumentException("illegal rd");
    }
    if (k16 < 0 || k16 > 0xffff) {
      throw new IllegalArgumentException("illegal k16");
    }
    return baseOpcode | ((rdAddress) << 20) | k16;
  }

  private final int rdAddress;
  private final int k16;
  private final String toStringValue;

  protected Instruction_Rd_K16(int opcode,
                               String mnemonic)
  {
    super(opcode,
          OPCODE_MASK,
          mnemonic);
    rdAddress = (opcode >> 20) & 0x1f;
    k16 = opcode & 0xffff;
    toStringValue = mnemonic + " r" + rdAddress + ", " + HexIntAdapter.toHexString(k16,
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
