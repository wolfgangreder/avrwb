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

/**
 *
 * @author wolfi
 */
public abstract class Instruction_K22 extends AbstractInstruction
{

  public static int composeOpcode(int baseOpcode,
                                  int k22)
  {
    if ((baseOpcode & ~0xfe0e) != 0) {
      throw new IllegalArgumentException("invalid baseopcode");
    }
    if (k22 < 0 || k22 > 0x3fffff) {
      throw new IllegalArgumentException("invalid k22");
    }
    return (baseOpcode << 16) | (k22 & 0x1ffff) | ((k22 & 0x3e0000) << 3);
  }

  private final int k22;
  private final String toStringValue;

  public Instruction_K22(int opcode,
                         int nextOpcode,
                         String mnemonic)
  {
    super(opcode << 16 | nextOpcode,
          mnemonic);
    int tmp = getOpcode();
    k22 = (tmp & 0xffff) | ((tmp & 0x1f00000) >> 3) | (tmp & 0x10000);
    toStringValue = mnemonic + " 0x" + Integer.toHexString(k22);
  }

  public final int getK22()
  {
    return k22;
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
