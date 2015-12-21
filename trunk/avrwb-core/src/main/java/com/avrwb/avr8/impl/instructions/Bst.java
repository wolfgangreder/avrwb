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

import com.avrwb.annotations.InstructionImplementation;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.api.AVRWBDefaults;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xfe08, opcodes = {0xfa00})
public final class Bst extends Instruction_Rd_b
{

  public static final int OPCODE = 0xfa00;

  public static Bst getInstance(AvrDeviceKey deviceKey,
                                int opcode,
                                int nextOpcode)
  {
    return new Bst(deviceKey,
                   opcode,
                   nextOpcode);
  }

  private Bst(AvrDeviceKey deviceKey,
              int opcode,
              int nextOpcode)
  {
    super(opcode,
          "bst");
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    final SREG sreg = device.getCPU().getSREG();
    final int oldSREG = sreg.getValue();
    final boolean set = (rdVal & bitMask) != 0;
    sreg.setT(set);
    if (oldSREG != sreg.getValue()) {
      resultBuilder.addModifiedRegister(sreg);
    }
    resultBuilder.finished(true,
                           device.getCPU().getIP() + 1);
    if (AVRWBDefaults.isDebugLoggingActive()) {
      device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                             () -> MessageFormat.format("{0} {1} T bit in SREG",
                                                        getCurrentDeviceMessage(clockState,
                                                                                device),
                                                        set ? "set" : "clear"));
    }
  }

}
