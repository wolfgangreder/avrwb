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
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
public abstract class Instruction_Rd_K8 extends AbstractInstruction
{

  public static int composeOpcode(int opcode,
                                  int rd,
                                  int k8)
  {
    if ((opcode & ~0xf000) != 0) {
      throw new IllegalArgumentException("invalid rd,rr opcode" + Integer.toHexString(opcode));
    }
    if (rd > 31 || rd < 16) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    if (k8 < 0 || k8 > 0xff) {
      throw new IllegalArgumentException("invalid k8 " + k8);
    }
    return opcode | ((k8 & 0xf0) << 4) | (k8 & 0xf) | ((rd - 16) << 4);
  }

  protected final int rdAddress;
  protected final int k8;
  private final String toStringValue;
  protected int rdVal;

  protected Instruction_Rd_K8(int opcode,
                              String mnemonic)
  {
    super(opcode,
          mnemonic);
    rdAddress = ((opcode & 0xf0) >> 4) + 16;
    k8 = (opcode & 0xf) + ((opcode & 0xf00) >> 4);
    toStringValue = MessageFormat.format("{0} r{1,number,0}, 0x{2}",
                                         mnemonic,
                                         rdAddress,
                                         Integer.toHexString(k8));
  }

  public final int getRdAddress()
  {
    return rdAddress;
  }

  public final int getK8()
  {
    return k8;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    rdVal = device.getSRAM().getByteAt(rdAddress);
    if (AVRWBDefaults.isDebugLoggingActive()) {
      Logger logger = device.getLogger();
      logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                 "{0} reading rdVal r{1,number,0}={2}",
                 new Object[]{getCurrentDeviceMessage(clockState,
                                                      device),
                              rdAddress,
                              Converter.printHexString(rdVal,
                                                       2)});
    }
  }

  protected void logExecutionResult(ClockState clockState,
                                    Device device,
                                    int result,
                                    int rdAddress)
  {
    if (AVRWBDefaults.isDebugLoggingActive()) {
      device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                             ()
                             -> MessageFormat.format("{0} writing result {1} to r{2,number,0}",
                                                     getCurrentDeviceMessage(clockState,
                                                                             device),
                                                     Converter.printHexString(result,
                                                                              2),
                                                     rdAddress));
    }
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
