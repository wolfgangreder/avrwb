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
package at.reder.avrwb.avr8.api.instructions;

import at.reder.atmelschema.util.HexIntAdapter;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.SREG;
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;
import at.reder.avrwb.avr8.helper.AVRWBDefaults;
import at.reder.avrwb.avr8.helper.SimulationException;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
public final class BitClearSet extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xff0f;
  public static final int OPCODE = 0x9408;
  private final boolean setBit;
  private final int bitOffset;

  public BitClearSet(int opcode)
  {
    super(opcode,
          0xff0f,
          decodeMnemonic(opcode));
    setBit = decodeSetBit(opcode);
    bitOffset = decodeBitOffset(opcode);
  }

  private static int decodeBitOffset(int opcode)
  {
    return (opcode & 0x70) >> 4;
  }

  private static String decodeMnemonic(int opcode)
  {
    int bitOffset = decodeBitOffset(opcode);
    boolean set = decodeSetBit(opcode);
    if (set) {
      switch (bitOffset) {
        case 0:
          return "clc";
        case 1:
          return "clz";
        case 2:
          return "cln";
        case 3:
          return "clv";
        case 4:
          return "cls";
        case 5:
          return "clh";
        case 6:
          return "clt";
        case 7:
          return "cli";
      }
    } else {
      switch (bitOffset) {
        case 0:
          return "sec";
        case 1:
          return "sez";
        case 2:
          return "sen";
        case 3:
          return "sev";
        case 4:
          return "ses";
        case 5:
          return "seh";
        case 6:
          return "set";
        case 7:
          return "sei";

      }
    }
    throw new IllegalArgumentException("invalid opcode " + HexIntAdapter.toHexString(opcode,
                                                                                     4));
  }

  private static boolean decodeSetBit(int opcode)
  {
    return (opcode & 0x80) != 0;
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    final SREG sreg = device.getCPU().getSREG();
    if (sreg.setBit(bitOffset,
                    setBit) != setBit) {
      resultBuilder.addModifiedRegister(sreg);
    }
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    if (AVRWBDefaults.isDebugLoggingActive()) {
      device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                             () -> MessageFormat.format("{0} bit {1,number,0}->{2}",
                                                        getCurrentDeviceMessage(clockState,
                                                                                device),
                                                        bitOffset,
                                                        setBit));
    }
  }

}
