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
public class LsrNGTest extends AbstractInstructionTest
{

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {1, 0x00, SREG.MASK_C, false, 0, SREG.MASK_Z},
      {2, 0xff, SREG.MASK_I | SREG.MASK_T | SREG.MASK_V, false, 0x7f, SREG.MASK_C | SREG.MASK_V | SREG.MASK_S | SREG.MASK_I
                                                                      | SREG.MASK_T},
      {3, 0x01, 0, false, 0x00, SREG.MASK_C | SREG.MASK_V | SREG.MASK_S | SREG.MASK_Z},
      {4, 0x80, SREG.MASK_V, false, 0x40, 0}
    };
  }

  @Test(dataProvider = "Provider")
  public void testLsr(int rd,
                      int rdVal,
                      int sregInit,
                      boolean list,
                      int rdExpected,
                      int sregExpected) throws Exception
  {
    final String cmd = "lsr r" + rd + "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sreg.setValue(sregInit);
    sram.setByteAt(rd,
                   rdVal);
    sram.addMemoryChangeListener(new AbstractInstructionTest.MemoryChangeHandler(sram,
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
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
