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
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class InOutNGTest extends AbstractInstructionTest
{

  public InOutNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {0x00, 0x00, 0x10, 0xff, false},
      {0x01, 0x00, 0x10, 0x00, false},
      {0x02, 0x01, 0x10, 0x10, false},
      {0x03, 0x10, 0x10, 0x0f, false}
    };
  }

  @Test(dataProvider = "Provider")
  public void testIn(int rd,
                     int rdVal,
                     final int port,
                     int portInit,
                     boolean list) throws Exception
  {
    final String cmd = "in r" + rd + ",0x" + Integer.toHexString(port) + "; r" + rd + "=0x" + Integer.toHexString(
            rdVal) + ", 0x" + Integer.toHexString(port) + "=0x" + Integer.
            toHexString(portInit);
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Register register = device.getIOSpace().get(port);

    sram.setByteAt(rd,
                   rdVal);
    register.setValue(portInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    device.onClock(cs.getAndNext());
    if (rdVal != portInit) {
      expectedChange.add(rd);
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 portInit,
                 cmd);
    assertEquals(register.getValue(),
                 portInit,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

  @Test(dataProvider = "Provider")
  public void testOut(int rd,
                      int rdVal,
                      final int port,
                      int portInit,
                      boolean list) throws Exception
  {
    final String cmd = "out 0x" + Integer.toHexString(port) + ",r" + rd + "; r" + rd + "=0x" + Integer.toHexString(
            rdVal) + ", 0x" + Integer.toHexString(port) + "=0x" + Integer.
            toHexString(portInit);
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Register register = device.getIOSpace().get(port);

    sram.setByteAt(rd,
                   rdVal);
    register.setValue(portInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    device.onClock(cs.getAndNext());
    if (rdVal != portInit) {
      expectedChange.add(register.getMemoryAddress());
    }
    device.onClock(cs.getAndNext());
    assertEquals(sram.getByteAt(rd),
                 rdVal,
                 cmd);
    assertEquals(register.getValue(),
                 rdVal,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
