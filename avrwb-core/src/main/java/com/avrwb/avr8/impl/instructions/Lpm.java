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
import com.avrwb.annotations.InstructionImplementations;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.InstructionResultBuilder;

/**
 *
 * @author wolfi
 */
@InstructionImplementations({
  @InstructionImplementation(opcodeMask = 0xffff, opcodes = 0x95c8),
  @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x9004, 0x9005})
})
public final class Lpm extends AbstractInstruction
{

  public static final int OPCODE_MASK_LPM = 0xffff;
  public static final int OPCODE_MASK_LPM_RD = 0xfe0f;
  public static final int OPCODE_LPM = 0x95c8;
  public static final int OPCODE_LPM_RD = 0x9004;
  public static final int OPCODE_LPM_RD_P = 0x9005;
  private final int rdAddress;
  private final boolean postIncrement;
  private int pointer;
  private int pointee;
  private int rdVal;
  private int rampzVal;

  public static Lpm getInstance(AvrDeviceKey deviceKey,
                                int opcode,
                                int nextOpcode)
  {
    return new Lpm(opcode);
  }

  private Lpm(int opcode)
  {
    super(opcode,
          "lpm");
    switch (opcode & OPCODE_MASK_LPM_RD) {
      case OPCODE_LPM_RD:
        rdAddress = (opcode & 0x1f0) >> 4;
        postIncrement = false;
        break;
      case OPCODE_LPM_RD_P:
        rdAddress = (opcode & 0x1f0) >> 4;
        postIncrement = true;
        break;
      default:
        postIncrement = false;
        rdAddress = 0;
    }
  }

  @Override
  public int getCycleCount()
  {
    return 3;
  }

  @Override
  protected void doPrepare(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == -1) {
      SRAM sram = device.getSRAM();
      rdVal = sram.getByteAt(rdAddress);
      Register rampz = device.getCPU().getRAMP(Pointer.Z);
      if (rampz != null) {
        rampzVal = rampz.getValue();
      } else {
        rampzVal = 0;
      }
      pointer = computePointer(Pointer.Z,
                               device);
      pointee = device.getFlash().getByteAt(pointer);
      finishCycle = clockState.getCycleCount() + 2;
    }
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == clockState.getCycleCount()) {
      SRAM sram = device.getSRAM();
      if (rdVal != pointee) {
        sram.setByteAt(rdAddress,
                       pointee);
        resultBuilder.addModifiedDataAddresses(rdAddress);
      }
      if (postIncrement) {
        int newPointer = pointer + 1;
        if (getLoPart(pointer) != getLoPart(newPointer)) {
          sram.setByteAt(30,
                         getLoPart(newPointer));
          resultBuilder.addModifiedDataAddresses(30);
        }
        if (getHiPart(pointer) != getHiPart(newPointer)) {
          sram.setByteAt(31,
                         getHiPart(newPointer));
          resultBuilder.addModifiedDataAddresses(31);
        }
        if (getRampPart(pointer) != getRampPart(newPointer)) {
          Register rampz = device.getCPU().getRAMP(Pointer.Z);
          if (rampz != null) {
            resultBuilder.addModifiedRegister(rampz);
            rampz.setValue(getRampPart(newPointer));
          }
        }
      }
      resultBuilder.finished(true,
                             device.getCPU().getIP() + 1);
    }
  }

}
