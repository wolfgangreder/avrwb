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
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.InstructionNotAvailableException;
import com.avrwb.avr8.helper.SimulationException;
import java.text.MessageFormat;
import java.util.logging.Level;

/**
 *
 * @author wolfi
 */
public final class BranchInstruction extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xfc00;
  public static final int OPCODE_SET = 0xf000;
  public static final int OPCODE_CLR = 0xf400;
  private final int bitOffset;
  private final int offset;
  private final boolean bitSet;
  private boolean branch;
  private final String toStringValue;

  public BranchInstruction(int opcode)
  {
    super(opcode,
          OPCODE_MASK,
          decodeMnemonic(opcode));
    offset = decodeOffset(opcode);
    bitOffset = decodeBitOffset(opcode);
    bitSet = decodeBitSet(opcode);
    toStringValue = MessageFormat.format("{0} {1,number,0}",
                                         getMnemonic(),
                                         offset);
  }

  private static boolean decodeBitSet(int opcode)
  {
    return (opcode & 0x400) == 0;
  }

  private static int decodeBitOffset(int opcode)
  {
    return opcode & 0x7;
  }

  /**
   * Package private um testen zu ermöglichen
   *
   * @param opcode opcode
   * @return sprungoffset
   */
  static int decodeOffset(int opcode)
  {
    byte tmp = (byte) ((opcode & 0x3f8) >> 3);
    if ((tmp & 0x40) != 0) { // 7bit negativ !!
      tmp |= 0x80;
    }
    return tmp;
  }

  private static String decodeMnemonic(int opcode)
  {
    int bitOffset = decodeBitOffset(opcode);
    StringBuilder builder = new StringBuilder("br");
    switch (bitOffset) {
      case 0:
        builder.append('c');
        break;
      case 1:
        builder.append('z');
        break;
      case 2:
        builder.append('n');
        break;
      case 3:
        builder.append('v');
        break;
      case 4:
        builder.append('s');
        break;
      case 5:
        builder.append('h');
        break;
      case 6:
        builder.append('t');
        break;
      case 7:
        builder.append('i');
        break;
      default:
        throw new IllegalArgumentException("illegal bitoffset " + bitOffset);
    }
    if (bitOffset != 7) {
      if (decodeBitSet(opcode)) {
        builder.append('s');
      } else {
        builder.append('c');
      }
    } else if (decodeBitSet(opcode)) {
      builder.append('e');
    } else {
      builder.append('d');
    }
    return builder.toString();
  }

  public int getBitIndex()
  {
    return bitOffset;
  }

  public int getOffset()
  {
    return offset;
  }

  public boolean isBitSet()
  {
    return bitSet;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      final CPU cpu = device.getCPU();
      final SREG sreg = cpu.getSREG();
      if (bitSet) {
        branch = sreg.getBit(bitOffset);
      } else {
        branch = !sreg.getBit(bitOffset);
      }
      if (branch) {
        finishCycle = clockState.getCycleCount() + 1;
      } else {
        finishCycle = clockState.getCycleCount();
      }
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} bit {1,number,0} in SREG is {2}set.",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          bitOffset,
                                                          sreg.getBit(bitOffset) ? "" : "not "));
      }
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           final Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      final int nextIp;
      if (branch) {
        nextIp = device.getCPU().getIP() + 1 + offset;
      } else {
        nextIp = device.getCPU().getIP() + 1;
      }
      resultBuilder.finished(true,
                             nextIp);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> {
                                 final int addressWidth = device.getFlash().getHexAddressStringWidth();
                                 try {
                                   Instruction nextInstruction = device.getCPU().getInstructionDecoder().getInstruction(device,
                                                                                                                        nextIp);
                                   return MessageFormat.format("{0} next instrction is {1} @ip={2}",
                                                               getCurrentDeviceMessage(clockState,
                                                                                       device),
                                                               nextInstruction.toString(),
                                                               HexIntAdapter.toHexString(nextIp,
                                                                                         addressWidth));
                                 } catch (NullPointerException | IllegalArgumentException | InstructionNotAvailableException ex) {
                                   device.getLogger().log(Level.SEVERE,
                                                          MessageFormat.format("get instruction @ip={0}",
                                                                               new Object[]{HexIntAdapter.toHexString(nextIp,
                                                                                                                      addressWidth)}),
                                                          ex);
                                   throw new IllegalStateException();
                                 }
                               });
      }
    }
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
