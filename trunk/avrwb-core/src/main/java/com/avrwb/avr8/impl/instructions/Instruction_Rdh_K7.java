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

import com.avrwb.avr8.Device;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.InstructionResultBuilder;

/**
 *
 * @author wolfi
 */
public abstract class Instruction_Rdh_K7 extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xf800;

  public static final int composeOpcode(int baseOpcode,
                                        int rdh,
                                        int k7)
  {
    if ((baseOpcode & ~OPCODE_MASK) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (rdh < 16 || rdh > 31) {
      throw new IllegalArgumentException("invalid rdh");
    }
    if (k7 < 0 || k7 > 0x7f) {
      throw new IllegalArgumentException("invalid k7");
    }
    return baseOpcode | ((k7 & 0x70) << 4) | ((rdh & 0xf) << 4) | (k7 & 0xf);
  }

  protected final int rdhAddress;
  protected final int k7;
  private final String toStringValue;

  protected Instruction_Rdh_K7(int opcode,
                               String mnemonic)
  {
    super(opcode,
          mnemonic);
    rdhAddress = ((opcode >> 4) & 0xf) + 16;
    k7 = ((opcode >> 4) & 0x70) | (opcode & 0xf);
    toStringValue = mnemonic + " r" + rdhAddress + ", 0x" + Integer.toHexString(k7);
  }

  public final int getRdhAddress()
  {
    return rdhAddress;
  }

  public final int getK7()
  {
    return k7;
  }

  @Override
  protected void doPrepare(ClockDomain clockDomain,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    throw new UnsupportedOperationException("not implemented yet");
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
