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
public abstract class Instruction_Rdl_K6 extends AbstractInstruction
{

  private final int rdlAddress;
  private final int k6;
  private final String toStringValue;
  protected int rdVal;

  public Instruction_Rdl_K6(int opcode,
                            String mnemonic)
  {
    super(opcode,
          mnemonic);
    rdlAddress = ((opcode & 0x30) >> 3) + 24;
    k6 = (opcode & 0xf) + ((opcode & 0xc0) >> 2);
    toStringValue = MessageFormat.format("{0} r{2,number,0}:r{1,number,0}, 0x{3}",
                                         mnemonic,
                                         rdlAddress,
                                         rdlAddress + 1,
                                         Integer.toHexString(k6));
  }

  public final int getRdlAddress()
  {
    return rdlAddress;
  }

  public final int getK6()
  {
    return k6;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      rdVal = device.getSRAM().getWordAt(rdlAddress);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        Logger logger = device.getLogger();
        logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                   "{0} reading rdVal r{2,number,0}:r{1,number,0}={3}",
                   new Object[]{getCurrentDeviceMessage(clockState,
                                                        device),
                                rdlAddress,
                                rdlAddress + 1,
                                Converter.printHexString(rdVal,
                                                         4)});
      }
      finishCycle = clockState.getCycleCount() + 1;
    }
  }

  protected void logExecutionResult(ClockState clockState,
                                    Device device,
                                    int result,
                                    int rdlAddress)
  {
    if (AVRWBDefaults.isDebugLoggingActive()) {
      device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                             ()
                             -> MessageFormat.format("{0} writing result {1} to r{3,number,0}:r{2,number,0}",
                                                     getCurrentDeviceMessage(clockState,
                                                                             device),
                                                     Converter.printHexString(result,
                                                                              4),
                                                     rdlAddress,
                                                     rdlAddress + 1));
    }
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
