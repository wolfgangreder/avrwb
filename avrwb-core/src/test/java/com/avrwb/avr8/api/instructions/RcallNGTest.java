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

import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.instructions.helper.ClockStateTestImpl;
import com.avrwb.schema.XmlPart;
import com.avrwb.schema.util.DeviceStreamer;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class RcallNGTest extends AbstractInstructionTest
{

  private static XmlPart partmega22;
  private static XmlPart partxmega16;
  private static XmlPart partxmega22;

  public RcallNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    AbstractInstructionTest.setUpClass();
    partmega22 = DeviceStreamer.loadDevice(LdiNGTest.class.getResource("/com/avrwb/devices/ATmega22bitFlash.xml"),
                                           DeviceStreamer.Version.V_1_0);
    assertNotNull(partmega22,
                  "partmega22==null");
    partxmega22 = DeviceStreamer.loadDevice(LdiNGTest.class.getResource("/com/avrwb/devices/ATxmega22bit.xml"),
                                            DeviceStreamer.Version.V_1_0);
    assertNotNull(partxmega22,
                  "partxmega22==null");
    partxmega16 = DeviceStreamer.loadDevice(LdiNGTest.class.getResource("/com/avrwb/devices/ATxmega16bit.xml"),
                                            DeviceStreamer.Version.V_1_0);
    assertNotNull(partxmega16,
                  "partxmega16==null");
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {part, 0x1f0, 0x2f0, false, 3, 2},
      {part, 0x80c, 0x00d, false, 3, 2},
      {part, 0x1f0, 0x3f2, false, 3, 2},
      {part, 0x10d, 0x90d, false, 3, 2},
      {partmega22, 0x101f0, 0x102f0, false, 4, 3},
      {partmega22, 0x2080c, 0x2000d, false, 4, 3},
      {partmega22, 0x101f0, 0x103f2, false, 4, 3},
      {partmega22, 0x2010d, 0x2090d, false, 4, 3}
    // TODO wenn xmega core implementiert ist, die folgenden tests aktivieren
//      {partxmega16, 0x1f0, 0, 2, false, 0x0ff0, 3},
//      {partxmega16, 0xff0, 0, 2, false, 0x01f0, 3},
//      {partxmega16, 0x1f0, 0xff, 2, false, 0x0ff0, 3},
//      {partxmega16, 0xff0, 0xff, 2, false, 0x01f0, 3},
//      {partxmega22, 0x101f0, 0, 3, false, 0x2ff0, 4},
//      {partxmega22, 0x20ff0, 0, 3, false, 0x1f0, 4},
//      {partxmega22, 0x101f0, 0xff, 3, false, 0x2ff0, 4},
//      {partxmega22, 0x20ff0, 0xff, 3, false, 0x1f0, 4}
    };
  }

  @Test(dataProvider = "Provider")
  public void testRcal(XmlPart p,
                       int ip,
                       int expectedIp,
                       boolean list,
                       int expectedCycles,
                       int expectedPush) throws Exception
  {
    final String cmd;
    if (expectedIp != ip) {
      cmd = "nop\n.org 0x" + Integer.toHexString(ip) + "\nrcall label\n.dw 0xffff\n.org 0x" + Integer.toHexString(expectedIp)
                    + "\nlabel:";
    } else {
      cmd = "nop\n.org 0x" + Integer.toHexString(ip) + "\nlabel:\nrcall label\n.dw 0xffff";
    }
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final Register sp = cpu.getStackPointer();
    final SRAM sram = device.getSRAM();
    final int spInit = sram.getSize() - 1;
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final int spNew = spInit - expectedPush;
    cpu.setIP(device,
              ip);
    sp.setValue(spInit);

    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((expectedCycles - 1) * 2) + 1; ++i) {
      device.onClock(cs.getAndNext());
    }
    expectedChange.add(sp.getMemoryAddress());
    if (sp.getSize() > 1 && (spNew & 0xff00) != (spInit & 0xff00)) {
      expectedChange.add(sp.getMemoryAddress() + 1);
    }
    if (sp.getSize() > 2 && (spNew & 0xff0000) != (spInit & 0xff0000)) {
      expectedChange.add(sp.getMemoryAddress() + 2);
    }
    if (sp.getSize() > 3 && (spNew & 0xff000000) != (spInit & 0xff000000)) {
      expectedChange.add(sp.getMemoryAddress() + 3);
    }
    for (int i = 0; i < expectedPush; ++i) {
      expectedChange.add(spInit - i);
    }
    device.onClock(cs.getAndNext());
    assertEquals(cpu.getIP(),
                 expectedIp,
                 cmd);
    assertEquals(spInit - sp.getValue(),
                 expectedPush,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
