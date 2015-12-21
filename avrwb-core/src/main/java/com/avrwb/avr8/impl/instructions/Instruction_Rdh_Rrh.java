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
public abstract class Instruction_Rdh_Rrh extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xff00;

  private final int rdhAddress;
  private final int rrhAddress;
  private final String toStringValue;
  private int rdh;
  private int rdl;

  protected Instruction_Rdh_Rrh(int opcode,
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
