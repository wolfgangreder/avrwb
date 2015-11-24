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
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.Instruction;
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
@InstructionImplementation(opcodeMask = 0xfc00, opcodes = {0x1000})
public final class Cpse extends Instruction_Rd_Rr
{

  public static final int OPCODE = 0x1000;
  private int cycles = 1;
  private int nextIpDelta = 1;
  private Instruction nextInstruction;

  public static Cpse getInstance(AvrDeviceKey deviceKey,
                                 int opcode,
                                 int nextOpcode)
  {
    return new Cpse(deviceKey,
                    opcode,
                    nextOpcode);
  }

  private Cpse(AvrDeviceKey deviceKey,
               int opcode,
               int nextOpcode)
  {
    super(opcode,
          "cpse");
  }

  @Override
  public int getCycleCount()
  {
    return cycles;
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
      finishCycle = clockState.getCycleCount();
      if (rdVal == rrVal) {
        if (AVRWBDefaults.isDebugLoggingActive()) {
          device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                                 () -> MessageFormat.format("{0} r{1,number,0}==r{2,number,0} -> skip next instruction",
                                                            getCurrentDeviceMessage(clockState,
                                                                                    device),
                                                            rdAddress,
                                                            rrAddress));
        }
        CPU cpu = device.getCPU();
        nextInstruction = device.getCPU().getInstructionDecoder().getInstruction(device,
                                                                                 cpu.getIP() + 1);
        if (AVRWBDefaults.isDebugLoggingActive()) {
          device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                                 () -> MessageFormat.format("{0} next instruction is {1} @ip {2}",
                                                            getCurrentDeviceMessage(clockState,
                                                                                    device),
                                                            nextInstruction.toString(),
                                                            Converter.printHexString(cpu.getIP() + 1,
                                                                                     device.getFlash().getHexAddressStringWidth())));
        }
        if (nextInstruction.getSize() == 2) {
          cycles = 2;
          nextIpDelta = 2;
          finishCycle = clockState.getCycleCount() + 1;
        } else if (nextInstruction.getSize() == 4) {
          cycles = 3;
          nextIpDelta = 3;
          finishCycle = clockState.getCycleCount() + 2;
        }
      } else if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               ()
                               -> MessageFormat.format("{0} r{1,number,0}!=r{2,number,0} -> no skip",
                                                       getCurrentDeviceMessage(clockState,
                                                                               device),
                                                       rdAddress,
                                                       rrAddress));
      }
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (clockState.getCycleCount() == finishCycle) {
      CPU cpu = device.getCPU();
      int nextIp = cpu.getIP() + nextIpDelta;
      resultBuilder.finished(true,
                             nextIp);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} next instruction is {1}| next IP={2}, #CY={3,number,0} ",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          nextInstruction.toString(),
                                                          Converter.printHexString(nextIp,
                                                                                   device.getFlash().getHexAddressStringWidth()),
                                                          cycles));
      }
    }
  }

}
