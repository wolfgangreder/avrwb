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
package com.avrwb.avr8.impl;

import com.avrwb.avr8.Device;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.api.instructions.DummyDevice;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;
import com.avrwb.schema.XmlPart;
import com.avrwb.schema.util.DeviceStreamer;
import java.util.List;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class CPU_2ENGTest
{

  private static CPU_2E cpu;

  public CPU_2ENGTest()
  {
  }

  private static XmlModule findCPU(XmlPart part)
  {
    XmlDevice dev = part.getDevice();
    for (XmlModule m : dev.getModules().getModule()) {
      if (m.getId().equals("cpu")) {
        return m;
      }
    }
    return null;
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    CPU_2EBuilder builder = new CPU_2EBuilder();
    XmlPart file = DeviceStreamer.loadDevice(CPU_2ENGTest.class.getResource("/com/avrwb/devices/ATmega8.xml"),
                                             null);
    XmlModule module = findCPU(file);
    cpu = (CPU_2E) builder.
            notFoundStrategy(NotFoundStrategy.ERROR).
            moduleDescriptor(module).
            build();
    assertNotNull("cpu==null",
                  cpu);
  }

  @Test
  public void testGetSREG()
  {
    assertNotNull("sreg==null",
                  cpu.getSREG());
  }

  @Test
  public void testGetStackPointer()
  {
    assertNotNull("sp==null",
                  cpu.getStackPointer());
  }

  @Test
  public void testGetRegister()
  {
    List<Register> register = cpu.getRegister();
    assertNotNull("register==null",
                  register);
    assertFalse("register is empty",
                register.isEmpty());
    assertTrue("register contains no sreg",
               register.contains(cpu.getSREG()));
    assertTrue("register contains no sp",
               register.contains(cpu.getStackPointer()));
  }

  @Test(dependsOnMethods = "testGetRegister")
  public void testReset() throws SimulationException
  {
    Device device = new DummyDevice(cpu,
                                    32,
                                    1024);
    for (ResetSource rs : ResetSource.values()) {
      for (Register r : cpu.getRegister()) {
        r.setValue(rs.ordinal());
      }
      cpu.reset(device,
                rs);
      for (Register r : cpu.getRegister()) {
        assertEquals("Resetvalue for resetsource " + rs.name() + " and register " + r.getName() + " is not zero",
                     0,
                     r.getValue());
      }
    }
  }

  @Test(enabled = false)
  public void testOnClock() throws SimulationException
  {
    cpu.onClock(null,
                null);
  }

  @Test(enabled = false)
  public void testGetCurrentInstruction()
  {
    cpu.getCurrentInstruction();
  }

}
