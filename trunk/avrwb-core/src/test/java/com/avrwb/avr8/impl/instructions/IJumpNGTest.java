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
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class IJumpNGTest extends AbstractInstructionTest
{

  public IJumpNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0x1f0, 0x1, 0x0, false, 2},
      {0xff0, 0x0, 0x1, false, 2},
      {0x1f0, 0x2, 0x2, false, 2},
      {0xff0, 0xf0, 0xf, false, 2}
    };
  }

  @Test(dataProvider = "Provider")
  public void test(int ip,
                   int r30,
                   int r31,
                   boolean list,
                   int expectedCycles) throws Exception
  {
    final String cmd;
    final int expectedIp = ((r31 & 0xff) << 8) + (r30 & 0xff);
    if (expectedIp != ip) {
      cmd = "nop\n.org 0x" + Integer.toHexString(ip) + "\nijmp\n.dw 0xffff\n.org 0x" + Integer.toHexString(expectedIp);
    } else {
      cmd = "nop\n.org 0x" + Integer.toHexString(ip) + "\nijmp\n.dw 0xffff";
    }
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final Register sp = cpu.getStackPointer();
    final SRAM sram = device.getSRAM();
    final int spInit = sram.getSize() - 1;
    final Set<Integer> expectedChange = new HashSet<>();

    cpu.setIP(context,
              ip);
    sp.setValue(spInit);

    sram.setPointer(Pointer.Z,
                    expectedIp);

    assertEquals(sram.getByteAt(30),
                 r30,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 r31,
                 cmd);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((expectedCycles - 1) * 2) + 1; ++i) {
      controller.stepCPU();
    }
    controller.stepCPU();
    assertEquals(cpu.getIP(),
                 expectedIp,
                 cmd);
    assertEquals(sp.getValue(),
                 spInit,
                 cmd);
    assertEquals(sram.getByteAt(30),
                 r30,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 r31,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
