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
public class AndiNGTest extends AbstractInstructionTest
{

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {16, 0, 0xff, SREG.MASK_V, 0, SREG.MASK_Z},
      {17, 0xff, 0, SREG.MASK_V, 0, SREG.MASK_Z},
      {18, 0xff, 0xaa, SREG.MASK_V, 0xaa, SREG.MASK_N | SREG.MASK_S},
      {19, 0xaa, 0x55, SREG.MASK_V, 0, SREG.MASK_Z}
    };
  }

  @Test(dataProvider = "Provider")
  public void testAndi(int rd,
                       int rdVal,
                       int k8,
                       int sregInit,
                       int rdExpected,
                       int sregExptected) throws Exception
  {
    final String cmd = "andi r" + rd + ",0x" + Integer.toHexString(k8) + "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();
    final SREG sreg = device.getCPU().getSREG();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();

    sram.setByteAt(rd,
                   rdVal);
    sreg.setValue(sregInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    device.onClock(cs.getAndNext());
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    if (sregInit != sregExptected) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertSREG(sreg.getValue(),
               sregExptected,
               cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
    assertEquals(device.getCPU().getIP(),
                 1,
                 cmd);
  }

}
