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
import com.avrwb.avr8.api.AVRWBDefaults;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
public abstract class Instruction_Rd extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xfe0f;

  protected int rdVal;
  private final String toStringVal;

  public Instruction_Rd(int opcode,
                        String mnemonic)
  {
    super(opcode,
          mnemonic);
    toStringVal = MessageFormat.format("{0} r{1,number,0}",
                                       getMnemonic(),
                                       getRdAddress());

  }

  public final int getRdAddress()
  {
    return (getOpcode() & 0x1f0) >> 4;
  }

  @Override
  protected void doPrepare(ClockDomain clockDomain,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == -1) {
      final int rdAddress = getRdAddress();
      rdVal = device.getSRAM().getByteAt(rdAddress);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        Logger logger = device.getLogger();
        logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                   "{0} reading rdVal r{1,number,0}={2}",
                   new Object[]{getCurrentDeviceMessage(clockDomain,
                                                        device),
                                rdAddress,
                                Converter.printHexString(rdVal,
                                                         2)});
      }
      finishCycle = clockDomain.getState().getCycleCount() + getCycleCount() - 1;
    }
  }

  protected void logExecutionResult(ClockDomain clockDomain,
                                    Device device,
                                    int result,
                                    int rdAddress)
  {
    if (AVRWBDefaults.isDebugLoggingActive()) {
      device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                             ()
                             -> MessageFormat.format("{0} writing result {1} to r{2,number,0}",
                                                     getCurrentDeviceMessage(clockDomain,
                                                                             device),
                                                     Converter.printHexString(result,
                                                                              2),
                                                     rdAddress));
    }
  }

  @Override
  public String toString()
  {
    return toStringVal;
  }

}
