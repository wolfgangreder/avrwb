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
import com.avrwb.avr8.api.SimulationWarning;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author wolfi
 */
@InstructionImplementations({
  @InstructionImplementation(opcodeMask = 0xffff,
                             opcodes = 0x95d8),
  @InstructionImplementation(opcodeMask = 0xfe0f,
                             opcodes = {0x9006, 0x9007}
  )})
@Messages({"Elpm_error_rampz_not_found=RAMPZ not implemented"})
public final class Elpm extends AbstractInstruction
{

  public static final int OPCODE_ELPM = 0x95d8;
  public static final int OPCODE_MASK_ELPM = 0xffff;
  public static final int OPCODE_ELPM_RD = 0x9006;
  public static final int OPCODE_ELPM_RDP = 0x9007;
  public static final int OPCODE_MASK_ELPM_RD = 0xfe0f;

  public static Elpm getInstance(AvrDeviceKey deviceKey,
                                 int opcode,
                                 int nextOpcode)
  {
    if (opcode == OPCODE_ELPM) {
      return new Elpm(opcode,
                      0,
                      "elpm",
                      false);
    } else {
      int rdAddress = (opcode & ~OPCODE_MASK_ELPM_RD) >> 4;
      boolean pi = (opcode & 0x1) != 0;
      StringBuilder toStringVal = new StringBuilder("elpm r");
      toStringVal.append(rdAddress);
      toStringVal.append(", Z");
      if (pi) {
        toStringVal.append("+");
      }
      return new Elpm(opcode,
                      rdAddress,
                      "elpm",
                      pi);
    }
  }

  private final int rdAddress;
  private final boolean postIncrement;
  private int z;
  private int rampz;
  private int rdVal;

  private Elpm(int opcode,
               int rdAddress,
               String mnemonic,
               boolean postIncrement)
  {
    super(opcode,
          mnemonic);
    this.rdAddress = rdAddress;
    this.postIncrement = postIncrement;
  }

  public int getRdAddress()
  {
    return rdAddress;
  }

  public boolean isPostIncrement()
  {
    return postIncrement;
  }

  @Override
  protected void doPrepare(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == -1) {
      Register rz = device.getCPU().getRAMP(Pointer.Z);
      if (rz == null) {
        resultBuilder.addSimulationEvent(new SimulationWarning(clockState,
                                                               () -> Bundle.Elpm_error_rampz_not_found()));
      }
      z = device.getSRAM().getPointer(Pointer.Z);
      rampz = rz != null ? rz.getValue() : 0;
      rdVal = device.getSRAM().getByteAt(rdAddress);
      finishCycle = clockState.getCycleCount() + 2;
    }
  }

  @Override
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (clockState.getCycleCount() == finishCycle) {
      final SRAM sram = device.getSRAM();
      int oldVal = rdVal;
      int address = z + (rampz << 16);
      rdVal = device.getFlash().getByteAt(address);
      if (oldVal != rdVal) {
        resultBuilder.addModifiedDataAddresses(rdAddress);
        sram.setByteAt(rdAddress,
                       rdVal);
      }
      if (postIncrement) {
        int newaddress = address + 1;
        if ((address & 0xff) != (newaddress & 0xff)) {
          resultBuilder.addModifiedDataAddresses(30);
          sram.setByteAt(30,
                         newaddress & 0xff);
        }
        if ((address & 0xff00) != (newaddress & 0xff00)) {
          resultBuilder.addModifiedDataAddresses(31);
          sram.setByteAt(31,
                         (newaddress >> 8) & 0xff);
        }
        if ((address & 0xff0000) != (newaddress & 0xff0000)) {
          Register rz = device.getCPU().getRAMP(Pointer.Z);
          resultBuilder.addModifiedRegister(rz);
          rz.setValue((newaddress >> 16) & 0xff);
        }
      }
      resultBuilder.finished(true,
                             device.getCPU().getIP() + 1);
    }
  }

}
