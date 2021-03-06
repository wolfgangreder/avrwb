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
import com.avrwb.avr8.Register;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.api.AVRWBDefaults;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
public abstract class Instruction_P_b extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xff00;
  private final int portAddress;
  private final int bitOffset;
  private final String toStringValue;
  protected Register port;

  protected Instruction_P_b(int opcode,
                            String mnemonic)
  {
    super(opcode,
          mnemonic);
    portAddress = (opcode & 0xf8) >> 3;
    bitOffset = opcode & 0x7;
    toStringValue = MessageFormat.format("{0} 0x{1}, {2,number,0}",
                                         mnemonic,
                                         Integer.toHexString(portAddress),
                                         bitOffset);
  }

  public final int getBitMask()
  {
    return 1 << bitOffset;
  }

  public final int getPortAddress()
  {
    return portAddress;
  }

  public final int getBitOffset()
  {
    return bitOffset;
  }

  @Override
  protected void doPrepare(ClockDomain clockDomain,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == -1) {
      port = device.getIOSpace().get(portAddress);
      finishCycle = clockDomain.getState().getCycleCount() + 1;
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} reading 0x{2} from io 0x{1}",
                                                          getCurrentDeviceMessage(clockDomain,
                                                                                  device),
                                                          Integer.toHexString(portAddress),
                                                          Integer.toHexString(port.getValue())));
      }
    }
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
