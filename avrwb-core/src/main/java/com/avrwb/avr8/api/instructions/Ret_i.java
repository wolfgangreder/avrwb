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
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.Stack;
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
@InstructionImplementation(opcodeMask = 0xffff, opcodes = {0x9508, 0x9518})
public final class Ret_i extends AbstractInstruction
{

  public static int OPCODE_RET = 0x9508;
  public static int OPCODE_RETI = 0x9518;
  private boolean longRet;

  public Ret_i(AvrDeviceKey deviceKey,
               int opcode,
               int nextOpcode)
  {
    super(opcode,
          isReti(opcode) ? "reti" : "ret");
  }

  private static boolean isReti(int opcode)
  {
    return (opcode & 0x10) != 0;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      longRet = device.getFlash().getHexAddressStringWidth() > 4;
      if (longRet) {
        finishCycle = clockState.getCycleCount() + 4;
      } else {
        finishCycle = clockState.getCycleCount() + 3;
      }
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      final CPU cpu = device.getCPU();
      final SREG sreg = cpu.getSREG();
      final Stack stack = device.getStack();
      resultBuilder.addModifiedRegister(cpu.getStackPointer());
      int tmpIP = (stack.pop() << 8) + stack.pop();
      if (longRet) {
        tmpIP = (tmpIP << 8) + stack.pop();
      }
      final int targetIP = tmpIP;
      if (isReti(getOpcode()) && !sreg.getI()) {
        sreg.setI(true);
        resultBuilder.addModifiedRegister(sreg);
      }
      resultBuilder.finished(true,
                             targetIP);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               ()
                               -> MessageFormat.format("{0} returning to ip {1}",
                                                       getCurrentDeviceMessage(clockState,
                                                                               device),
                                                       Converter.printHexString(targetIP,
                                                                                device.getFlash().getHexAddressStringWidth())));
      }
    }
  }

}
