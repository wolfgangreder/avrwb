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
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.instructions.helper.ClockStateTestImpl;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class Bclr_BsetNGTest extends AbstractInstructionTest
{

  public Bclr_BsetNGTest()
  {
  }

  @DataProvider(name = "Provider")
  public Object[][] getData()
  {
    return new Object[][]{
      {"cli", 0xff, 0xff & ~SREG.MASK_I},
      {"clc", 0xff, 0xff & ~SREG.MASK_C},
      {"clh", 0xff, 0xff & ~SREG.MASK_H},
      {"cln", 0xff, 0xff & ~SREG.MASK_N},
      {"cls", 0xff, 0xff & ~SREG.MASK_S},
      {"clt", 0xff, 0xff & ~SREG.MASK_T},
      {"clv", 0xff, 0xff & ~SREG.MASK_V},
      {"clz", 0xff, 0xff & ~SREG.MASK_Z},
      {"sei", 0x00, SREG.MASK_I},
      {"sec", 0x00, SREG.MASK_C},
      {"seh", 0x00, SREG.MASK_H},
      {"sen", 0x00, SREG.MASK_N},
      {"ses", 0x00, SREG.MASK_S},
      {"set", 0x00, SREG.MASK_T},
      {"sev", 0x00, SREG.MASK_V},
      {"sez", 0x00, SREG.MASK_Z},
      {"bclr 7", 0xff, 0xff & ~SREG.MASK_I},
      {"bclr 0", 0xff, 0xff & ~SREG.MASK_C},
      {"bclr 5", 0xff, 0xff & ~SREG.MASK_H},
      {"bclr 2", 0xff, 0xff & ~SREG.MASK_N},
      {"bclr 4", 0xff, 0xff & ~SREG.MASK_S},
      {"bclr 6", 0xff, 0xff & ~SREG.MASK_T},
      {"bclr 3", 0xff, 0xff & ~SREG.MASK_V},
      {"bclr 1", 0xff, 0xff & ~SREG.MASK_Z},
      {"bset 7", 0x00, SREG.MASK_I},
      {"bset 0", 0x00, SREG.MASK_C},
      {"bset 5", 0x00, SREG.MASK_H},
      {"bset 2", 0x00, SREG.MASK_N},
      {"bset 4", 0x00, SREG.MASK_S},
      {"bset 6", 0x00, SREG.MASK_T},
      {"bset 3", 0x00, SREG.MASK_V},
      {"bset 1", 0x00, SREG.MASK_Z}};
  }

  @Test(dataProvider = "Provider")
  public void test(String cmd,
                   int sregInit,
                   int expected) throws Exception
  {
    final Device device = getDevice(cmd);
    final SRAM sram = device.getSRAM();
    final CPU cpu = device.getCPU();
    final SREG sreg = cpu.getSREG();
    sreg.setValue(sregInit);
    final ClockStateTestImpl cs = new ClockStateTestImpl();
    final Set<Integer> expectedChange = new HashSet<>();
    sram.addMemoryChangeListener(new MemoryChangeHandler(sram,
                                                         expectedChange,
                                                         cmd)::onMemoryChanged);

    device.onClock(cs.getAndNext());
    expectedChange.add(sreg.getMemoryAddress());
    device.onClock(cs.getAndNext());
    assertSREG(sreg.getValue(),
               expected,
               cmd);
    assertTrue(expectedChange.isEmpty(),
               cmd);
    assertEquals(device.getCPU().getIP(),
                 1,
                 cmd);
  }

}
