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
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.impl.instructions.helper.ClockStateTestImpl;
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
public class LdsNGTest extends AbstractInstructionTest
{

  private static XmlPart part22bit;

  public LdsNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    AbstractInstructionTest.setUpClass();
    part22bit = DeviceStreamer.loadDevice(LdiNGTest.class.getResource("/com/avrwb/devices/ATmega22bitFlash.xml"),
                                          DeviceStreamer.Version.V_1_0);
    assertNotNull(part22bit,
                  "tmp==null");
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {part, 0x00, 0x00, 0x0066, 0x00, false, 0xff},
      {part, 0x01, 0x00, 0x0040, 0x00, false, 0xff},
      {part, 0x02, 0x00, 0x0066, 0x10, false, 0xff},
      {part22bit, 0x03, 0x00, 0x0066, 0x00, false, 0xff},
      {part22bit, 0x04, 0x00, 0x0166, 0x00, false, 0xff},
      {part22bit, 0x05, 0x00, 0x0066, 0x10, false, 0xff}
    };
  }

  @Test(dataProvider = "Provider")
  public void testLds(XmlPart p,
                      int rd,
                      int rdVal,
                      int k16,
                      int rampdInit,
                      boolean list,
                      int rdExpected) throws Exception
  {
    final String cmd = "lds r" + rd + ",0x" + Integer.toHexString(k16) + "\nnop\n.dw 0xffff";
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Register rampd = cpu.getRAMPD();
    final int rampdVal;
    final int ptr;

    if (rampd != null) {
      rampd.setValue(rampdInit);
      rampdVal = rampdInit;
    } else {
      rampdVal = 0;
    }
    ptr = (rampdVal << 16) + k16;
    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(ptr,
                   rdExpected);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < 3; ++i) {
      device.onClock(cs.getAndNext());
    }
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertEquals(sram.getByteAt(ptr),
                 rdExpected,
                 cmd);
    assertEquals(cpu.getIP(),
                 2,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
