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
public class CpseNGTest extends AbstractInstructionTest
{

  public CpseNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0, 1, 0, 0, "nop", false, 2, 2},
      {0, 0, 2, 0, 0, "call 0", false, 3, 3},
      {0, 23, 3, 23, 0, "nop", false, 2, 2},
      {0, 0xff, 4, 0xff, 0, "call 0", false, 3, 3},
      //
      {0, 0, 5, 0, 0xff, "nop", false, 2, 2},
      {0, 0, 6, 0, 0xff, "call 0", false, 3, 3},
      {0, 23, 7, 23, 0xff, "nop", false, 2, 2},
      {0, 0xff, 8, 0xff, 0xff, "call 0", false, 3, 3},
      //
      {0, 0, 9, 1, 0, "nop", false, 1, 1},
      {0, 0, 10, 2, 0, "call 0", false, 1, 1},
      {0, 23, 11, 21, 0, "nop", false, 1, 1},
      {0, 0xff, 12, 0xf0, 0, "call 0", false, 1, 1},
      //
      {0, 0, 13, 1, 0xff, "nop", false, 1, 1},
      {0, 0, 14, 2, 0xff, "call 0", false, 1, 1},
      {0, 23, 15, 22, 0xff, "nop", false, 1, 1},
      {0, 0xff, 16, 0x0f, 0xff, "call 0", false, 1, 1}
    };
  }

  @Test(dataProvider = "Provider")
  public void testCpse(int rd,
                       int rdVal,
                       int rr,
                       int rrVal,
                       int sregInit,
                       String nextCmd,
                       boolean list,
                       int ipExpected,
                       int cyclesExpected) throws Exception
  {
    final String cmd = "cpse r" + rd + ",r" + rr + "\n" + nextCmd + "; r" + rd + "=" + rdVal + ", r" + rr + "=" + rrVal;
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();

    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(rr,
                   rrVal);
    sreg.setValue(sregInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < (cyclesExpected * 2); ++i) {
      device.onClock(cs.getAndNext());
    }
    assertSREG(sreg.getValue(),
               sregInit,
               cmd);
    assertEquals(sram.getByteAt(rd),
                 rdVal,
                 cmd);
    assertEquals(sram.getByteAt(rr),
                 rrVal,
                 cmd);
    assertEquals(cpu.getIP(),
                 ipExpected,
                 cmd);
  }

}
