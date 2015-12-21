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
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class SubNGTest extends AbstractInstructionTest
{

  public SubNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0x00, 1, 0, 0, false, SREG.MASK_Z, 0},
      {0, 0x00, 2, 0, SREG.MASK_C, false, SREG.MASK_Z, 0},
      {0, 0xff, 3, 0, SREG.MASK_C, false, SREG.MASK_S | SREG.MASK_N, 0xff},
      {0, 0xff, 4, 0, 0, false, SREG.MASK_N | SREG.MASK_S, 0xff},
      {0, 0x0f, 5, 1, 0, false, 0, 0x0e},
      {0, 0x10, 6, 1, SREG.MASK_C, false, SREG.MASK_H, 0x0f},
      {0, 0x80, 7, 1, SREG.MASK_C, false, SREG.MASK_V | SREG.MASK_S | SREG.MASK_H, 0x7f}
    };
  }

  @Test(dataProvider = "Provider")
  public void testSub(int rd,
                      int rdVal,
                      int rr,
                      int rrVal,
                      int sregInit,
                      boolean list,
                      int sregExpected,
                      int rdExpected) throws Exception
  {
    final String cmd = "sub r" + rd + ",r" + rr;
    final Device device = getDevice(cmd,
                                    list);
    final SRAM sram = device.getSRAM();
    final SREG sreg = device.getCPU().getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sreg.setValue(sregInit);
    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(rr,
                   rrVal);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    controller.stepCPU();
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    if (sregExpected != sregInit) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertEquals(sram.getByteAt(rr),
                 rrVal,
                 cmd);
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
    assertEquals(device.getCPU().getIP(),
                 1,
                 cmd);
  }

}
