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
import com.avrwb.avr8.Register;
import com.avrwb.avr8.api.instructions.helper.ClockStateTestImpl;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class Sbic_SbisNGTest extends AbstractInstructionTest
{

  public Sbic_SbisNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0x10, 0x00, 0x00, "nop", true, 0x01, 0x01},
      {0x10, 0x00, 0x01, "nop", true, 0x01, 0x01},
      {0x10, 0x00, 0x02, "nop", true, 0x01, 0x01},
      {0x10, 0x00, 0x03, "nop", true, 0x01, 0x01},
      {0x10, 0x00, 0x04, "nop", true, 0x01, 0x01},
      {0x10, 0x00, 0x05, "nop", true, 0x01, 0x01},
      {0x10, 0x00, 0x06, "nop", true, 0x01, 0x01},
      {0x10, 0x00, 0x07, "nop", true, 0x01, 0x01},
      {0x10, 0x01, 0x00, "nop", true, 0x02, 0x02},
      {0x10, 0x02, 0x01, "nop", true, 0x02, 0x02},
      {0x10, 0x04, 0x02, "nop", true, 0x02, 0x02},
      {0x10, 0x08, 0x03, "nop", true, 0x02, 0x02},
      {0x10, 0x10, 0x04, "nop", true, 0x02, 0x02},
      {0x10, 0x20, 0x05, "nop", true, 0x02, 0x02},
      {0x10, 0x40, 0x06, "nop", true, 0x02, 0x02},
      {0x10, 0x80, 0x07, "nop", true, 0x02, 0x02},
      {0x10, 0x00, 0x00, "call 0x0000", true, 0x01, 0x01},
      {0x10, 0x00, 0x01, "call 0x0000", true, 0x01, 0x01},
      {0x10, 0x00, 0x02, "call 0x0000", true, 0x01, 0x01},
      {0x10, 0x00, 0x03, "call 0x0000", true, 0x01, 0x01},
      {0x10, 0x00, 0x04, "call 0x0000", true, 0x01, 0x01},
      {0x10, 0x00, 0x05, "call 0x0000", true, 0x01, 0x01},
      {0x10, 0x00, 0x06, "call 0x0000", true, 0x01, 0x01},
      {0x10, 0x00, 0x07, "call 0x0000", true, 0x01, 0x01},
      {0x10, 0x01, 0x00, "call 0x0000", true, 0x03, 0x03},
      {0x10, 0x02, 0x01, "call 0x0000", true, 0x03, 0x03},
      {0x10, 0x04, 0x02, "call 0x0000", true, 0x03, 0x03},
      {0x10, 0x08, 0x03, "call 0x0000", true, 0x03, 0x03},
      {0x10, 0x10, 0x04, "call 0x0000", true, 0x03, 0x03},
      {0x10, 0x20, 0x05, "call 0x0000", true, 0x03, 0x03},
      {0x10, 0x40, 0x06, "call 0x0000", true, 0x03, 0x03},
      {0x10, 0x80, 0x07, "call 0x0000", true, 0x03, 0x03}
    };
  }

  @Test(dataProvider = "Provider")
  public void testSbis(int ioPort,
                       int ioPortInit,
                       int bit,
                       String nextCmd,
                       boolean list,
                       int expectedIp,
                       int expectedCycles) throws Exception
  {
    final String cmd = "sbis 0x" + Integer.toHexString(ioPort) + "," + bit + "\n" + nextCmd + "\nnop" + "\n.dw 0xffff";
    final Device device = getDevice(cmd,
                                    list);
    final Register io = device.getIOSpace().get(ioPort);
    final ClockStateTestImpl cs = new ClockStateTestImpl();

    io.setValue(ioPortInit);
    for (int i = 0; i < (expectedCycles * 2); ++i) {
      device.onClock(cs.getAndNext());
    }
    assertEquals(device.getCPU().getIP(),
                 expectedIp,
                 cmd);
  }

}
