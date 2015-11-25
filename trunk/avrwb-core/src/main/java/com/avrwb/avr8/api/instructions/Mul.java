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
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.SREG;
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
@InstructionImplementation(opcodeMask = 0xfc00, opcodes = 0x9c00)
public final class Mul extends Instruction_Rd_Rr
{

  public static final int OPCODE = 0x9c00;

  public static Mul getInstance(AvrDeviceKey deviceKey,
                                int opcode,
                                int nextOpcode)
  {
    return new Mul(deviceKey,
                   opcode,
                   nextOpcode);
  }

  private Mul(AvrDeviceKey deviceKey,
              int opcode,
              int nextOpcode)
  {
    super(opcode,
          "mul");
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      super.doPrepare(clockState,
                      device,
                      resultBuilder);
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      final Memory sram = device.getSRAM();
      final CPU cpu = device.getCPU();
      final SREG sreg = cpu.getSREG();
      int oldR0 = sram.getByteAt(0);
      int oldR1 = sram.getByteAt(1);
      int oldSREG = sreg.getValue();
      int result = (rdVal * rrVal) & 0xffff;
      sreg.setC((result & 0x8000) != 0);
      sreg.setZ(result == 0);
      if (oldSREG != sreg.getValue()) {
        resultBuilder.addModifiedRegister(sreg);
      }
      if (oldR0 != (result & 0xff)) {
        resultBuilder.addModifiedDataAddresses(0);
      }
      if (oldR1 != ((result & 0xff) >> 8)) {
        resultBuilder.addModifiedDataAddresses(1);
      }
      resultBuilder.finished(true,
                             cpu.getIP() + 1);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} writing result {1} to r1:r0",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          Converter.printHexString(result,
                                                                                   4)));
      }
    }
  }

}
