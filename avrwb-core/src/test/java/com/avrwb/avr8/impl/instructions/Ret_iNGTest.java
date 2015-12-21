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
public class Ret_iNGTest extends AbstractInstructionTest
{

  private static XmlPart partmega22;

  public Ret_iNGTest()
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

  }

  @DataProvider(name = "RetProvider")
  public Object[][] getData()
  {
    return new Object[][]{
      {part, 0x1231, (~SREG.MASK_I) & 0xff, false, 0xff, 2, 4},
      {partmega22, 0x11232, (~SREG.MASK_I) & 0xff, false, 0xff, 3, 5},
      {part, 0x1233, 0x00, false, SREG.MASK_I, 2, 4},
      {partmega22, 0x11234, 0x00, false, SREG.MASK_I, 3, 5}
    };
  }

  @Test(dataProvider = "RetProvider")
  public void testRet(XmlPart p,
                      int returnIp,
                      int sregInit,
                      boolean list,
                      int sregExpected,
                      int expectedPop,
                      int cycles) throws Exception
  {
    final String cmd = "ret; return to 0x" + Integer.toHexString(returnIp) + "\n.dw 0xffff\n.org 0x" + Integer.
            toHexString(returnIp);
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final Stack stack = device.getStack();
    final Register sp = cpu.getStackPointer();
    final SRAM sram = device.getSRAM();
    final int spInit = sram.getSize() - 1;
    final Set<Integer> expectedChange = new HashSet<>();

    sp.setValue(spInit);
    for (int i = 0; i < expectedPop; ++i) {
      stack.push((returnIp >> (8 * i)) & 0xff);
    }
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((cycles - 1) * 2) + 1; ++i) {
      controller.stepCPU();
    }
    final int spNew = sp.getValue();
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
    controller.stepCPU();
    assertEquals(cpu.getIP(),
                 returnIp,
                 cmd);
    assertEquals(sp.getValue(),
                 spInit,
                 cmd);
  }

  @Test(dataProvider = "RetProvider")
  public void testReti(XmlPart p,
                       int returnIp,
                       int sregInit,
                       boolean list,
                       int sregExpected,
                       int expectedPop,
                       int cycles) throws Exception
  {
    final String cmd = "reti; return to 0x" + Integer.toHexString(returnIp) + "\n.dw 0xffff\n.org 0x" + Integer.
            toHexString(returnIp);
    final Device device = getDevice(p,
                                    cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final Stack stack = device.getStack();
    final Register sp = cpu.getStackPointer();
    final SRAM sram = device.getSRAM();
    final int spInit = sram.getSize() - 1;
    final SREG sreg = cpu.getSREG();
    final Set<Integer> expectedChange = new HashSet<>();

    sreg.setValue(sregInit);
    sp.setValue(spInit);
    for (int i = 0; i < expectedPop; ++i) {
      stack.push((returnIp >> (8 * i)) & 0xff);
    }
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);
    for (int i = 0; i < ((cycles - 1) * 2) + 1; ++i) {
      controller.stepCPU();
    }
    final int spNew = sp.getValue();
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
    if (sregInit != sregExpected) {
      expectedChange.add(sreg.getMemoryAddress());
    }
    controller.stepCPU();
    assertSREG(sreg.getValue(),
               sregExpected,
               cmd);
    assertEquals(cpu.getIP(),
                 returnIp,
                 cmd);
    assertEquals(sp.getValue(),
                 spInit,
                 cmd);
  }

}
