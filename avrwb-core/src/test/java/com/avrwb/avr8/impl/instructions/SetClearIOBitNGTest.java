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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class SetClearIOBitNGTest extends AbstractInstructionTest
{

  public SetClearIOBitNGTest()
  {
  }

  @DataProvider(name = "ProviderSbi")
  public Object[][] getDataSbi()
  {
    return new Object[][]{
      {0x10, 0, 0, false, 0x01},
      {0x10, 0, 1, false, 0x02},
      {0x10, 0, 2, false, 0x04},
      {0x10, 0, 3, false, 0x08},
      {0x10, 0, 4, false, 0x10},
      {0x10, 0, 5, false, 0x20},
      {0x10, 0, 6, false, 0x40},
      {0x10, 0, 7, false, 0x80}
    };
  }

  @Test(dataProvider = "ProviderSbi")
  public void testSbi(int io,
                      int ioInit,
                      int bit,
                      boolean list,
                      int ioExpected) throws Exception
  {
    final String cmd = "sbi 0x" + Integer.toHexString(io) + "," + bit;
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final Register reg = device.getIOSpace().get(io);
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();

    reg.setValue(ioInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    for (int i = 0; i < 3; ++i) {
      device.onClock(cs.getAndNext());
    }
    if (ioExpected != ioInit) {
      expectedChange.add(reg.getMemoryAddress());
    }
    device.onClock(cs.getAndNext());
    assertEquals(reg.getValue(),
                 ioExpected,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

  @DataProvider(name = "ProviderCbi")
  public Object[][] getDataCbi()
  {
    return new Object[][]{
      {0x10, 0xff, 0, false, 0xfe},
      {0x10, 0xff, 1, false, 0xfd},
      {0x10, 0xff, 2, false, 0xfb},
      {0x10, 0xff, 3, false, 0xf7},
      {0x10, 0xff, 4, false, 0xef},
      {0x10, 0xff, 5, false, 0xdf},
      {0x10, 0xff, 6, false, 0xbf},
      {0x10, 0xff, 7, false, 0x7f}
    };
  }

  @Test(dataProvider = "ProviderCbi")
  public void testCbi(int io,
                      int ioInit,
                      int bit,
                      boolean list,
                      int ioExpected) throws Exception
  {
    final String cmd = "cbi 0x" + Integer.toHexString(io) + "," + bit;
    final Device device = getDevice(cmd,
                                    list);
    final CPU cpu = device.getCPU();
    final Register reg = device.getIOSpace().get(io);
    final SRAM sram = device.getSRAM();
    final Set<Integer> expectedChange = new HashSet<>();
    final ClockStateTestImpl cs = new ClockStateTestImpl();

    reg.setValue(ioInit);
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    for (int i = 0; i < 3; ++i) {
      device.onClock(cs.getAndNext());
    }
    if (ioExpected != ioInit) {
      expectedChange.add(reg.getMemoryAddress());
    }
    device.onClock(cs.getAndNext());
    assertEquals(reg.getValue(),
                 ioExpected,
                 cmd);
    assertEquals(cpu.getIP(),
                 1,
                 cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
  }

}
