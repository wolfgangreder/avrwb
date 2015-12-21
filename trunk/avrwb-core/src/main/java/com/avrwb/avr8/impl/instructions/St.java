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
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.api.AVRWBDefaults;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
@InstructionImplementations({
  @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x920c, 0x920d, 0x920e}), // ld Rd,X
  @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x8208, 0x9209, 0x920a}), // ld Rd,Y
  @InstructionImplementation(opcodeMask = 0xd208, opcodes = {0x8208}), // ld Rd,Y+q
  @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x8200, 0x9201, 0x9202}), // ld Rd,Z
  @InstructionImplementation(opcodeMask = 0xd208, opcodes = {0x8200})})
public final class St extends Instruction_LdSt
{

  public static final int OPCODE_ST_X = 0x920c;
  public static final int OPCODE_ST_X_P = 0x920d;
  public static final int OPCODE_ST_X_M = 0x920e;
  public static final int OPCODE_ST_Y = 0x8208;
  public static final int OPCODE_ST_Y_P = 0x9209;
  public static final int OPCODE_ST_Y_M = 0x920a;
  public static final int OPCODE_ST_Z = 0x8200;
  public static final int OPCODE_ST_Z_P = 0x9201;
  public static final int OPCODE_ST_Z_M = 0x9202;

  public static St getInstance(AvrDeviceKey deviceKey,
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
    StringBuilder strBuilder = new StringBuilder("st");
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
    return new St(opcode,
                  menmonic,
                  rdAddress,
                  mode,
                  displacement,
                  ptr,
                  strBuilder.toString());
  }

  private St(int opcode,
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
  protected void doExecute(ClockDomain clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder)
  {
    if (finishCycle == clockState.getCycleCount()) {
      final SRAM sram = device.getSRAM();
      final int oldRdVal = rdVal;
      if (oldRdVal != pointeeVal) {
        sram.setByteAt(ptrVal,
                       rdVal);
        resultBuilder.addModifiedDataAddresses(ptrVal);
      }
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
            ramp.setValue(getRampPart(ptrVal));
            resultBuilder.addModifiedRegister(ramp);
          }
        }
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
