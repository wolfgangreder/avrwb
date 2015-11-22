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

import com.avrwb.annotations.InstructionImplementation;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xff00, opcodes = {0x9800, 0x9a00})
public final class SetClearIOBit extends Instruction_P_b
{

  private final boolean setBit;

  public SetClearIOBit(AvrDeviceKey deviceKey,
                       int opcode,
                       int nextOpcode)
  {
    super(opcode,
          decodeMnemonic(opcode));
    setBit = (opcode & 0x200) != 0;
  }

  private static String decodeMnemonic(int opcode)
  {
    if ((opcode & 0x200) != 0) {
      return "sbi";
    } else {
      return "cbi";
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      final Memory sram = device.getSRAM();
      int oldVal = portVal;
      final int bitMask = getBitMask();
      if (setBit) {
        portVal |= bitMask;
      } else {
        portVal &= ~bitMask;
      }
      if (oldVal != portVal) {
        int address = getPortAddress() + AVRWBDefaults.PORT_ADDRESS_OFFSET;
        sram.setByteAt(address,
                       portVal);
        resultBuilder.addModifiedDataAddresses(address);
      }
      resultBuilder.finished(setBit,
                             device.getCPU().getIP() + 1);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} {1} bit {2,number,0} at io 0x{3}",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          setBit ? "set" : "clear",
                                                          getBitOffset(),
                                                          Integer.toHexString(getPortAddress())));
      }
    }
  }

}
