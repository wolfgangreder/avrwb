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
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.SREG;
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
public class DecNGTest extends AbstractInstructionTest
{

  public DecNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0, 0, false, 0xff, SREG.MASK_N | SREG.MASK_S},
      {1, 1, 0, false, 0, SREG.MASK_Z},
      {2, 0x80, 0, false, 0x7f, SREG.MASK_V | SREG.MASK_S}
    };
  }

  @Test(dataProvider = "Provider")
  public void testDec(int rd,
                      int rdVal,
                      int sregInit,
                      boolean list,
                      int rdExptected,
                      int sregExpected) throws Exception
  {
    final String cmd = "dec r" + rd + "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();

    sram.setByteAt(rd,
                   rdVal);
    sreg.setValue(sregInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    controller.stepCPU();
    if (sregExpected != sregInit) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    expectedChange.add(rd);
    controller.stepCPU();
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertEquals(sram.getByteAt(rd),
                 rdExptected,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
