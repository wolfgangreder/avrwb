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
public final class Elpm extends AbstractInstruction
{

  public static final int OPCODE_ELPM = 0x95d8;
  public static final int OPCODE_MASK_ELPM = 0xffff;
  public static final int OPCODE_ELPM_RD = 0x9006;
  public static final int OPCODE_ELPM_RDP = 0x9007;
  public static final int OPCODE_MASK_ELPM_RD = 0xfe0f;

  public static Elpm getInstance(int opcode)
  {
    if (opcode == OPCODE_ELPM) {
      return new Elpm(opcode,
                      OPCODE_MASK_ELPM,
                      0,
                      "elpm",
                      false);
    } else {
      int rdAddress = (opcode & ~OPCODE_MASK_ELPM_RD) >> 4;
      boolean pi = (opcode & 0x1) != 0;
      StringBuilder toStringVal = new StringBuilder("elpm r");
      toStringVal.append(rdAddress);
      toStringVal.append(", Z");
      if (pi) {
        toStringVal.append("+");
      }
      return new Elpm(opcode,
                      OPCODE_MASK_ELPM_RD,
                      rdAddress,
                      "elpm",
                      pi);
    }
  }

  private final int rdAddress;
  private final boolean postIncrement;

  private Elpm(int opcode,
               int opcodemask,
               int rdAddress,
               String mnemonic,
               boolean postIncrement)
  {
    super(opcode,
          opcodemask,
          mnemonic);
    this.rdAddress = rdAddress;
    this.postIncrement = postIncrement;
  }

  public int getRdAddress()
  {
    return rdAddress;
  }

  public boolean isPostIncrement()
  {
    return postIncrement;
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
