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
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class SbiwNGTest extends AbstractInstructionTest
{

  public SbiwNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {24, 0, 1, 0, false, -1, SREG.MASK_C | SREG.MASK_N | SREG.MASK_S},
      {26, 1234, 2, SREG.MASK_I | SREG.MASK_T | SREG.MASK_V, false, 1232, SREG.MASK_I | SREG.MASK_T},
      {28, 0x8000, 1, SREG.MASK_Z, false, 0x7fff, SREG.MASK_V | SREG.MASK_S}
    };
  }

  @Test(dataProvider = "Provider")
  public void testSbiw(int rd,
                       int rdVal,
                       int k7,
                       int sregInit,
                       boolean list,
                       int rdExpected,
                       int sregExpected) throws Exception
  {
    final String cmd = "sbiw r" + (rd + 1) + ":r" + rd + "," + k7 + "; r" + rd + "=" + rdVal;
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sreg.setValue(sregInit);
    sram.setWordAt(rd,
                   rdVal);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < 3; ++i) {
      controller.stepCPU();
    }
    if ((rdVal & 0xff) != (rdExpected & 0xff)) {
      expectedChange.add(rd);
    }
    if ((rdVal & 0xff00) != (rdExpected & 0xff00)) {
      expectedChange.add(rd + 1);
    }
    if (sregExpected != sregInit) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    controller.stepCPU();
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertEquals(sram.getWordAt(rd),
                 rdExpected & 0xffff,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
