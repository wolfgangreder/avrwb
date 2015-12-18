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

import com.avrwb.assembler.Assembler;
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import static com.avrwb.avr8.impl.instructions.AbstractInstructionTest.assembler;
import com.avrwb.avr8.impl.instructions.helper.ClockStateTestImpl;
import com.avrwb.schema.XmlPart;
import com.avrwb.schema.util.DeviceStreamer;
import java.util.HashSet;
import java.util.Set;
import org.openide.util.Lookup;
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
public class EIJmpNGTest extends AbstractInstructionTest
{

//  private static XmlPart partxmega22;
  public EIJmpNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    assembler = Lookup.getDefault().lookup(Assembler.class);
    assertNotNull(assembler,
                  "assembler==null");
    part = DeviceStreamer.loadDevice(LdiNGTest.class.getResource("/com/avrwb/devices/ATmega22bitFlash.xml"),
                                     DeviceStreamer.Version.V_1_0);
    assertNotNull(part,
                  "part==null");
//    partxmega22 = DeviceStreamer.loadDevice(LdiNGTest.class.getResource("/com/avrwb/devices/ATxmega22bit.xml"),
//                                            DeviceStreamer.Version.V_1_0);
//    assertNotNull(partxmega22,
//                  "partxmega22==null");
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {part, 0x101f0, 0x1, 0x0, 0x0, false, 2},
      {part, 0x20ff0, 0x0, 0x1, 0x1, false, 2},
      {part, 0x101f0, 0x2, 0x2, 0x0, false, 2},
      {part, 0x20ff0, 0xcc, 0xff, 0x20, false, 2}
    // TODO wenn xmega core implementiert ist, die folgenden tests aktivieren
    //      {partxmega22, 0x101f0, 0, 3, false, 0x2ff0, 4},
    //      {partxmega22, 0x20ff0, 0, 3, false, 0x1f0, 4},
    //      {partxmega22, 0x101f0, 0xff, 3, false, 0x2ff0, 4},
    //      {partxmega22, 0x20ff0, 0xff, 3, false, 0x1f0, 4}
    };
  }

  @Test(dataProvider = "Provider")
  public void test(XmlPart p,
                   int ip,
                   int r30,
                   int r31,
                   int eind,
                   boolean list,
                   int expectedCycles) throws Exception
  {
    final String cmd;
    final int expectedIp = ((r31 & 0xff) << 8) + (r30 & 0xff) + (eind << 16);
    if (expectedIp != ip) {
      cmd = "nop\n.org 0x" + Integer.toHexString(ip) + "\neijmp\n.dw 0xffff\n.org 0x" + Integer.toHexString(expectedIp);
    } else {
      cmd = "nop\n.org 0x" + Integer.toHexString(ip) + "\neijmp\n.dw 0xffff";
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

    cpu.setIP(device,
              ip);
    cpu.getEIND().setValue(eind);
    sp.setValue(spInit);

    sram.setPointer(Pointer.Z,
                    expectedIp);

    assertEquals(sram.getByteAt(30),
                 r30,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 r31,
                 cmd);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((expectedCycles - 1) * 2) + 1; ++i) {
      device.onClock(cs.getAndNext());
    }
    device.onClock(cs.getAndNext());
    assertEquals(cpu.getEIND().getValue(),
                 eind,
                 cmd);
    assertEquals(cpu.getIP(),
                 expectedIp,
                 cmd);
    assertEquals(sram.getByteAt(30),
                 r30,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 r31,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
