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

import com.avrwb.avr8.Device;
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstructionResultBuilder;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.schema.AvrFamily;
import com.avrwb.schema.util.Converter;
import java.text.MessageFormat;
import java.util.logging.Logger;

// TODO timingfeinheiten bezüglich externes ram und intr. implementieren
public abstract class Instruction_LdSt extends AbstractInstruction
{

  public static final int OPCODE_MASK = 0xfe0f;
  public static final int OPCODE_MASK_Q = 0xd208;

  public static int composeOpcode(int baseOpcode,
                                  Pointer ptr,
                                  int rd,
                                  Instruction_LdSt.Mode mode,
                                  int displacement)
  {
    if (rd > 31 || rd < 0) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    int result = baseOpcode;
    if (null != mode) {
      switch (mode) {
        case POST_INCREMENT:
          result |= 0x1001;
          break;
        case PRE_DECREMENT:
          result |= 0x1002;
          break;
        case DISPLACEMENT:
          if (ptr == Pointer.X) {
            throw new IllegalArgumentException("displacement only implemented for Y and Z");
          }
          if (displacement < 0) {
            throw new IllegalArgumentException("displacement <0");
          }
          if (displacement > 63) {
            throw new IllegalArgumentException("displacement > 63");
          }
          result = result | ((displacement & 0x20) << 0x8) | ((displacement & 0x18) << 7) | (displacement & 0x7);
          break;
        default:
          break;
      }
    }
    return result | ((rd << 4));
  }

  public enum Mode
  {
    UNMODIFIED,
    POST_INCREMENT,
    PRE_DECREMENT,
    DISPLACEMENT;
  }

  protected final int rdAddress;
  protected final Mode mode;
  protected final Pointer ptr;
  protected final int displacement;
  protected final String toStringValue;
  protected int rdVal;
  protected int ptrVal;
  protected int originalPtrVal;
  protected int pointeeVal;
  protected boolean smallPtr;

  protected static Pointer getPtr(int opcode,
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

  protected static int getDisplacement(int opcode)
  {
    int tmp1 = opcode & OPCODE_MASK_Q;
    if (tmp1 != 0x8208 && tmp1 != 0x8200 && tmp1 != 0x8008 && tmp1 != 0x8000) {
      return 0;
    }
    tmp1 = opcode & 0x7;
    int tmp2 = (opcode & 0xc00) >> 7;
    int tmp3 = (opcode & 0x2000) >> 8;
    return tmp1 + tmp2 + tmp3;
  }

  protected static Mode getMode(int opcode,
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

  protected Instruction_LdSt(int opcode,
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

  public final int getRdAddress()
  {
    return rdAddress;
  }

  public final int getDisplacement()
  {
    return displacement;
  }

  public final Pointer getPointer()
  {
    return ptr;
  }

  public final Mode getMode()
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
      smallPtr = sram.getHexAddressStringWidth() < 3;
      rdVal = sram.getByteAt(rdAddress);
      ptrVal = sram.getPointer(ptr);
      if (smallPtr) {
        ptrVal &= 0xff;
      } else {
        Register ramp = device.getCPU().getRAMP(ptr);
        if (ramp != null) {
          ptrVal += ramp.getValue() << 16;
        }
      }
      int finishDelta = 0;
      originalPtrVal = ptrVal;
      if (mode == Mode.PRE_DECREMENT) {
        ptrVal = (ptrVal - 1) & 0xffffff;
      } else if (displacement != 0) {
        ptrVal += displacement;
        originalPtrVal = ptrVal;
      }
      pointeeVal = sram.getByteAt(ptrVal);
      final boolean external = sram.isAddressExternal(ptrVal);
      if (mode == Mode.DISPLACEMENT) {
        finishDelta = 1;
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
  public String toString()
  {
    return toStringValue;
  }

}
