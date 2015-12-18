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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class MovwNGTest extends AbstractInstructionTest
{

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0xffff, 30, 0x1234, false},
      {2, 0xffff, 28, 0x5678, false},
      {4, 0x0000, 26, 0x9abc, false},
      {6, 0x0000, 24, 0x9abc, false},
      {8, 0x0000, 22, 0x0000, false}
    };
  }

  @Test(dataProvider = "Provider")
  public void testMovw(int rd,
                       int rdVal,
                       int rr,
                       int rrVal,
                       boolean list) throws Exception
  {
    final String cmd = "movw r" + (rd + 1) + ":r" + rd + ",r" + (rr + 1) + ":r" + rr + "; r" + rd + "=0x" + Integer.toHexString(
            rdVal) + ", r" + rr + "=0x" + Integer.
            toHexString(rrVal);
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final int sregVal = sreg.getValue();

    sram.setWordAt(rd,
                   rdVal);
    sram.setWordAt(rr,
                   rrVal);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    device.onClock(cs.getAndNext());
    if (rdVal != rrVal) {
      expectedChange.add(rd);
      expectedChange.add(rd + 1);
    }
    device.onClock(cs.getAndNext());
    assertSREG(sreg.getValue(),
               sregVal,
               cmd);
    assertEquals(sram.getWordAt(rd),
                 rrVal,
                 cmd);
    assertEquals(sram.getWordAt(rr),
                 rrVal,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
