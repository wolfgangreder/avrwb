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
public class PopNGTest extends AbstractInstructionTest
{

  public PopNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0x00, 0xff, 0x100, 0x20, false},
      {0x01, 0xff, 0x0ff, 0x3f, false},
      {0x02, 0xff, 0x070, 0x4f, false},
      {0x03, 0xff, 0x110, 0x5f, false}
    };
  }

  @Test(dataProvider = "Provider")
  public void testPop(int rd,
                      int rdVal,
                      int spPtrInit,
                      int spVal,
                      boolean list) throws Exception
  {
    final String cmd = "pop  r" + rd;
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Register sp = cpu.getStackPointer();
    final Set<Integer> expectedChange = new HashSet<>();
    final int newSp = spPtrInit + 1;

    sp.setValue(spPtrInit);
    sram.setByteAt(spPtrInit + 1,
                   spVal);
    sram.setByteAt(rd,
                   rdVal);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < 3; ++i) {
      controller.stepCPU();
    }
    if (rdVal != spVal) {
      expectedChange.add(rd);
    }
    expectedChange.add(sp.getMemoryAddress());
    if (sp.getSize() > 1 && (spPtrInit & 0xff00) != (newSp & 0xff00)) {
      expectedChange.add(sp.getMemoryAddress() + 1);
    }
    if (sp.getSize() > 2 && (spPtrInit & 0xff0000) != (newSp & 0xff0000)) {
      expectedChange.add(sp.getMemoryAddress() + 2);
    }
    if (sp.getSize() > 3 && (spPtrInit & 0xff000000) != (newSp & 0xff000000)) {
      expectedChange.add(sp.getMemoryAddress() + 3);
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(rd),
                 spVal,
                 cmd);
    assertEquals(sp.getValue(),
                 newSp);
    assertEquals(sram.getByteAt(spPtrInit + 1),
                 spVal);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
