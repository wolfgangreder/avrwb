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
import com.avrwb.avr8.api.SimulationException;
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
public class LdNGTest extends AbstractInstructionTest
{

  private static XmlPart part22bit;

  public LdNGTest()
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

  private int getLoPart(int ptr)
  {
    return ptr & 0xff;
  }

  private int getHiPart(int ptr)
  {
    return (ptr & 0xff00) >> 8;
  }

  private int getRampPart(int ptr)
  {
    return (ptr & 0xff0000) >> 16;
  }

  @DataProvider(name = "Provider1")
  public Object[][] getData()
  {
    return new Object[][]{
      {part, 0x00, 0x00, 0x62, 0x00, 0x00, Pointer.X, false, 0xff, 1},
      {part, 0x01, 0x22, 0x02, 0x01, 0x00, Pointer.X, false, 0x11, 1},
      {part, 0x02, 0x00, 0x62, 0x00, 0x00, Pointer.Y, false, 0xff, 1},
      {part, 0x03, 0x22, 0x02, 0x01, 0x00, Pointer.Y, false, 0x11, 1},
      {part, 0x04, 0x00, 0x62, 0x00, 0x00, Pointer.Z, false, 0xff, 1},
      {part, 0x05, 0x22, 0x02, 0x01, 0x00, Pointer.Z, false, 0x11, 1},
      {part22bit, 0x06, 0x00, 0x62, 0x00, 0x00, Pointer.X, false, 0xff, 1},
      {part22bit, 0x07, 0x22, 0x02, 0x01, 0x01, Pointer.X, false, 0x11, 1},
      {part22bit, 0x08, 0x00, 0x62, 0x00, 0x02, Pointer.Y, false, 0xff, 1},
      {part22bit, 0x09, 0x22, 0x02, 0x01, 0x00, Pointer.Y, false, 0x11, 1},
      {part22bit, 0x0a, 0x00, 0x62, 0x00, 0x00, Pointer.Z, false, 0xff, 1},
      {part22bit, 0x0b, 0x22, 0x02, 0x01, 0x00, Pointer.Z, false, 0x11, 1}
    };
  }

  @Test(dataProvider = "Provider1")
  public void testLd1(XmlPart p,
                      int rd,
                      int rdVal,
                      int ptrLo,
                      int ptrHi,
                      int rampInit,
                      Pointer ptr,
                      boolean list,
                      int rdExpected,
                      int cycles) throws Exception
  {
    final String cmd = "ld r" + rd + "," + ptr.name() + "\nnop\n.dw 0xffff";
    final int ptrAddrLo;
    switch (ptr) {
      case X:
        ptrAddrLo = 26;
        break;
      case Y:
        ptrAddrLo = 28;
        break;
      case Z:
        ptrAddrLo = 30;
        break;
      default:
        throw new SimulationException(() -> "unkown pointer " + ptr);
    }
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Register ramp = cpu.getRAMP(ptr);
    final Set<Integer> expectedChange = new HashSet<>();
    final boolean smallRam = sram.getHexAddressStringWidth() < 3;
    final int ptrValue;

    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(ptrAddrLo,
                   ptrLo);
    if (!smallRam) {
      sram.setByteAt(ptrAddrLo + 1,
                     ptrHi);
      if (ramp != null) {
        ramp.setValue(rampInit);
        ptrValue = ptrLo + (ptrHi << 8) + (rampInit << 16);
      } else {
        ptrValue = ptrLo + (ptrHi << 8);
      }
    } else {
      ptrValue = ptrLo;
    }
    sram.setByteAt(ptrValue,
                   rdExpected);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((cycles - 1) * 2) + 1; ++i) {
      controller.stepCPU();
    }
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertEquals(sram.getByteAt(ptrAddrLo),
                 ptrLo,
                 cmd);
    if (!smallRam) {
      assertEquals(sram.getByteAt(ptrAddrLo + 1),
                   ptrHi,
                   cmd);
      if (ramp != null) {
        assertEquals(ramp.getValue(),
                     rampInit,
                     cmd);
      }
    }
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

  @DataProvider(name = "Provider2")
  public Object[][] getData2()
  {
    return new Object[][]{
      {part, 0x00, 0x00, 0x62, 0x00, 0x00, Pointer.X, false, 0xff, 2},
      {part, 0x01, 0x22, 0x02, 0x01, 0x00, Pointer.X, false, 0x11, 2},
      {part, 0x02, 0x00, 0x62, 0x00, 0x00, Pointer.Y, false, 0xff, 2},
      {part, 0x03, 0x22, 0x02, 0x01, 0x00, Pointer.Y, false, 0x11, 2},
      {part, 0x04, 0x00, 0xff, 0x00, 0x00, Pointer.Z, false, 0xff, 2},
      {part, 0x05, 0x22, 0x02, 0x01, 0x00, Pointer.Z, false, 0x11, 2},
      {part22bit, 0x06, 0x00, 0x62, 0x00, 0x00, Pointer.X, false, 0xff, 2},
      {part22bit, 0x07, 0x22, 0x02, 0x01, 0x01, Pointer.X, false, 0x11, 2},
      {part22bit, 0x08, 0x00, 0x62, 0x00, 0x02, Pointer.Y, false, 0xff, 2},
      {part22bit, 0x09, 0x22, 0x02, 0x01, 0x00, Pointer.Y, false, 0x11, 2},
      {part22bit, 0x0a, 0x00, 0xff, 0xff, 0x00, Pointer.Z, false, 0xff, 2},
      {part22bit, 0x0b, 0x22, 0x02, 0x01, 0x00, Pointer.Z, false, 0x11, 2}
    };
  }

