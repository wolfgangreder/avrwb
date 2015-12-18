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
public class JmpNGTest extends AbstractInstructionTest
{

  public JmpNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0x1f0, 0, false, 0x0ff0, 3},
      {0xff0, 0, false, 0x01f0, 3},
      {0x1f0, 0xff, false, 0x0ff0, 3},
      {0xff0, 0xff, false, 0x01f0, 3}
    };
  }

  @Test(dataProvider = "Provider")
  public void testJmp(int ip,
                      int sregInit,
                      boolean list,
                      int targetAddress,
                      int expectedCycles) throws Exception
  {
    final String cmd = "nop\n.org 0x" + Integer.toHexString(ip) + "\njmp label\nnop\n.org 0x" + Integer.
            toHexString(targetAddress) + "\nlabel:\nnop";
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();

    cpu.setIP(device,
              ip);
    sreg.setValue(sregInit);
    device.getSRAM().addMemoryChangeListener(new MemoryChangeHandler(device.getSRAM(),
                                                                     expectedChange,
                                                                     cmd)::onMemoryChanged);

    for (int i = 0; i < ((expectedCycles - 1) * 2) + 1; ++i) {
      device.onClock(cs.getAndNext());
    }
    device.onClock(cs.getAndNext());
    assertSREG(sreg.getValue(),
               sregInit,
               cmd);
    assertEquals(cpu.getIP(),
                 targetAddress,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
