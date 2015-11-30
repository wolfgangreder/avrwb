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
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xf800, opcodes = {0xb000, 0xb800})
public final class InOut extends AbstractInstruction
{

  public static final int OPCODE_IN = 0xb000;
  public static final int OPCODE_OUT = 0xb800;
  private final boolean in;
  private final int portAddress;
  private final int registerAddress;
  private final String toStringVal;
  private int registerVal;
  private int portVal;

  public static InOut getInstance(AvrDeviceKey deviceKey,
                                  int opcode,
                                  int nextOpcode)
  {
    return new InOut(deviceKey,
                     opcode,
                     nextOpcode);
  }

  private InOut(AvrDeviceKey deviceKey,
                int opcode,
                int nextOpcode)
  {
    super(opcode,
          decodeMnemonic(opcode));
    portAddress = ((opcode & 0x600) >> 5) | (opcode & 0xf);
    registerAddress = (opcode & 0x1f0) >> 4;
    String pattern;
    in = (opcode & 0x800) == 0;
    if (in) {
      pattern = "{0} r{1,number,0}, 0x{2}";
    } else {
      pattern = "{0} 0x{2}, r{1,number,0}";
    }
    toStringVal = MessageFormat.format(pattern,
                                       getMnemonic(),
                                       registerAddress,
                                       Integer.toHexString(portAddress));
  }

  public int getPortAddress()
  {
    return portAddress;
  }

  public int getRegisterAddress()
  {
    return registerAddress;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    final Memory sram = device.getSRAM();
    registerVal = sram.getByteAt(registerAddress);
    portVal = sram.getByteAt(portAddress + AVRWBDefaults.PORT_ADDRESS_OFFSET);
    if (AVRWBDefaults.isDebugLoggingActive()) {
      final Logger logger = device.getLogger();
      logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                 () -> MessageFormat.format("{0} reading 0x{1} from r{2,number,0}",
                                            getCurrentDeviceMessage(clockState,
                                                                    device),
                                            Integer.toHexString(registerVal),
                                            registerAddress));
      logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                 () -> MessageFormat.format("{0} reading 0x{1} from io 0x{2}",
                                            getCurrentDeviceMessage(clockState,
                                                                    device),
                                            Integer.toHexString(portVal),
                                            Integer.toHexString(portAddress)));
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    final Memory sram = device.getSRAM();
    final String pattern;
    final int newVal;
    if (in) {
      newVal = portVal;
      if (registerVal != portVal) {
        sram.setByteAt(registerAddress,
                       portVal);
        resultBuilder.addModifiedDataAddresses(registerAddress);
      }
      pattern = "{0} writing 0x{1} to r{2,number,0}";
    } else {
      newVal = registerVal;
      if (registerVal != portVal) {
        sram.setByteAt(portAddress + AVRWBDefaults.PORT_ADDRESS_OFFSET,
                       registerVal);
        resultBuilder.addModifiedDataAddresses(portAddress + AVRWBDefaults.PORT_ADDRESS_OFFSET);
      }
      pattern = "{0} writing 0x{1} to io 0x{3}";
    }
    if (AVRWBDefaults.isDebugLoggingActive()) {
      device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                             () -> MessageFormat.format(pattern,
                                                        getCurrentDeviceMessage(clockState,
                                                                                device),
                                                        newVal,
                                                        registerAddress,
                                                        Integer.toHexString(portAddress)));
    }
  }

  private static String decodeMnemonic(int opcode)
  {
    if ((opcode & 0x800) == 0) {
      return "in";
    } else {
      return "out";
    }
  }

  @Override
  public String toString()
  {
    return toStringVal;
  }

}
