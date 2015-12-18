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
import com.avrwb.schema.util.DeviceStreamer;
import java.util.HashSet;
import java.util.Set;
import org.openide.util.Lookup;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class ElpmNGTest extends AbstractInstructionTest
{

  public ElpmNGTest()
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
                  "tmp==null");
  }

  @DataProvider(name = "Provider1")
  public Object[][] getData()
  {
    return new Object[][]{
      {0x00, 0x10, 0x00, 0x00, false, 0xf5, 3},
      {0xff, 0x10, 0x20, 0x00, false, 0xc5, 3},
      {0xff, 0x30, 0x20, 0x10, false, 0xcc, 3}
    };
  }

  @Test(dataProvider = "Provider1")
  public void testElpm_1(int r0,
                         int r30,
                         int r31,
                         int rampzInit,
                         boolean list,
                         int expectedR0,
                         int expectedCycles) throws Exception
  {
    final int address = (((rampzInit << 8) + r31) << 8) + r30;
    String cmd = "elpm\nnop\n.org 0x" + Integer.toHexString(address / 2) + "\n.db 0x";
    if ((address % 2) != 0) {
      cmd += Integer.toHexString((~expectedR0) & 0xff) + ",0x";
    }
    cmd += Integer.toHexString(expectedR0 & 0xff);
    final Device device = getDevice(cmd,
                                    list);
    final SRAM sram = device.getSRAM();
    final CPU cpu = device.getCPU();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Register rampz = cpu.getRAMP(Pointer.Z);

    sram.setByteAt(0,
                   r0);
    sram.setPointer(Pointer.Z,
                    address & 0xffff);
    rampz.setValue(rampzInit);
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
    if (r0 != expectedR0) {
      expectedChange.add(0);
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(0),
                 expectedR0,
                 cmd);
    assertEquals(sram.getByteAt(30),
                 r30,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 r31,
                 cmd);
    assertEquals(rampz.getValue(),
                 rampzInit,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
  }

  @DataProvider(name = "Provider2")
  public Object[][] getData2()
  {
    return new Object[][]{
      {1, 0x00, 0x10, 0x00, 0x00, false, 0xf5, 3},
      {2, 0xff, 0x10, 0x20, 0x00, false, 0xc5, 3},
      {3, 0xff, 0x30, 0x20, 0x10, false, 0xcc, 3}
    };
  }

  @Test(dataProvider = "Provider2")
  public void testElpm_2(int rd,
                         int rdVal,
                         int r30,
                         int r31,
                         int rampzInit,
                         boolean list,
                         int expectedR0,
                         int expectedCycles) throws Exception
  {
    final int address = (((rampzInit << 8) + r31) << 8) + r30;
    String cmd = "elpm r" + rd + ",Z\nnop\n.org 0x" + Integer.toHexString(address / 2) + "\n.db 0x";
    if ((address % 2) != 0) {
      cmd += Integer.toHexString((~expectedR0) & 0xff) + ",0x";
    }
    cmd += Integer.toHexString(expectedR0 & 0xff);
    cmd += "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd,
                                    list);
    final SRAM sram = device.getSRAM();
    final CPU cpu = device.getCPU();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Register rampz = cpu.getRAMP(Pointer.Z);

    sram.setByteAt(rd,
                   rdVal);
    sram.setPointer(Pointer.Z,
                    address & 0xffff);
    rampz.setValue(rampzInit);
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
    if (sram.getByteAt(rd) != expectedR0) {
      expectedChange.add(rd);
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 expectedR0,
                 cmd);
    assertEquals(sram.getByteAt(30),
                 r30,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 r31,
                 cmd);
    assertEquals(rampz.getValue(),
                 rampzInit,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
  }

  @DataProvider(name = "Provider3")
  public Object[][] getData3()
  {
    return new Object[][]{
      {1, 0x00, 0x10, 0x00, 0x00, false, 0xf5, 3, 0x00, 0x00},
      {2, 0xff, 0x10, 0x20, 0x00, false, 0xc5, 3, 0x20, 0x00},
      {3, 0xff, 0x30, 0x20, 0x10, false, 0xcc, 3, 0x20, 0x10},
      {4, 0x00, 0xff, 0x00, 0x00, false, 0xf5, 3, 0x01, 0x00},
      {5, 0xff, 0xff, 0xff, 0x00, false, 0xc5, 3, 0x00, 0x01}
    };
  }

  @Test(dataProvider = "Provider3")
  public void testElpm_3(int rd,
                         int rdVal,
                         int r30,
                         int r31,
                         int rampzInit,
                         boolean list,
                         int expectedR0,
                         int expectedCycles,
                         int r31Exp,
                         int rampExp) throws Exception
  {
    final int address = (((rampzInit << 8) + r31) << 8) + r30;
    String cmd = "elpm r" + rd + ",Z+\nnop\n.org 0x" + Integer.toHexString(address / 2) + "\n.db 0x";
    if ((address % 2) != 0) {
      cmd += Integer.toHexString((~expectedR0) & 0xff) + ",0x";
    }
    cmd += Integer.toHexString(expectedR0 & 0xff);
    cmd += "; r" + rd + "=0x" + Integer.toHexString(rdVal);
    final Device device = getDevice(cmd,
                                    list);
    final SRAM sram = device.getSRAM();
    final CPU cpu = device.getCPU();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Register rampz = cpu.getRAMP(Pointer.Z);

    sram.setByteAt(rd,
                   rdVal);
    sram.setPointer(Pointer.Z,
                    address & 0xffff);
    rampz.setValue(rampzInit);
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
    if (sram.getByteAt(rd) != expectedR0) {
      expectedChange.add(rd);
    }
    expectedChange.add(30);
    if (r31Exp != r31) {
      expectedChange.add(31);
    }
    if (rampExp != rampzInit) {
      expectedChange.add(rampz.getMemoryAddress());
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 expectedR0,
                 cmd);
    assertEquals(sram.getByteAt(30),
                 (r30 + 1) & 0xff,
                 cmd);
    assertEquals(sram.getByteAt(31),
                 r31Exp,
                 cmd);
    assertEquals(rampz.getValue(),
                 rampExp,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
  }

}
