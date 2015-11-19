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

import com.avrwb.avr8.api.instructions.BranchInstruction;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class BranchInstructionNGTest extends AbstractInstructionTest
{

  public BranchInstructionNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    testClass(BranchInstruction.class);
  }

  @Test
  public void testDecodeBranchOffset()
  {
    final int opcodemask = 0xfc07;
    int opcode = opcodemask;
    int expected = 0;
    int result = BranchInstruction.decodeOffset(opcode);
    assertEquals("opcode " + Integer.toHexString(opcode),
                 expected,
                 result);
    opcode = opcodemask | 0x3f8;
    expected = -1;
    result = BranchInstruction.decodeOffset(opcode);
    assertEquals("opcode " + Integer.toHexString(opcode),
                 expected,
                 result);
    opcode = opcodemask | 0x1f8;
    expected = 63;
    result = BranchInstruction.decodeOffset(opcode);
    assertEquals("opcode " + Integer.toHexString(opcode),
                 expected,
                 result);
    opcode = opcodemask | 0x200;
    expected = -64;
    result = BranchInstruction.decodeOffset(opcode);
    assertEquals("opcode " + Integer.toHexString(opcode),
                 expected,
                 result);
  }

}
