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
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.schema.AvrFamily;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
@InstructionImplementations(factoryMethod = "getInstance",
                            value = {
                              @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x900c, 0x900d, 0x900e}), // ld Rd,X
                              @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x8008, 0x9009, 0x900a}), // ld Rd,Y
                              @InstructionImplementation(opcodeMask = 0xd208, opcodes = {0x8008}), // ld Rd,Y+q
                              @InstructionImplementation(opcodeMask = 0xfe0f, opcodes = {0x8000, 0x9001, 0x9002}), // ld Rd,Z
                              @InstructionImplementation(opcodeMask = 0xd208, opcodes = {0x8000})})
public final class Ld extends AbstractInstruction
{

  public enum Mode
  {
    UNMODIFIED,
    POST_INCREMENT,
    PRE_DECREMENT,
    DISPLACEMENT;
  }

  private final int rdAddress;
  private final Mode mode;
  private final Pointer ptr;
  private final int displacement;
  private final String toStringValue;
  private int rdVal;
  private int ptrVal;
  private int pointeeVal;

  private static Pointer getPtr(int opcode,
                                Mode mode)
  {
    switch (mode) {
      case UNMODIFIED:
      case POST_INCREMENT:
      case PRE_DECREMENT:
        switch (opcode & 0xc) {
          case 0:
            return Pointer.Z;
          case 8:
            return Pointer.Y;
          case 0xc:
            return Pointer.X;
          default:
            return null;
        }
      case DISPLACEMENT:
        switch (opcode & 0x8) {
          case 0:
            return Pointer.Z;
          case 8:
            return Pointer.Y;
          default:
            return null;
        }
      default:
        return null;
    }
  }

  private static int getDisplacement(int opcode)
  {
//    switch (opcode & 0xfe0f) {
//      case 0x900c:
//      case 0x900d:
//      case 0x900e: // X
//        return 0;
//    }
    int tmp1 = opcode & 0xd208;
    if (tmp1 != 0x8008 && tmp1 != 0x8000) {
      return 0;
    }
    tmp1 = opcode & 0x7;
    int tmp2 = (opcode & 0xc00) >> 7;
    int tmp3 = (opcode & 0x2000) >> 8;
    return tmp1 + tmp2 + tmp3;
  }

  private static Mode getMode(int opcode,
                              int displacement)
  {
    if (displacement == 0) {
      switch (opcode & 0x3) {
        case 0:
          return Mode.UNMODIFIED;
        case 1:
          return Mode.POST_INCREMENT;
        case 2:
          return Mode.PRE_DECREMENT;
        default:
          return null;
      }
    } else {
      return Mode.DISPLACEMENT;
    }

  }

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
    if (deviceKey.getFamily() != AvrFamily.XMEGA && mode == Mode.DISPLACEMENT) {
      return null;
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
          mnemonic);
    this.rdAddress = rdAddress;
    this.mode = mode;
    this.displacement = displacement;
    this.toStringValue = toStringValue;
    this.ptr = ptr;
  }

  public int getRdAddress()
  {
    return rdAddress;
  }

  public int getDisplacement()
  {
    return displacement;
  }

  public Pointer getPointer()
  {
    return ptr;
  }

  public Ld.Mode getMode()
  {
    return mode;
  }

  @Override
  protected void doPrepare(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == -1) {
      SRAM sram = device.getSRAM();
      rdVal = sram.getByteAt(rdAddress);
      ptrVal = sram.getPointer(ptr);
      pointeeVal = sram.getByteAt(ptrVal);
      int finishDelta = 0;
      if (mode == Mode.PRE_DECREMENT) {
        ptrVal--;
      } else if (displacement != 0) {
        ptrVal += displacement;
      }
      final boolean external = sram.isAddressExternal(ptrVal);
      if (mode == Mode.DISPLACEMENT) {
        finishDelta = external ? 1 : 2;
      } else if (device.getDeviceKey().getFamily() == AvrFamily.XMEGA) {
        if (mode != Mode.PRE_DECREMENT) {
          finishDelta = external ? 0 : 1;
        } else {
          finishDelta = external ? 1 : 2;
        }
      } else { // family!=Family.AVR_XMEGA
        switch (mode) {
          case POST_INCREMENT:
            finishDelta = 1;
            break;
          case PRE_DECREMENT:
            finishDelta = 2;
        }
      }
      finishCycle = clockState.getCycleCount() + finishDelta;
      if (AVRWBDefaults.isDebugLoggingActive()) {
        final Logger logger = device.getLogger();
        logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                   ()
                   -> MessageFormat.format("{0} reading 0x{1,number,0} from r{2,number,0}",
                                           getCurrentDeviceMessage(clockState,
                                                                   device),
                                           rdVal,
                                           rdAddress));
        logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                   ()
                   -> MessageFormat.format("{0} pointer {1} points to {2}",
                                           getCurrentDeviceMessage(clockState,
                                                                   device),
                                           ptr.name(),
                                           Converter.printHexString(ptrVal,
                                                                    sram.getHexAddressStringWidth())));
        logger.log(AVRWBDefaults.getInstructionTraceLevel(),
                   ()
                   -> MessageFormat.format("{0} reading 0x{1} from sram @{2}{3}",
                                           getCurrentDeviceMessage(clockState,
                                                                   device),
                                           Integer.toHexString(pointeeVal),
                                           Converter.printHexString(ptrVal,
                                                                    sram.getHexAddressStringWidth()),
                                           external ? " ext" : ""));
      }
    }
  }

  @Override
  protected void doExecute(ClockState clockState,
                           Device device,
                           InstructionResultBuilder resultBuilder) throws SimulationException
  {
    if (finishCycle == clockState.getCycleCount()) {
      if (mode == Mode.POST_INCREMENT) {
        ptrVal++;
      }
      final SRAM sram = device.getSRAM();
      final int oldPointerVal = sram.getPointer(ptr);
      final int oldRdVal = rdVal;
      if (ptrVal != oldPointerVal) {
        sram.setPointer(ptr,
                        ptrVal);
        if ((ptrVal & 0xff) != (oldPointerVal & 0xff)) {
          resultBuilder.addModifiedDataAddresses(ptr.getAddressLo());
        }
        if ((ptrVal & 0xff00) != (oldPointerVal & 0xff00)) {
          resultBuilder.addModifiedDataAddresses(ptr.getAddressHi());
        }
      }
      if (oldRdVal != rdVal) {
        sram.setByteAt(rdAddress,
                       rdVal);
        resultBuilder.addModifiedDataAddresses(rdAddress);
      }
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

  @Override
  public String toString()
  {
    return toStringValue;
  }

}
