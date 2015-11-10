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
import java.text.MessageFormat;
import java.util.logging.Level;

/**
 *
 * @author wolfi
 */
public final class Mov extends Instruction_Rd_Rr
{

  private final String toStringVal;

  public Mov(int opcode)
  {
    super(opcode,
          "MOV");
    toStringVal = MessageFormat.format("{0} R{1,number,00},R{2,number,00}",
                                       getMnemonic(),
                                       getRdAddress(),
                                       getRrAddress());
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    switch (clockState.getPhase()) {
      case RISING:
        readValues(clockState,
                   device);
        break;
      case FALLING: {
        int rdAddress = getRdAddress();
        device.getSRAM().setByteAt(rdAddress,
                                   rdVal);
        resultBuilder.finished(true);
        resultBuilder.addModifiedDataAddresses(rdAddress);
        resultBuilder.nextIp(device.getCPU().getIP() + getSize());
        if (AVRWBDefaults.isDebugLoggingActive()) {
          device.getLogger().log(Level.FINEST,
                                 "{0} Writing result {1} to R{2,number,00}",
                                 new Object[]{
                                   getCurrentDeviceMessage(clockState,
                                                           device),
                                   HexIntAdapter.toHexString(rdVal,
                                                             2),
                                   rdAddress});
        }
      }
    }
  }

  @Override
  public String toString()
  {
    return toStringVal;
  }

}
