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
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.Stack;
import com.avrwb.schema.XmlPart;
import com.avrwb.schema.util.DeviceStreamer;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class CallNGTest extends AbstractInstructionTest
{

  private static XmlPart partmega22;
  private static XmlPart partxmega16;
  private static XmlPart partxmega22;

  public CallNGTest()
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
      {part, 0x1f0, 0, 2, false, 0x0ff0, 4},
      {part, 0xff0, 0, 2, false, 0x01f0, 4},
      {part, 0x1f0, 0xff, 2, false, 0x0ff0, 4},
      {part, 0xff0, 0xff, 2, false, 0x01f0, 4},
      {partmega22, 0x101f0, 0, 3, false, 0x2ff0, 5},
      {partmega22, 0x20ff0, 0, 3, false, 0x1f0, 5},
      {partmega22, 0x101f0, 0xff, 3, false, 0x2ff0, 5},
      {partmega22, 0x20ff0, 0xff, 3, false, 0x1f0, 5}
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
  public void testCall(XmlPart p,
                       int ip,
                       int sregInit,
                       int pushBytes,
                       boolean list,
                       int targetAddress,
                       int expectedCycles) throws Exception
  {
    final String cmd = "nop\n.org 0x" + Integer.toHexString(ip) + "\ncall label\nnop\n.org 0x" + Integer.
            toHexString(targetAddress) + "\nlabel:\nnop";
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final Register sp = cpu.getStackPointer();
    final SREG sreg = cpu.getSREG();
    final Stack stack = device.getStack();
    final Set<Integer> expectedChange = new HashSet<>();
    final int spInit = device.getSRAM().getSize() - 1;

    cpu.setIP(context,
              ip);
    sreg.setValue(sregInit);
    sp.setValue(spInit);
    device.getSRAM().addMemoryChangeListener(new MemoryChangeHandler(device.getSRAM(),
                                                                     expectedChange,
                                                                     cmd)::onMemoryChanged);

    for (int i = 0; i < (expectedCycles - 1) * 2; ++i) {
      controller.stepCPU();
    }
    for (int i = 0; i < sp.getSize(); ++i) {
      expectedChange.add(sp.getMemoryAddress() + i);
    }
    for (int i = 0; i < pushBytes; ++i) {
      expectedChange.add(spInit - i);
    }
    controller.stepCPU();
    controller.stepCPU();
    assertEquals(stack.getSP(),
                 spInit - pushBytes,
                 cmd);
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
