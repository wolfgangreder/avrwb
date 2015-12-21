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

import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.SREG;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class BranchInstructionNGTest extends AbstractInstructionTest
{

  @Test
  public void testDecodeBranchOffset()
  {
    final int opcodemask = 0xfc07;
    int opcode = opcodemask;
    int expected = 0;
    int result = Brbs_Brbc.decodeOffset(opcode);
    assertEquals(result,
                 expected,
                 "opcode " + Integer.toHexString(opcode));
    opcode = opcodemask | 0x3f8;
    expected = -1;
    result = Brbs_Brbc.decodeOffset(opcode);
    assertEquals(result,
                 expected,
                 "opcode " + Integer.toHexString(opcode)
    );
    opcode = opcodemask | 0x1f8;
    expected = 63;
    result = Brbs_Brbc.decodeOffset(opcode);
    assertEquals(result,
                 expected,
                 "opcode " + Integer.toHexString(opcode));
    opcode = opcodemask | 0x200;
    expected = -64;
    result = Brbs_Brbc.decodeOffset(opcode);
    assertEquals(result,
                 expected,
                 "opcode " + Integer.toHexString(opcode));
  }

  @DataProvider(name = "Provider_BRBC")
  public Object[][] getDataBRBC()
  {
    return new Object[][]{
      {SREG.C, 0, 100, 100, false, 100, 2},
      {SREG.C, SREG.MASK_C, 100, 110, false, 101, 1},
      {SREG.C, 0, 100, 100 - 63, false, 100 - 63, 2},
      {SREG.C, SREG.MASK_C, 100, 100 - 63, false, 101, 1},
      {SREG.C, 0, 100, 100 + 64, false, 100 + 64, 2},
      {SREG.C, SREG.MASK_C, 100, 100 + 64, false, 101, 1},
      {SREG.H, 0, 100, 100 - 20, false, 100 - 20, 2},
      {SREG.H, SREG.MASK_H, 100, 100 - 20, false, 101, 1},
      {SREG.I, 0, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.I, SREG.MASK_I, 100, 100 - 20, false, 101, 1},
      {SREG.N, 0, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.N, SREG.MASK_N, 100, 100 - 20, false, 101, 1},
      {SREG.S, 0, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.S, SREG.MASK_S, 100, 100 - 20, false, 101, 1},
      {SREG.T, 0, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.T, SREG.MASK_T, 100, 100 - 20, false, 101, 1},
      {SREG.V, 0, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.V, SREG.MASK_V, 100, 100 - 20, false, 101, 1},
      {SREG.Z, 0, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.Z, SREG.MASK_Z, 100, 100 - 20, false, 101, 1}
    };
  }

  @Test(dataProvider = "Provider_BRBC")
  public void testBRBC(int b,
                       int sregInit,
                       int ip,
                       int branchIp,
                       boolean list,
                       int expectedIp,
                       int numCycles) throws Exception
  {
    final String cmd;
    if (ip != branchIp) {
      cmd = "nop\n.org " + ip + "\nbrbc " + b + ",label\nnop\nnop\n.org " + branchIp + "\nlabel:\nnop";
    } else {
      cmd = "nop\n.org " + ip + "\nlabel:\nbrbc " + b + ",label\nnop\nnop";
    }
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sreg.setValue(sregInit);
    cpu.setIP(context,
              ip);
    device.getSRAM().addMemoryChangeListener(new MemoryChangeHandler(device.getSRAM(),
                                                                     expectedChange,
                                                                     cmd)::onMemoryChanged);
    for (int i = 0; i < numCycles * 2; ++i) {
      controller.stepCPU();
    }
    assertSREG(sreg.getValue(),
               sregInit,
               cmd);
    assertEquals(cpu.getIP(),
                 expectedIp,
                 cmd);
  }

  @DataProvider(name = "Provider_BRBS")
  public Object[][] getDataBRBS()
  {
    return new Object[][]{
      {SREG.C, SREG.MASK_C, 100, 100, false, 100, 2},
      {SREG.C, 0, 100, 110, false, 101, 1},
      {SREG.C, SREG.MASK_C, 100, 100 - 63, false, 100 - 63, 2},
      {SREG.C, 0, 100, 100 - 63, false, 101, 1},
      {SREG.C, SREG.MASK_C, 100, 100 + 64, false, 100 + 64, 2},
      {SREG.C, 0, 100, 100 + 64, false, 101, 1},
      {SREG.H, SREG.MASK_H, 100, 100 - 20, false, 100 - 20, 2},
      {SREG.H, 0, 100, 100 - 20, false, 101, 1},
      {SREG.I, SREG.MASK_I, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.I, 0, 100, 100 - 20, false, 101, 1},
      {SREG.N, SREG.MASK_N, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.N, 0, 100, 100 - 20, false, 101, 1},
      {SREG.S, SREG.MASK_S, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.S, 0, 100, 100 - 20, false, 101, 1},
      {SREG.T, SREG.MASK_T, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.T, 0, 100, 100 - 20, false, 101, 1},
      {SREG.V, SREG.MASK_V, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.V, 0, 100, 100 - 20, false, 101, 1},
      {SREG.Z, SREG.MASK_Z, 100, 100 + 20, false, 100 + 20, 2},
      {SREG.Z, 0, 100, 100 - 20, false, 101, 1}
    };
  }

  @Test(dataProvider = "Provider_BRBS")
  public void testBRBS(int b,
                       int sregInit,
                       int ip,
                       int branchIp,
                       boolean list,
                       int expectedIp,
                       int numCycles) throws Exception
  {
    final String cmd;
    if (ip != branchIp) {
      cmd = "nop\n.org " + ip + "\nbrbs " + b + ",label\nnop\nnop\n.org " + branchIp + "\nlabel:\nnop";
    } else {
      cmd = "nop\n.org " + ip + "\nlabel:\nbrbs " + b + ",label\nnop\nnop";
    }
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sreg.setValue(sregInit);
    cpu.setIP(context,
              ip);
    device.getSRAM().addMemoryChangeListener(new MemoryChangeHandler(device.getSRAM(),
                                                                     expectedChange,
                                                                     cmd)::onMemoryChanged);
    for (int i = 0; i < numCycles * 2; ++i) {
      controller.stepCPU();
    }
    assertSREG(sreg.getValue(),
               sregInit,
               cmd);
    assertEquals(cpu.getIP(),
                 expectedIp,
                 cmd);
  }

}
