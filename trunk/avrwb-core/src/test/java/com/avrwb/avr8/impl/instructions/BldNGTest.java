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
public class BldNGTest extends AbstractInstructionTest
{

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0, 0, 0, 0},
      {1, 0, 0, SREG.MASK_T, 1},
      {2, 0, 1, ~SREG.MASK_T, 0},
      {3, 0, 1, SREG.MASK_T, 2},
      {4, 0xff, 2, SREG.MASK_T, 0xff},
      {5, 0xff, 2, ~SREG.MASK_T, 0xfb},
      {6, 0, 3, SREG.MASK_T, 0x08},
      {7, 0, 4, SREG.MASK_T, 0x10},
      {8, 0, 5, SREG.MASK_T, 0x20},
      {9, 0, 6, SREG.MASK_T, 0x40},
      {10, 0, 7, SREG.MASK_T, 0x80}
    };
  }

  @Test(dataProvider = "Provider")
  public void testBld(int rd,
                      int rdVal,
                      int b,
                      int sregInit,
                      int rdExpected) throws Exception
  {
    final String cmd = "bld r" + rd + "," + b + "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();
    final SREG sreg = device.getCPU().getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sram.setByteAt(rd,
                   rdVal);
    sreg.setValue(sregInit & 0xff);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    controller.stepCPU();
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertSREG(sreg.getValue(),
               sregInit & 0xff,
               cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
    assertEquals(device.getCPU().getIP(),
                 1,
                 cmd);
  }

}
