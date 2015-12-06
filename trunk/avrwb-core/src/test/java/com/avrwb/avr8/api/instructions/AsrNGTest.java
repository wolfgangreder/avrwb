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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class AsrNGTest extends AbstractInstructionTest
{

  public AsrNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0, 0, 0, SREG.MASK_Z},
      {1, 0x7f, 0, 0x3f, SREG.MASK_C | SREG.MASK_V | SREG.MASK_S},
      {2, 0x01, 0, 0x0, SREG.MASK_C | SREG.MASK_Z | SREG.MASK_V | SREG.MASK_S},
      {3, 0x80, 0, 0xc0, SREG.MASK_N | SREG.MASK_V},
      {4, 0xfe, 0xff, 0xff, SREG.MASK_N | SREG.MASK_V | SREG.MASK_H | SREG.MASK_I | SREG.MASK_T}
    };
  }

  @Test(dataProvider = "Provider")
  public void testAsr(int rd,
                      int rdVal,
                      int sregInit,
                      int rdExpected,
                      int sregExpected) throws Exception
  {
    final String cmd = "asr r" + rd + ";r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();
    final SREG sreg = device.getCPU().getSREG();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Set<Integer> expectedChange = new HashSet<>();

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
    if (sregInit != sregExpected) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
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