  @Test(dataProvider = "Provider2")
  public void testLd2(XmlPart p,
                      int rd,
                      int rdVal,
                      int ptrLo,
                      int ptrHi,
                      int rampInit,
                      Pointer ptr,
                      boolean list,
                      int rdExpected,
                      int cycles) throws Exception
  {
    final String cmd = "ld r" + rd + "," + ptr.name() + "+\nnop\n.dw 0xffff";
    final int ptrAddrLo;
    switch (ptr) {
      case X:
        ptrAddrLo = 26;
        break;
      case Y:
        ptrAddrLo = 28;
        break;
      case Z:
        ptrAddrLo = 30;
        break;
      default:
        throw new SimulationException(() -> "unkown pointer " + ptr);
    }
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Register ramp = cpu.getRAMP(ptr);
    final Set<Integer> expectedChange = new HashSet<>();
    final boolean smallRam = sram.getHexAddressStringWidth() < 3;
    final int ptrValue;

    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(ptrAddrLo,
                   ptrLo);
    if (!smallRam) {
      sram.setByteAt(ptrAddrLo + 1,
                     ptrHi);
      if (ramp != null) {
        ramp.setValue(rampInit);
        ptrValue = ptrLo + (ptrHi << 8) + (rampInit << 16);
      } else {
        ptrValue = ptrLo + (ptrHi << 8);
      }
    } else {
      ptrValue = ptrLo;
    }
    final int exptectedPtr = ptrValue + 1;
    sram.setByteAt(ptrValue,
                   rdExpected);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((cycles - 1) * 2) + 1; ++i) {
      controller.stepCPU();
    }
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    if (ptrLo != getLoPart(exptectedPtr)) {
      expectedChange.add(ptrAddrLo);
    }
    if (ptrHi != getHiPart(exptectedPtr)) {
      expectedChange.add(ptrAddrLo + 1);
    }
    if (ramp != null && rampInit != getRampPart(exptectedPtr)) {
      expectedChange.add(ramp.getMemoryAddress());
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertEquals(sram.getByteAt(ptrAddrLo),
                 exptectedPtr & 0xff,
                 cmd);
    if (!smallRam) {
      assertEquals(sram.getByteAt(ptrAddrLo + 1),
                   (exptectedPtr & 0xff00) >> 8,
                   cmd);
      if (ramp != null) {
        assertEquals(ramp.getValue(),
                     (exptectedPtr & 0xff0000) >> 16,
                     cmd);
      }
    }
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

  @DataProvider(name = "Provider3")
  public Object[][] getData3()
  {
    return new Object[][]{
      {part, 0x00, 0x00, 0x62, 0x00, 0x00, Pointer.X, false, 0xff, 3},
      {part, 0x01, 0x22, 0x02, 0x01, 0x00, Pointer.X, false, 0x11, 3},
      {part, 0x02, 0x00, 0x62, 0x00, 0x00, Pointer.Y, false, 0xff, 3},
      {part, 0x03, 0x22, 0x02, 0x01, 0x00, Pointer.Y, false, 0x11, 3},
      {part, 0x04, 0x00, 0xff, 0x00, 0x00, Pointer.Z, false, 0xff, 3},
      {part, 0x05, 0x22, 0x02, 0x01, 0x00, Pointer.Z, false, 0x11, 3},
      {part22bit, 0x06, 0x00, 0x62, 0x00, 0x00, Pointer.X, false, 0xff, 3},
      {part22bit, 0x07, 0x22, 0x02, 0x01, 0x01, Pointer.X, false, 0x11, 3},
      {part22bit, 0x08, 0x00, 0x62, 0x00, 0x02, Pointer.Y, false, 0xff, 3},
      {part22bit, 0x09, 0x22, 0x02, 0x01, 0x00, Pointer.Y, false, 0x11, 3},
      {part22bit, 0x0a, 0x00, 0xff, 0xff, 0x00, Pointer.Z, false, 0xff, 3},
      {part22bit, 0x0b, 0x22, 0x02, 0x01, 0x00, Pointer.Z, false, 0x11, 3}
    };
  }

  @Test(dataProvider = "Provider3")
  public void testLd3(XmlPart p,
                      int rd,
                      int rdVal,
                      int ptrLo,
                      int ptrHi,
                      int rampInit,
                      Pointer ptr,
                      boolean list,
                      int rdExpected,
                      int cycles) throws Exception
  {
    final String cmd = "ld r" + rd + ",-" + ptr.name() + "\nnop\n.dw 0xffff";
    final int ptrAddrLo;
    switch (ptr) {
      case X:
        ptrAddrLo = 26;
        break;
      case Y:
        ptrAddrLo = 28;
        break;
      case Z:
        ptrAddrLo = 30;
        break;
      default:
        throw new SimulationException(() -> "unkown pointer " + ptr);
    }
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Register ramp = cpu.getRAMP(ptr);
    final Set<Integer> expectedChange = new HashSet<>();
    final boolean smallRam = sram.getHexAddressStringWidth() < 3;
    final int ptrValue;

    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(ptrAddrLo,
                   ptrLo);
    if (!smallRam) {
      sram.setByteAt(ptrAddrLo + 1,
                     ptrHi);
      if (ramp != null) {
        ramp.setValue(rampInit);
        ptrValue = ptrLo + (ptrHi << 8) + (rampInit << 16);
      } else {
        ptrValue = ptrLo + (ptrHi << 8);
      }
    } else {
      ptrValue = ptrLo;
    }
    final int exptectedPtr = ptrValue - 1;
    sram.setByteAt(exptectedPtr,
                   rdExpected);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((cycles - 1) * 2) + 1; ++i) {
      controller.stepCPU();
    }
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    if (ptrLo != getLoPart(exptectedPtr)) {
      expectedChange.add(ptrAddrLo);
    }
    if (ptrHi != getHiPart(exptectedPtr)) {
      expectedChange.add(ptrAddrLo + 1);
    }
    if (ramp != null && rampInit != getRampPart(exptectedPtr)) {
      expectedChange.add(ramp.getMemoryAddress());
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertEquals(sram.getByteAt(ptrAddrLo),
                 exptectedPtr & 0xff,
                 cmd);
    if (!smallRam) {
      assertEquals(sram.getByteAt(ptrAddrLo + 1),
                   (exptectedPtr & 0xff00) >> 8,
                   cmd);
      if (ramp != null) {
        assertEquals(ramp.getValue(),
                     (exptectedPtr & 0xff0000) >> 16,
                     cmd);
      }
    }
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

  @DataProvider(name = "Provider4")
  public Object[][] getData4()
  {
    return new Object[][]{
      {part22bit, 0x08, 0x00, 0x62, 0x00, 0x02, 0, Pointer.Y, true, 0xff, 1},
      {part22bit, 0x09, 0x22, 0x02, 0x01, 0x00, 1, Pointer.Y, true, 0x11, 2},
      {part22bit, 0x0a, 0x00, 0xff, 0xff, 0x00, 2, Pointer.Z, true, 0xff, 2},
      {part22bit, 0x0b, 0x22, 0x02, 0x01, 0x00, 3, Pointer.Z, true, 0x11, 2}
    };
  }

  @Test(dataProvider = "Provider4", enabled = false) // TODO befehl ist nur bei xmega implementiert
  public void testLd4(XmlPart p,
                      int rd,
                      int rdVal,
                      int ptrLo,
                      int ptrHi,
                      int rampInit,
                      int q,
                      Pointer ptr,
                      boolean list,
                      int rdExpected,
                      int cycles) throws Exception
  {
    final String cmd = "ldd r" + rd + "," + ptr.name() + "+" + q + "\nnop\n.dw 0xffff";
    final int ptrAddrLo;
    switch (ptr) {
      case X:
        ptrAddrLo = 26;
        break;
      case Y:
        ptrAddrLo = 28;
        break;
      case Z:
        ptrAddrLo = 30;
        break;
      default:
        throw new SimulationException(() -> "unkown pointer " + ptr);
    }
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Register ramp = cpu.getRAMP(ptr);
    final Set<Integer> expectedChange = new HashSet<>();
    final boolean smallRam = sram.getHexAddressStringWidth() < 3;
    final int ptrValue;

    sram.setByteAt(rd,
                   rdVal);
    sram.setByteAt(ptrAddrLo,
                   ptrLo);
    if (!smallRam) {
      sram.setByteAt(ptrAddrLo + 1,
                     ptrHi);
      if (ramp != null) {
        ramp.setValue(rampInit);
        ptrValue = ptrLo + (ptrHi << 8) + (rampInit << 16);
      } else {
        ptrValue = ptrLo + (ptrHi << 8);
      }
    } else {
      ptrValue = ptrLo;
    }
    sram.setByteAt(ptrValue + q,
                   rdExpected);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((cycles - 1) * 2) + 1; ++i) {
      controller.stepCPU();
    }
    if (rdVal != rdExpected) {
      expectedChange.add(rd);
    }
    controller.stepCPU();
    assertEquals(sram.getByteAt(rd),
                 rdExpected,
                 cmd);
    assertEquals(sram.getByteAt(ptrAddrLo),
                 (ptrValue) & 0xff,
                 cmd);
    if (!smallRam) {
      assertEquals(sram.getByteAt(ptrAddrLo + 1),
                   ((ptrValue) & 0xff00) >> 8,
                   cmd);
      if (ramp != null) {
        assertEquals(ramp.getValue(),
                     ((ptrValue) & 0xff0000) >> 16,
                     cmd);
      }
    }
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
