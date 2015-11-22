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

import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;
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
@NotThreadSave
public abstract class Instruction_Rd_Rr extends AbstractInstruction
{

  public static int composeOpcode(int opcode,
                                  int rd,
                                  int rr)
  {
    if ((opcode & ~0xfc00) != 0) {
      throw new IllegalArgumentException("invalid rd,rr opcode" + Integer.toHexString(opcode));
    }
    if (rd > 31 || rd < 0) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    if (rr > 31 || rr < 0) {
      throw new IllegalArgumentException("invalid register r" + rr);
    }
    return opcode | (rd << 4) | ((rr & 0x10) << 5) | (rr & 0xf);
  }

  protected int rdVal;
  protected int rrVal;
  private final String toStringVal;
  protected final int rdAddress;
  protected final int rrAddress;

  public Instruction_Rd_Rr(int opcode,
                           String mnemonic)
  {
    super(opcode,
          mnemonic);
    rdAddress = (opcode & 0x1f0) >> 4;
    rrAddress = ((opcode & 0x200) >> 5) | (opcode & 0xf);
    toStringVal = MessageFormat.format("{0} r{1,number,0}, r{2,number,0}",
                                       mnemonic,
                                       rdAddress,
                                       rrAddress);
  }

  public final int getRdAddress()
  {
    return rdAddress;
  }

  public final int getRrAddress()
  {
    return rrAddress;
  }

  /**
   * List die Werte der Register Rr und Rd in den Zwischenspeicher {@code rdVal} und {@code rrVal}
   *
   * @param clockState clockState
   * @param device device
   */
  protected void readValues(@NotNull ClockState clockState,
                            @NotNull Device device)
  {
    rdVal = device.getSRAM().getByteAt(rdAddress);
    rrVal = device.getSRAM().getByteAt(rrAddress);
    if (AVRWBDefaults.isDebugLoggingActive()) {
      Logger logger = device.getLogger();
      logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                 "{0} reading rdVal r{1,number,0}={2}",
                 new Object[]{getCurrentDeviceMessage(clockState,
                                                      device),
                              rdAddress,
                              Converter.printHexString(rdVal,
                                                       2)});
      logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                 "{0} reading rrVal r{1}={2}",
                 new Object[]{getCurrentDeviceMessage(clockState,
                                                      device),
                              rrAddress,
                              Converter.printHexString(rrVal,
                                                       2)});
    }
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    readValues(clockState,
               device);
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
    return toStringVal;
  }

}