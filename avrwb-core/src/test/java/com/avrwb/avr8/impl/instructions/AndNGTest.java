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
public class AndNGTest extends AbstractInstructionTest
{

  public AndNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0, 1, 0xff, SREG.MASK_V, 0, SREG.MASK_Z},
      {2, 0xff, 3, 0x0, SREG.MASK_V, 0, SREG.MASK_Z},
      {4, 0xff, 5, 0x55, SREG.MASK_V, 0x55, 0},
      {6, 0xff, 7, 0xaa, SREG.MASK_V, 0xaa, SREG.MASK_N | SREG.MASK_S}
    };
  }

  @Test(dataProvider = "Provider")
  public void testAnd(int rd,
                      int rdVal,
                      int rr,
                      int rrVal,
                      int sregInit,
                      int rdExpected,
                      int sregExpected) throws Exception
  {
    final String cmd = "and r" + rd + ",r" + rr + ";r" + rd + "=0x" + Integer.toHexString(rdVal) + ",r" + rr + "=0x" + Integer.
            toHexString(rrVal);
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();
    final SREG sreg = device.getCPU().getSREG();
    final Set<Integer> expectedToChanged = new HashSet<>();

    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(rr,
                   rrVal);
    sreg.setValue(sregInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedToChanged,
                                                         cmd)::onMemoryChanged);

    controller.stepCPU();
    if (rdVal != rdExpected) {
      expectedToChanged.add(rd);
    }
    if (sregInit != sregExpected) {
      expectedToChanged.add(sreg.getMemoryAddress());
    }
    controller.stepCPU();

    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertTrue(expectedToChanged.isEmpty(),
               cmd);
    assertEquals(device.getCPU().getIP(),
                 1,
                 cmd);
  }

}
