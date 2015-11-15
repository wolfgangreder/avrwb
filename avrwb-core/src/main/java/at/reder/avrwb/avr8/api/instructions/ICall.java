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
import at.reder.avrwb.avr8.CPU;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.Pointer;
import at.reder.avrwb.avr8.Stack;
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstructionResultBuilder;
import at.reder.avrwb.avr8.helper.AVRWBDefaults;
import at.reder.avrwb.avr8.helper.SimulationException;
import java.text.MessageFormat;

/**
 *
 * @author wolfi
 */
public final class ICall extends AbstractInstruction
{

  public static final int OPCODE = 0x9509;
  private int callTarget;
  private boolean longCall;

  public ICall()
  {
    super(OPCODE,
          0xffff,
          "icall");
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      longCall = device.getFlash().getHexAddressStringWidth() > 4;
      callTarget = device.getSRAM().getPointer(Pointer.Z);
      if (longCall) {
        finishCycle = clockState.getCycleCount() + 3;
      } else {
        finishCycle = clockState.getCycleCount() + 2;
      }
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      final Stack stack = device.getStack();
      final CPU cpu = device.getCPU();
      final int ipToPush = cpu.getIP() + 2;
      int toPush = ipToPush & 0xff;
      int oldByte = stack.push(toPush);
      resultBuilder.addModifiedRegister(cpu.getStackPointer());
      if (oldByte != toPush) {
        resultBuilder.addModifiedDataAddresses(stack.getSP() - 1);
      }
      toPush = (ipToPush & 0xff00) >> 8;
      oldByte = stack.push(toPush);
      if (oldByte != toPush) {
        resultBuilder.addModifiedDataAddresses(stack.getSP() - 1);
      }
      if (longCall) {
        toPush = (ipToPush & 0x3f0000) >> 16;
        oldByte = stack.push(toPush);
        if (oldByte != toPush) {
          resultBuilder.addModifiedDataAddresses(stack.getSP() - 1);
        }
      }
      resultBuilder.finished(true,
                             callTarget);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        device.getLogger().log(AVRWBDefaults.getInstructionTraceLevel(),
                               () -> MessageFormat.format("{0} calling ip {1}",
                                                          getCurrentDeviceMessage(clockState,
                                                                                  device),
                                                          HexIntAdapter.toHexString(callTarget,
                                                                                    device.getFlash().getHexAddressStringWidth())));
      }

    }
  }

}
