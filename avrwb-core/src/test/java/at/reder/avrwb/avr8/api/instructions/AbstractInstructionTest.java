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

import at.reder.avrwb.avr8.api.Instruction;
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

  protected int constructOpcodeRdb(int opcode,
                                   int rd,
                                   int b)
  {
    if ((opcode & ~0xfe08) != 0) {
      return new IllegalArgumentException("invalid rd,b opcode" + Integer.toHexString(opcode));
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
