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
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.impl.instructions.helper.ClockStateTestImpl;
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
public class XchNGTest extends AbstractInstructionTest
{

  private static XmlPart part22bit;

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
      {part, 0, 0x00, 0xf0, 0x00, 0x00, 0xff, false},
      {part, 1, 0xff, 0x11, 0x01, 0x00, 0x00, false},
      {part22bit, 2, 0x00, 0xf0, 0x0f, 0x0e, 0xff, false},
      {part22bit, 3, 0xff, 0xf0, 0x0f, 0x00, 0x00, false}
    };
  }

  @Test(dataProvider = "Provider")
  public void testXch(XmlPart p,
                      int rd,
                      int rdVal,
                      int ptrLo,
                      int ptrHi,
                      int ramp,
                      int ptrVal,
                      boolean list) throws Exception
  {
    final String cmd = "xch Z,r" + rd;
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Register rampz = cpu.getRAMP(Pointer.Z);
    final int rampdVal;
    final int ptr;

    if (rampz != null) {
      rampz.setValue(ramp);
      rampdVal = ramp;
    } else {
      rampdVal = 0;
    }
    ptr = (rampdVal << 16) + (ptrHi << 8) + ptrLo;
    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(ptr,
                   ptrVal);
    sram.setPointer(Pointer.Z,
                    ptr & 0xffff);
    sram.addMemoryChangeListener(new AbstractInstructionTest.MemoryChangeHandler(sram,
                                                                                 expectedChange,
                                                                                 cmd)::onMemoryChanged);
    for (int i = 0; i < 3; ++i) {
      device.onClock(cs.getAndNext());
    }
    if (ptrVal != rdVal) {
      expectedChange.add(rd);
      expectedChange.add(ptr);
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 ptrVal,
                 cmd);
    assertEquals(sram.getByteAt(ptr),
                 rdVal,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
