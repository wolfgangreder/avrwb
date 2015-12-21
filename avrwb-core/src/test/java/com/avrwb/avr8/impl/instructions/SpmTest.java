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
public class SpmTest extends AbstractInstructionTest
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

  @DataProvider(name = "ProviderNaked")
  public Object[][] getDataNaked()
  {
    return new Object[][]{
      {part, 0x00, 0x00, 0x00, -1, false, 0xc8},
      {part, 0x00, 0x01, 0x00, -1, false, 0x95},
      {part, 0x00, 0x02, 0x00, -1, false, 0x00},
      {part, 0x00, 0x03, 0x00, -1, false, 0x00},
      {part, 0x00, 0x00, 0x10, -1, false, 0x12},
      {part, 0x00, 0x04, 0x10, -1, false, 0x34},
      {part22bit, 0x00, 0x00, 0x00, 0x00, false, 0xc8},
      {part22bit, 0x00, 0x01, 0x00, 0x00, false, 0x95},
      {part22bit, 0x00, 0x02, 0x00, 0x00, false, 0x00},
      {part22bit, 0x00, 0x03, 0x00, 0x00, false, 0x00},
      {part22bit, 0x00, 0x00, 0x10, 0x01, false, 0x12},
      {part22bit, 0x00, 0x04, 0x10, 0x02, false, 0x34}};
  }

  @Test(dataProvider = "ProviderNaked", enabled = false)
  public void testSPMNaked(XmlPart p,
                           int rdVal,
                           int ptrLo,
                           int ptrHi,
                           int rampzInit,
                           boolean list,
                           int rdExpected) throws Exception
  {
    final int rampzVal;
    final int ptr;
    if (rampzInit != -1) {
      rampzVal = rampzInit;
    } else {
      rampzVal = 0;
    }
    ptr = (rampzVal << 16) + (ptrHi << 8) + ptrLo;
    final String cmd;
    if (ptr > 4) {
      cmd = "spm\nnop\n.dw 0xffff\n.org 0x" + Integer.toHexString(ptr / 2) + "\n.db 0x" + Integer.toHexString(rdExpected);
    } else {
      cmd = "spm\nnop\n.dw 0xffff";
    }
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final Register rampz = cpu.getRAMP(Pointer.Z);
    if (rampz != null) {
      rampz.setValue(rampzInit);
    }
    sram.setByteAt(0,
                   rdVal);
    sram.setByteAt(30,
                   ptrLo);
    sram.setByteAt(31,
                   ptrHi);
    assertEquals(sram.getPointer(Pointer.Z),
                 ptr & 0xffff,
                 cmd);
    sram.addMemoryChangeListener(new AbstractInstructionTest.MemoryChangeHandler(sram,
                                                                                 expectedChange,
                                                                                 cmd)::onMemoryChanged);
    for (int i = 0; i < 5; ++i) {
      controller.stepCPU();
    }
    if (rdVal != rdExpected) {
      expectedChange.add(0);
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(0),
                 rdExpected,
                 cmd);
    assertEquals(sram.getByteAt(30),
                 ptrLo,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 ptrHi,
                 cmd);
    if (rampz != null) {
      assertEquals(rampz.getValue(),
                   rampzInit,
                   cmd);
    }
    assertEquals(sram.getByteAt(0),
                 rdExpected,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);

  }

}
