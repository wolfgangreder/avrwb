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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class SleepNGTest extends AbstractInstructionTest
{

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0xff},
      {0}
    };
  }

  @Test(dataProvider = "Provider")
  public void testSleep(int sregInit) throws Exception
  {
    final String cmd = "sleep";
    final Device device = getDevice(cmd);
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();

    sreg.setValue(sregInit);
    device.getSRAM().addMemoryChangeListener(new AbstractInstructionTest.MemoryChangeHandler(device.getSRAM(),
                                                                                             expectedChange,
                                                                                             cmd)::onMemoryChanged);

    device.onClock(cs.getAndNext());
    device.onClock(cs.getAndNext());
    assertSREG(sreg.getValue(),
               sregInit,
               cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
  }

}
