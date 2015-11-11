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
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.NotThreadSave;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;
import at.reder.avrwb.avr8.helper.AVRWBDefaults;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
@NotThreadSave
public abstract class Instruction_Rd_Rr extends AbstractInstruction
{

  public static final int MASK = 0xfc00;
  protected int rdVal;
  protected int rrVal;
  private final String toStringVal;

  public Instruction_Rd_Rr(int opcode,
                           String mnemonic)
  {
    super(opcode,
          MASK,
          mnemonic);
    toStringVal = MessageFormat.format("{0} r{1,number,0}, r{2,number,0}",
                                       getMnemonic(),
                                       getRdAddress(),
                                       getRrAddress());
  }

  public final int getRdAddress()
  {
    return (getOpcode() & 0x1f0) >> 4;
  }

  public final int getRrAddress()
  {
    return ((getOpcode() & 0x200) >> 5) | (getOpcode() & 0xf);
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
    final int rrAddress = getRrAddress();
    final int rdAddress = getRdAddress();
    rdVal = device.getSRAM().getByteAt(rdAddress);
    rrVal = device.getSRAM().getByteAt(rrAddress);
    if (AVRWBDefaults.isDebugLoggingActive()) {
      Logger logger = device.getLogger();
      logger.log(Level.FINEST,
                 "{0} reading rdVal{1}={2}",
                 new Object[]{getCurrentDeviceMessage(clockState,
                                                      device),
                              HexIntAdapter.toHexString(rdAddress,
                                                        2),
                              HexIntAdapter.toHexString(rdVal,
                                                        2)});
      logger.log(Level.FINEST,
                 "{0} reading rrVal{1}={2}",
                 new Object[]{getCurrentDeviceMessage(clockState,
                                                      device),
                              HexIntAdapter.toHexString(rrAddress,
                                                        2),
                              HexIntAdapter.toHexString(rrVal,
                                                        2)});
    }
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
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
      device.getLogger().log(Level.FINEST,
                             ()
                             -> MessageFormat.format("{0} Writing result {1} to r{2,number,00}",
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
    return toStringVal;
  }

}
