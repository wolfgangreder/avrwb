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
public class SbciNGTest extends AbstractInstructionTest
{

  public SbciNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {16, 0x00, 0x00, 0, false, 0, 0},
      {17, 0x00, 0x00, SREG.MASK_C, false, SREG.MASK_C | SREG.MASK_N | SREG.MASK_S | SREG.MASK_H, 0xff},
      {18, 0x00, 0x00, SREG.MASK_C | SREG.MASK_Z, false, SREG.MASK_C | SREG.MASK_N | SREG.MASK_S | SREG.MASK_H, 0xff},
      {19, 0xff, 0x00, 0, false, SREG.MASK_N | SREG.MASK_S, 0xff},
      {20, 0x01, 0x01, SREG.MASK_Z, false, SREG.MASK_Z, 0x00},
      {21, 0x0e, 1, SREG.MASK_C | SREG.MASK_Z, false, 0, 0x0c},
      {22, 0x80, 1, 0, false, SREG.MASK_H | SREG.MASK_V | SREG.MASK_S, 0x7f}
    };
  }

  @Test(dataProvider = "Provider")
  public void testSbci(int rd,
                       int rdVal,
                       int k8,
                       int sregInit,
                       boolean list,
                       int sregExpected,
                       int rdExpected) throws Exception
  {
    final String cmd = "sbci r" + rd + ",0x" + Integer.toHexString(k8);
    final Device device = getDevice(cmd,
                                    list);
    final SRAM sram = device.getSRAM();
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sreg.setValue(sregInit);
    sram.setByteAt(rd,
                   rdVal);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    controller.stepCPU();
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    if (sregInit != sregExpected) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
  }

}
