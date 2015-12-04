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

import com.avrwb.assembler.AssemblerResult;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.instructions.helper.StringAssemblerSource;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class LdiNGTest extends AbstractInstructionTest
{

  public LdiNGTest()
  {
  }

  @DataProvider(name = "Provider")
  Object[][] getData()
  {
    return new Object[][]{
      {16, 0, 0},
      {16, 0xff, 0},
      {31, 0, 0},
      {31, 0xff, 0},
      {16, 0, 0xff},
      {16, 0xff, 0xff},
      {31, 0, 0xff},
      {31, 0xff, 0xff}
    };
  }

  @Test(dataProvider = "Provider")
  public void testLdi(int register,
                      int k8,
                      int sreg) throws Exception
  {
    String cmd = "ldi r" + register + ",0x" + Integer.toHexString(k8);
    AssemblerResult asr = assembler.compile(new StringAssemblerSource(cmd + "\nnop",
                                                                      cmd),
                                            null);
    assertNotNull(asr,
                  cmd + "|asr==null");
    final Device device = getDevice();
    final SRAM sram = device.getSRAM();
    Set<Integer> expectedChange = new HashSet<>();
    sram.setByteAt(register,
                   ~k8);
    final ClockStateTestImpl clock = new ClockStateTestImpl();
    device.getFlash().initialize(asr.getCSEG());
    device.reset(ResetSource.POWER_UP);
    device.getCPU().getSREG().setValue(sreg);
    sram.addMemoryChangeListener((Memory mem, Set<Integer> addresses) -> {
      assertSame(mem,
                 sram,
                 cmd + "|memory is not sram");
      assertFalse(expectedChange.isEmpty(),
                  cmd + "|dont expect memory change");
      assertTrue(addresses.containsAll(expectedChange));
      assertTrue(expectedChange.containsAll(addresses));
      expectedChange.clear();
    });
    device.onClock(clock,
                   device);
    clock.next();
    expectedChange.add(register);
    device.onClock(clock,
                   device);
    assertTrue(expectedChange.isEmpty(),
               cmd + "|event listener not called");
    assertEquals(sram.getByteAt(register),
                 k8,
                 cmd + "|value test");
    assertEquals(device.getCPU().getSREG().getValue(),
                 sreg);
  }

}
