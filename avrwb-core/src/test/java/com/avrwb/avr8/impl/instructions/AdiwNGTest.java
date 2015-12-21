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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class AdiwNGTest extends AbstractInstructionTest
{

  public AdiwNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {24, 0, 0, 0, 0, SREG.MASK_Z},
      {26, 0xfff0, 0x3f, SREG.MASK_I, (0xfff0 + 0x3f) & 0xffff, SREG.MASK_C | SREG.MASK_I},
      {28, 25, 47, SREG.MASK_C | SREG.MASK_Z, 72, 0},
      {30, 0x7ffe, 0x10, 0, 0x800e, SREG.MASK_N | SREG.MASK_V},
      {24, 0xffff, 1, 0, 0, SREG.MASK_Z | SREG.MASK_C}
    };
  }

  @Test(dataProvider = "Provider")
  public void testAdiw(int rd,
                       int rdVal,
                       int k6,
                       int sregInit,
                       int expectedRd,
                       int expectedSREG) throws Exception
  {
    final String cmd = "adiw r" + (rd + 1) + ":r" + rd + ",0x" + Integer.toHexString(k6);
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();
    final SREG sreg = device.getCPU().getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sreg.setValue(sregInit);
    sram.setWordAt(rd,
                   rdVal);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    controller.stepCPU();
    controller.stepCPU();
    controller.stepCPU();
    if ((rdVal & 0xff) != (expectedRd & 0xff)) {
      expectedChange.add(rd);
    }
    if ((rdVal & 0xff00) != (expectedRd & 0xff00)) {
      expectedChange.add(rd + 1);
    }
    if (sregInit != expectedSREG) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    controller.stepCPU();
    assertEquals(sram.getWordAt(rd),
                 expectedRd,
                 cmd);
    assertSREG(sreg.getValue(),
               expectedSREG,
               cmd);
    assertTrue(expectedChange.isEmpty());
    assertEquals(device.getCPU().getIP(),
                 1,
                 cmd);
  }

}
