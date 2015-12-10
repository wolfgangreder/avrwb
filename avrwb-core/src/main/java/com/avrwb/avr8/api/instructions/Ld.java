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
import com.avrwb.annotations.InstructionImplementations;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
@InstructionImplementations({
  @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x900c, 0x900d, 0x900e}), // ld Rd,X
  @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x8008, 0x9009, 0x900a}), // ld Rd,Y
  @InstructionImplementation(opcodeMask = 0xd208, opcodes = {0x8008}), // ld Rd,Y+q
  @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x8000, 0x9001, 0x9002}), // ld Rd,Z
  @InstructionImplementation(opcodeMask = 0xd208, opcodes = {0x8000})})
public final class Ld extends Instruction_LdSt
{

  public static final int OPCODE_LD_X = 0x900c;
  public static final int OPCODE_LD_X_P = 0x900d;
  public static final int OPCODE_LD_X_M = 0x900e;
  public static final int OPCODE_LD_Y = 0x8008;
  public static final int OPCODE_LD_Y_P = 0x9009;
  public static final int OPCODE_LD_Y_M = 0x900a;
  public static final int OPCODE_LD_Z = 0x8000;
  public static final int OPCODE_LD_Z_P = 0x9001;
  public static final int OPCODE_LD_Z_M = 0x9002;

  public static Ld getInstance(AvrDeviceKey deviceKey,
                               int opcode,
                               int nextOpcode)
  {
    int displacement = getDisplacement(opcode);
    Mode mode = getMode(opcode,
                        displacement);
    if (mode == null) {
      return null;
    }
    if (displacement == 0 && mode == Mode.DISPLACEMENT) {
      mode = Mode.UNMODIFIED;
    }
    Pointer ptr = getPtr(opcode,
                         mode);
    if (ptr == null) {
      return null;
    }
    StringBuilder strBuilder = new StringBuilder("ld");
    if (mode == Mode.DISPLACEMENT) {
      strBuilder.append('d');
    }
    final String menmonic = strBuilder.toString();
    final int rdAddress = (opcode & 0x1f0) >> 4;
    strBuilder.append(" r");
    strBuilder.append(rdAddress);
    strBuilder.append(", ");
    if (mode == Mode.PRE_DECREMENT) {
      strBuilder.append('-');
    }
    strBuilder.append(ptr.name());
    if (mode == Mode.POST_INCREMENT || mode == Mode.DISPLACEMENT) {
      strBuilder.append('+');
      if (mode == Mode.DISPLACEMENT) {
        strBuilder.append(displacement);
      }
    }
    return new Ld(opcode,
                  menmonic,
                  rdAddress,
                  mode,
                  displacement,
                  ptr,
                  strBuilder.toString());
  }

  private Ld(int opcode,
             String mnemonic,
             int rdAddress,
             Mode mode,
             int displacement,
             Pointer ptr,
             String toStringValue)
  {
    super(opcode,
          mnemonic,
          rdAddress,
          mode,
          displacement,
          ptr,
          toStringValue);
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      final SRAM sram = device.getSRAM();
      if (mode == Mode.POST_INCREMENT) {
        ptrVal++;
      }
      if (ptrVal != originalPtrVal) {
        sram.setPointer(ptr,
                        ptrVal);
        if ((ptrVal & 0xff) != (originalPtrVal & 0xff)) {
          resultBuilder.addModifiedDataAddresses(ptr.getAddressLo());
        }
        if ((ptrVal & 0xff00) != (originalPtrVal & 0xff00)) {
          resultBuilder.addModifiedDataAddresses(ptr.getAddressHi());
        }
        if ((ptrVal & 0xff0000) != (originalPtrVal & 0xff0000)) {
          Register ramp = device.getCPU().getRAMP(ptr);
          if (ramp != null) {
            resultBuilder.addModifiedRegister(ramp);
            ramp.setValue((ptrVal & 0xff0000) >> 16);
          }
        }
      }
      if (pointeeVal != rdVal) {
        sram.setByteAt(rdAddress,
                       pointeeVal);
        resultBuilder.addModifiedDataAddresses(rdAddress);
      }
      resultBuilder.finished(true,
                             device.getCPU().getIP() + 1);
      if (AVRWBDefaults.isDebugLoggingActive()) {
        Logger logger = device.getLogger();
        logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                   () -> MessageFormat.format("{0} writing 0x{1} to r{2,number,0}",
                                              getCurrentDeviceMessage(clockState,
                                                                      device),
                                              Integer.toHexString(rdVal),
                                              rdAddress));
        logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                   () -> MessageFormat.format("{0} pointer {1} points to {2}",
                                              getCurrentDeviceMessage(clockState,
                                                                      device),
                                              ptr.name(),
                                              Converter.printHexString(ptrVal,
                                                                       sram.getHexAddressStringWidth())));
      }
    }
  }

}
