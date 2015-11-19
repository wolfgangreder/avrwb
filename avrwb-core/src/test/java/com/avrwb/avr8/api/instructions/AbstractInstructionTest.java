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

import com.avrwb.avr8.api.instructions.Ld;
import com.avrwb.avr8.api.instructions.Instruction_K4;
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.api.Instruction;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import static org.testng.AssertJUnit.*;

/**
 *
 * @author wolfi
 */
public class AbstractInstructionTest
{

  protected static void checkStaticField(Class<? extends Instruction> clazz,
                                         String fieldName) throws Exception
  {
    Field field = clazz.getField(fieldName);
    assertTrue(fieldName + " is not static",
               Modifier.isStatic(field.getModifiers()));
    assertTrue(fieldName + " is not final",
               Modifier.isFinal(field.getModifiers()));
    assertTrue(fieldName + " is not public",
               Modifier.isPublic(field.getModifiers()));
    Class<?> fieldClazz = field.getType();
    assertEquals(fieldName + " is not of type int",
                 Integer.TYPE,
                 fieldClazz);

  }

  protected static void testClass(Class<? extends Instruction> clazz) throws Exception
  {
    assertTrue(clazz.getName() + " is not final",
               Modifier.isFinal(clazz.getModifiers()));
  }

  protected int constructOpcodeCallJmp(int baseOpcode,
                                       int offset)
  {
    return baseOpcode | (offset & 0x1ffff) | ((offset & 0x3e0000) << 3);
  }

  protected int constructOpcodeK4(int baseOpcode,
                                  int k4)
  {
    if ((baseOpcode & ~Instruction_K4.OPCODE_MASK) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (k4 < 0 || k4 > 15) {
      throw new IllegalArgumentException("invalid k4");
    }
    return baseOpcode | (k4 << 4);
  }

  protected int constructOpcodeRdlK6(int baseOpcode,
                                     int rdl,
                                     int k6)
  {
    if ((baseOpcode & ~0xff00) != 0) {
      throw new IllegalArgumentException("invalid base opcode");
    }
    if (rdl < 24 || rdl > 30 || ((rdl % 2) != 0)) {
      throw new IllegalArgumentException("invalid rdl");
    }
    if (k6 < 0 || k6 > 63) {
      throw new IllegalArgumentException("invalid k6");
    }
    return baseOpcode | ((rdl - 24) << 3) | ((k6 & 0x30) << 2) | (k6 & 0xf);
  }

  protected int constructOpcodeLd(Pointer ptr,
                                  int rd,
                                  Ld.Mode mode,
                                  int displacement)
  {
    if (rd > 31 || rd < 0) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    int result = 0;
    switch (ptr) {
      case X: {
        result = 0x900c;
      }
      break;
      case Y: {
        result = 0x8008;
      }
      break;
      case Z: {
        result = 0x8000;
      }
    }
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

  protected int constructOpcodeBranch(int baseOpcode,
                                      int bit,
                                      int offset)
  {
    if ((baseOpcode & ~0xfc00) != 0) {
      throw new IllegalArgumentException("invalid opcode");
    }
    if (bit < 0 || bit > 7) {
      throw new IllegalArgumentException("invalid bit");
    }
    if (offset < -64 || offset > 63) {
      throw new IllegalArgumentException("invalid offset");
    }
    return baseOpcode | bit | ((offset & 0x7f) << 3);
  }

  protected int constructOpcodeRdP(int baseOpcode,
                                   int rd,
                                   int io)
  {
    if ((baseOpcode & ~0xf800) != 0) {
      throw new IllegalArgumentException("invalid opcode");
    }
    if (rd < 0 || rd > 31) {
      throw new IllegalArgumentException("invalid rd");
    }
    if (io < 0 || io > 63) {
      throw new IllegalArgumentException("invalid io");
    }
    return baseOpcode | (rd << 4) | (io & 0xf) | ((io & 0x30) << 5);
  }

  protected int constructOpcodePb(int opcode,
                                  int io,
                                  int b)
  {
    if ((opcode & ~0xff00) != 0) {
      throw new IllegalArgumentException("invalid opcode");
    }
    if (io < 0 || io > 31) {
      throw new IllegalArgumentException("invalid io");
    }
    if (b < 0 || b > 7) {
      throw new IllegalArgumentException("invalid bit");
    }
    return opcode | b | (io << 3);
  }

  protected int constructOpcodeRdb(int opcode,
                                   int rd,
                                   int b)
  {
    if ((opcode & ~0xfe08) != 0) {
      throw new IllegalArgumentException("invalid rd,b opcode" + Integer.toHexString(opcode));
    }
    if (rd > 31 || rd < 0) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    if (b > 7 || b < 0) {
      throw new IllegalArgumentException("invalid bit index " + b);
    }
    return opcode | (rd << 4) | (b);
  }

  protected int constructOpcodeRdK8(int opcode,
                                    int rd,
                                    int k8)
  {
    if ((opcode & ~0xf000) != 0) {
      throw new IllegalArgumentException("invalid rd,rr opcode" + Integer.toHexString(opcode));
    }
    if (rd > 31 || rd < 16) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    if (k8 < 0 || k8 > 0xff) {
      throw new IllegalArgumentException("invalid k8 " + k8);
    }
    return opcode | ((k8 & 0xf0) << 4) | (k8 & 0xf) | ((rd - 16) << 4);
  }

  protected int constructOpcodeRdRr(int opcode,
                                    int rd,
                                    int rr)
  {
    if ((opcode & ~0xfc00) != 0) {
      throw new IllegalArgumentException("invalid rd,rr opcode" + Integer.toHexString(opcode));
    }
    if (rd > 31 || rd < 0) {
      throw new IllegalArgumentException("invalid register r" + rd);
    }
    if (rr > 31 || rr < 0) {
      throw new IllegalArgumentException("invalid register r" + rr);
    }
    return opcode | (rd << 4) | ((rr & 0x10) << 5) | (rr & 0xf);
  }

}
