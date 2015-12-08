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
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.SRAM;
import static com.avrwb.avr8.api.instructions.AbstractInstructionTest.part;
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
public class LatNGTest extends AbstractInstructionTest
{

  private static XmlPart part22bit;

  public LatNGTest()
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
      // ohne rampz
      {part, 0x00, 0x00, 0x60, 0x00, 0x00, 0x00, false, 0x00, 0x000060},
      {part, 0x01, 0xff, 0x61, 0x00, 0x00, 0xee, false, 0x00, 0x000061},
      {part, 0x02, 0x00, 0x62, 0x00, 0x00, 0xee, false, 0xee, 0x000062},
      {part, 0x03, 0xcc, 0x20, 0x01, 0x00, 0xee, false, 0x22, 0x000120},
      {part, 0x04, 0xaa, 0x21, 0x01, 0xff, 0xee, false, 0x44, 0x000121},
      // mit rampz
      {part22bit, 0x00, 0x00, 0x60, 0x00, 0x01, 0x00, false, 0x00, 0x010060},
      {part22bit, 0x01, 0xff, 0x61, 0x00, 0x02, 0xee, false, 0x00, 0x020061},
      {part22bit, 0x02, 0x00, 0x62, 0x00, 0x03, 0xee, false, 0xee, 0x030062},
      {part22bit, 0x03, 0xcc, 0x20, 0x01, 0x04, 0xee, false, 0x22, 0x040120},
      {part22bit, 0x04, 0xaa, 0x21, 0x01, 0x05, 0xee, false, 0x44, 0x050121}
    };
  }

  @Test(dataProvider = "Provider")
  public void testLat(XmlPart p,
                      int rd,
                      int rdVal,
                      int r30,
                      int r31,
                      int rampzInit,
                      int ramInit,
                      boolean list,
                      int rdExpected,
                      int expectedPtr) throws Exception
  {
    final String cmd = "lat Z,r" + rd + "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Register rampz = cpu.getRAMP(Pointer.Z);
    final int zptr = r30 + (r31 << 8);
    final int ptr = zptr + (rampz != null ? (rampzInit << 16) : 0);

    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(30,
                   r30);
    sram.setByteAt(31,
                   r31);
    if (rampz != null) {
      rampz.setValue(rampzInit);
    }
    sram.setByteAt(ptr,
                   ramInit);
    assertEquals(sram.getPointer(Pointer.Z),
                 zptr,
                 cmd);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    device.onClock(cs.getAndNext());
    device.onClock(cs.getAndNext());
    device.onClock(cs.getAndNext());
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    if (rdExpected != ramInit) {
      expectedChange.add(ptr);
    }
    device.onClock(cs.getAndNext());
    assertEquals(ptr,
                 expectedPtr,
                 cmd);
    assertEquals(sram.getByteAt(30),
                 r30,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 r31,
                 cmd);
    if (rampz != null) {
      assertEquals(rampz.getValue(),
                   rampzInit,
                   cmd);
    }
    assertEquals(sram.getByteAt(expectedPtr),
                 rdExpected,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
