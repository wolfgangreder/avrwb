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
import com.avrwb.avr8.impl.instructions.helper.ClockStateTestImpl;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class ComNGTest extends AbstractInstructionTest
{

  public ComNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0, 0, false, 0xff, SREG.MASK_C | SREG.MASK_N | SREG.MASK_S},
      {1, 0, 0xff, false, 0xff, SREG.MASK_H | SREG.MASK_I | SREG.MASK_T | SREG.MASK_C | SREG.MASK_N | SREG.MASK_S},
      {2, 0xff, 0, false, 0, SREG.MASK_C | SREG.MASK_Z},
      {3, 0xff, 0xff, false, 0, SREG.MASK_H | SREG.MASK_I | SREG.MASK_T | SREG.MASK_C | SREG.MASK_Z},
      {4, 0x55, SREG.MASK_V | SREG.MASK_C | SREG.MASK_Z, false, 0xaa, SREG.MASK_C | SREG.MASK_N | SREG.MASK_S}
    };
  }

  @Test(dataProvider = "Provider")
  public void testCom(int rd,
                      int rdVal,
                      int sregInit,
                      boolean list,
                      int rdExpected,
                      int sregExpected) throws Exception
  {
    final String cmd = "com r" + rd + "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    final SRAM sram = device.getSRAM();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Set<Integer> expectedChange = new HashSet<>();

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
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    device.onClock(cs.getAndNext());
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
