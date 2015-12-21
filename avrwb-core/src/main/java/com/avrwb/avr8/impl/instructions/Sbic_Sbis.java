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
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionNotAvailableException;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.api.SimulationError;

/**
 *
 * @author wolfi
 */
@InstructionImplementation(opcodeMask = 0xff00, opcodes = {0x9900, 0x9b00})
public final class Sbic_Sbis extends Instruction_P_b
{

  public static final int OPCODE_SBIC = 0x9900;
  public static final int OPCODE_SBIS = 0x9b00;
  private final boolean bitState;
  private boolean skip;
  private int nextIp;

  public static Sbic_Sbis getInstance(AvrDeviceKey deviceKey,
                                      int opcode,
                                      int nextOpcode)
  {
    return new Sbic_Sbis(deviceKey,
                         opcode,
                         nextOpcode);
  }

  private Sbic_Sbis(AvrDeviceKey deviceKey,
                    int opcode,
                    int nextOpcode)
  {
    super(opcode,
          ((opcode & OPCODE_MASK) == OPCODE_SBIS) ? "sbis" : "sbic");
    bitState = (opcode & OPCODE_MASK) == OPCODE_SBIS;
  }

  @Override
  protected void doPrepare(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == -1) {
      super.doPrepare(clockState,
                      device,
                      resultBuilder);
      final CPU cpu = device.getCPU();
      Instruction i;
      try {
        i
                = cpu.getInstructionDecoder().getInstruction(device,
                                                             cpu.getIP() + 2);
      } catch (InstructionNotAvailableException ex) {
        resultBuilder.addSimulationEvent(new SimulationError(clockState,
                                                             ex));
        i = Nop.getInstance(device.getDeviceKey(),
                            0,
                            0);
      }
      skip = ((port.getValue() & getBitMask()) != 0) == bitState;
      if (skip) {
        if (i.getSize() > 2) {
          finishCycle = clockState.getCycleCount() + 2;
          nextIp = cpu.getIP() + 3;
        } else {
          finishCycle = clockState.getCycleCount() + 1;
          nextIp = cpu.getIP() + 2;
        }
      } else {
        nextIp = cpu.getIP() + 1;
        finishCycle = clockState.getCycleCount();
      }
    }
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == clockState.getCycleCount()) {
      resultBuilder.finished(true,
                             nextIp);
    }
  }

}
