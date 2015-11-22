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
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xfe0e, opcodes = {0x940c})
public final class Jmp extends AbstractInstruction
{

  private final int callTarget;
  private final String toStringValue;

  public Jmp(AvrDeviceKey deviceKey,
             int opcode,
             int nextOpcode)
  {
    super(opcode << 16 | nextOpcode,
          "jmp");
    int tmp = getOpcode();
    callTarget = (tmp & 0xffff) | ((tmp & 0x1f00000) >> 3) | (tmp & 0x10000);
    toStringValue = "jmp 0x" + Integer.toHexString(callTarget);
  }

  public int getJumpTarget()
  {
    return callTarget;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      finishCycle = clockState.getCycleCount() + 2;
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      resultBuilder.finished(true,
                             callTarget);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} jumping to ip {1}",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          Converter.printHexString(callTarget,
                                                                                   device.getFlash().getHexAddressStringWidth())));
      }

    }
  }

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
