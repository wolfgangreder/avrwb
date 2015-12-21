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
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class LdiNGTest extends AbstractInstructionTest
{

  public LdiNGTest()
  {
  }

  @DataProvider(name = "Provider")
  Object[][] getData()
  {
    return new Object[][]{
      {16, 0, 0},
      {16, 0xff, 0},
      {31, 0, 0},
      {31, 0xff, 0},
      {16, 0, 0xff},
      {16, 0xff, 0xff},
      {31, 0, 0xff},
      {31, 0xff, 0xff}
    };
  }

  @Test(dataProvider = "Provider")
  public void testLdi(int register,
                      int k8,
                      int sreg) throws Exception
  {
    String cmd = "ldi r" + register + ",0x" + Integer.toHexString(k8);
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();

    Set<Integer> expectedChange = new HashSet<>();
    sram.setByteAt(register,
                   ~k8);
    device.getCPU().getSREG().setValue(sreg);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    controller.stepCPU();
    expectedChange.add(register);
    controller.stepCPU();
    assertTrue(expectedChange.isEmpty(),
               cmd + "|event listener not called");
    assertEquals(sram.getByteAt(register),
                 k8,
                 cmd + "|value test");
    assertSREG(device.getCPU().getSREG().getValue(),
               sreg,
               cmd);
    assertEquals(device.getCPU().getIP(),
                 1,
                 cmd);
  }

}
