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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class MovNGTest extends AbstractInstructionTest
{

  public MovNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0, 0, 0},
      {31, 31, 0},
      {0, 31, 0},
      {31, 0, 0},
      {0, 0, 0xff},
      {31, 31, 0xff},
      {0, 31, 0xff},
      {31, 0, 0xff}
    };
  }

  @Test(dataProvider = "Provider")
  public void testMov(int rd,
                      int rr,
                      int sreg) throws Exception
  {
    String cmd = "mov r" + rd + ",r" + rr;
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();
    Set<Integer> expectedChange = new HashSet<>();
    sram.setByteAt(rd,
                   0);
    sram.setByteAt(rr,
                   0xff);
    device.getCPU().getSREG().setValue(sreg);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    controller.stepCPU();
    if (rd != rr) {
      expectedChange.add(rd);
    }
    controller.stepCPU();
    assertTrue(expectedChange.isEmpty(),
               cmd + "|event listener not called");
    assertEquals(sram.getByteAt(rd),
                 0xff,
                 cmd + "|value test");
    assertEquals(sram.getByteAt(rr),
                 0xff,
                 cmd + "|value test");
    assertSREG(device.getCPU().getSREG().getValue(),
               sreg,
               cmd);
    assertEquals(device.getCPU().getIP(),
                 1,
                 cmd);
  }

}
