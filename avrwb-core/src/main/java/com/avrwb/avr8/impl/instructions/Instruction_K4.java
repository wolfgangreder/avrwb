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

/**
 *
 * @author wolfi
 */
public abstract class Instruction_K4 extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xff0f;
  protected final int k4;
  private final String toStringVal;

  protected Instruction_K4(int opcode,
                           String mnemomic)
  {
    super(opcode,
          mnemomic);
    k4 = (opcode & ~OPCODE_MASK) >> 4;
    toStringVal = mnemomic + " " + k4;
  }

  public final int getK4()
  {
    return k4;
  }

  @Override
  public String toString()
  {
    return toStringVal;
  }

}
