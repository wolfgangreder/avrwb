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

import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.instructions.helper.ClockStateTestImpl;
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
public class CpiNGTest extends AbstractInstructionTest
{

  public CpiNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {16, 0, 0, 0, false, SREG.MASK_Z},
      {17, 0x55, 0x55, 0, false, SREG.MASK_Z},
      {18, 1, 2, SREG.MASK_I | SREG.MASK_T, false, SREG.MASK_S | SREG.MASK_I | SREG.MASK_T | SREG.MASK_N | SREG.MASK_H
                                                   | SREG.MASK_C},
      {19, 0x0b, 0x80, 0, false, SREG.MASK_C | SREG.MASK_V | SREG.MASK_N},
      {20, 0xc0, 0x7f, 0, false, SREG.MASK_V | SREG.MASK_H | SREG.MASK_S},
      {21, 0xc7, 0x7f, SREG.MASK_C | SREG.MASK_N, false, SREG.MASK_V | SREG.MASK_H | SREG.MASK_S},};
  }

  @Test(dataProvider = "Provider")
  public void testCpi(int rd,
                      int rdVal,
                      int k8,
                      int sregInit,
                      boolean list,
                      int sregExpected) throws Exception
  {
    final String cmd = "cpi r" + rd + ",0x" + Integer.toHexString(k8) + "; r" + rd + "=0x" + Integer.toHexString(rdVal)
                               + "diff=0x" + Integer.toBinaryString((rdVal - k8) & 0x1ff);
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();

    sram.setByteAt(rd,
                   rdVal);
    sreg.setValue(sregInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    device.onClock(cs.getAndNext());
    if (sregInit != sregExpected) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    device.onClock(cs.getAndNext());
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertEquals(sram.getByteAt(rd),
                 rdVal,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
