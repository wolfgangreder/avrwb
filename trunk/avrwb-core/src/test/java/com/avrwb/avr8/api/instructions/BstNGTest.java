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
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class BstNGTest extends AbstractInstructionTest
{

  public BstNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0, 0, 0, 0},
      {1, 0, 0, 0xff, (~SREG.MASK_T) & 0xff},
      {2, 1, 0, 0, SREG.MASK_T},
      {3, 0xfe, 0, 0xff, (~SREG.MASK_T) & 0xff},
      {4, 0, 1, 0, 0},
      {5, 0, 1, 0xff, (~SREG.MASK_T) & 0xff},
      {6, 2, 1, 0, SREG.MASK_T},
      {7, 0xfc, 1, 0xff, (~SREG.MASK_T) & 0xff},
      {8, 0, 2, 0, 0},
      {9, 0, 2, 0xff, (~SREG.MASK_T) & 0xff},
      {10, 4, 2, 0, SREG.MASK_T},
      {11, 0xfb, 2, 0xff, (~SREG.MASK_T) & 0xff},
      {12, 0, 3, 0, 0},
      {13, 0, 3, 0xff, (~SREG.MASK_T) & 0xff},
      {14, 8, 3, 0, SREG.MASK_T},
      {15, 0xf7, 3, 0xff, (~SREG.MASK_T) & 0xff},
      {16, 0, 4, 0, 0},
      {17, 0, 4, 0xff, (~SREG.MASK_T) & 0xff},
      {18, 0x10, 4, 0, SREG.MASK_T},
      {19, 0xef, 4, 0xff, (~SREG.MASK_T) & 0xff},
      {20, 0, 5, 0, 0},
      {21, 0, 5, 0xff, (~SREG.MASK_T) & 0xff},
      {22, 0x20, 5, 0, SREG.MASK_T},
      {23, 0xcf, 5, 0xff, (~SREG.MASK_T) & 0xff},
      {24, 0, 6, 0, 0},
      {25, 0, 6, 0xff, (~SREG.MASK_T) & 0xff},
      {26, 0x40, 6, 0, SREG.MASK_T},
      {27, 0xbf, 6, 0xff, (~SREG.MASK_T) & 0xff},
      {28, 0, 7, 0, 0},
      {29, 0, 7, 0xff, (~SREG.MASK_T) & 0xff},
      {30, 0x80, 7, 0, SREG.MASK_T},
      {31, 0x7f, 7, 0xff, (~SREG.MASK_T) & 0xff}
    };
  }

  @Test(dataProvider = "Provider")
  public void testBst(int rd,
                      int rdVal,
                      int b,
                      int sregInit,
                      int sregExpected) throws Exception
  {
    final String cmd = "bst r" + rd + "," + b + "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();
    final CPU cpu = device.getCPU();
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
    if (sregExpected != sregInit) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 rdVal,
                 cmd);
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
  }

}
