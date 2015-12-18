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
import com.avrwb.avr8.Register;
import com.avrwb.avr8.impl.instructions.helper.ClockStateTestImpl;
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

  @DataProvider(name = "ProviderSbis")
  public Object[][] getDataSbis()
  {
    return new Object[][]{
      {0x10, 0x00, 0x00, "nop", false, 0x01, 0x01},
      {0x10, 0x00, 0x01, "nop", false, 0x01, 0x01},
      {0x10, 0x00, 0x02, "nop", false, 0x01, 0x01},
      {0x10, 0x00, 0x03, "nop", false, 0x01, 0x01},
      {0x10, 0x00, 0x04, "nop", false, 0x01, 0x01},
      {0x10, 0x00, 0x05, "nop", false, 0x01, 0x01},
      {0x10, 0x00, 0x06, "nop", false, 0x01, 0x01},
      {0x10, 0x00, 0x07, "nop", false, 0x01, 0x01},
      {0x10, 0x01, 0x00, "nop", false, 0x02, 0x02},
      {0x10, 0x02, 0x01, "nop", false, 0x02, 0x02},
      {0x10, 0x04, 0x02, "nop", false, 0x02, 0x02},
      {0x10, 0x08, 0x03, "nop", false, 0x02, 0x02},
      {0x10, 0x10, 0x04, "nop", false, 0x02, 0x02},
      {0x10, 0x20, 0x05, "nop", false, 0x02, 0x02},
      {0x10, 0x40, 0x06, "nop", false, 0x02, 0x02},
      {0x10, 0x80, 0x07, "nop", false, 0x02, 0x02},
      {0x10, 0x00, 0x00, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x00, 0x01, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x00, 0x02, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x00, 0x03, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x00, 0x04, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x00, 0x05, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x00, 0x06, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x00, 0x07, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x01, 0x00, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x02, 0x01, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x04, 0x02, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x08, 0x03, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x10, 0x04, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x20, 0x05, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x40, 0x06, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x80, 0x07, "call 0x0000", false, 0x03, 0x03}
    };
  }

  @Test(dataProvider = "ProviderSbis")
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

  @DataProvider(name = "ProviderSbic")
  public Object[][] getDataSbic()
  {
    return new Object[][]{
      {0x10, 0x00, 0x00, "nop", false, 0x02, 0x02},
      {0x10, 0x00, 0x01, "nop", false, 0x02, 0x02},
      {0x10, 0x00, 0x02, "nop", false, 0x02, 0x02},
      {0x10, 0x00, 0x03, "nop", false, 0x02, 0x02},
      {0x10, 0x00, 0x04, "nop", false, 0x02, 0x02},
      {0x10, 0x00, 0x05, "nop", false, 0x02, 0x02},
      {0x10, 0x00, 0x06, "nop", false, 0x02, 0x02},
      {0x10, 0x00, 0x07, "nop", false, 0x02, 0x02},
      {0x10, 0x01, 0x00, "nop", false, 0x01, 0x01},
      {0x10, 0x02, 0x01, "nop", false, 0x01, 0x01},
      {0x10, 0x04, 0x02, "nop", false, 0x01, 0x01},
      {0x10, 0x08, 0x03, "nop", false, 0x01, 0x01},
      {0x10, 0x10, 0x04, "nop", false, 0x01, 0x01},
      {0x10, 0x20, 0x05, "nop", false, 0x01, 0x01},
      {0x10, 0x40, 0x06, "nop", false, 0x01, 0x01},
      {0x10, 0x80, 0x07, "nop", false, 0x01, 0x01},
      {0x10, 0x00, 0x00, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x00, 0x01, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x00, 0x02, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x00, 0x03, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x00, 0x04, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x00, 0x05, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x00, 0x06, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x00, 0x07, "call 0x0000", false, 0x03, 0x03},
      {0x10, 0x01, 0x00, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x02, 0x01, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x04, 0x02, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x08, 0x03, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x10, 0x04, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x20, 0x05, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x40, 0x06, "call 0x0000", false, 0x01, 0x01},
      {0x10, 0x80, 0x07, "call 0x0000", false, 0x01, 0x01}
    };
  }

  @Test(dataProvider = "ProviderSbic")
  public void testSbic(int ioPort,
                       int ioPortInit,
                       int bit,
                       String nextCmd,
                       boolean list,
                       int expectedIp,
                       int expectedCycles) throws Exception
  {
    final String cmd = "sbic 0x" + Integer.toHexString(ioPort) + "," + bit + "\n" + nextCmd + "\nnop" + "\n.dw 0xffff";
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
