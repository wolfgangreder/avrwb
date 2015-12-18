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

import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
public abstract class Instruction_k12 extends AbstractInstruction
{

  protected final int k12;
  private final String toStringValue;

  public Instruction_k12(int opcode,
                         String mnemonic)
  {
    super(opcode,
          mnemonic);
    int tmp = opcode & 0xfff;
    if ((tmp & 0x800) != 0) {
      tmp |= ((-1) & ~0xfff);
    }
    k12 = tmp;
    toStringValue = MessageFormat.format("{1} {0,number,0}",
                                         k12,
                                         mnemonic);
  }

  public final int getK12()
  {
    return k12;
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
