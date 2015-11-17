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
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;
import at.reder.avrwb.avr8.helper.AVRWBDefaults;
import at.reder.avrwb.avr8.helper.SimulationException;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
public abstract class Instruction_Rd_b extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xfe00;
  protected final int rdAddress;
  protected final int bit;
  protected final int bitMask;
  private final String toStringValue;
  protected int rdVal;

  protected Instruction_Rd_b(int opcode,
                             String mnemonic)
  {
    super(opcode,
          OPCODE_MASK,
          mnemonic);
    rdAddress = (opcode & 0x1f0) >> 4;
    bit = (opcode & 0x7);
    bitMask = 1 << bit;
    toStringValue = MessageFormat.format("{0} r{1,number,0}, {2}",
                                         mnemonic,
                                         rdAddress,
                                         Integer.toString(bit));
  }

  public final int getRdAddress()
  {
    return rdAddress;
  }

  public final int getBit()
  {
    return bit;
  }

  public final int getBitMask()
  {
    return bitMask;
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
                              HexIntAdapter.toHexString(rdVal,
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
                                                     HexIntAdapter.toHexString(result,
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