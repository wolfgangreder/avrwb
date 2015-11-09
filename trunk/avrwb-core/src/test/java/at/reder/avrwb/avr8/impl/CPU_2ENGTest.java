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
package at.reder.avrwb.avr8.impl;

import at.reder.atmelschema.ModuleVector;
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Module;
import at.reder.avrwb.avr8.Register;
import at.reder.avrwb.avr8.ResetSource;
import at.reder.avrwb.avr8.helper.NotFoundStrategy;
import java.util.List;
import java.util.Random;
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
  private static final Random RANDOM = new Random();

  public CPU_2ENGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    CPU_2EBuilder builder = new CPU_2EBuilder();
    XA_AvrToolsDeviceFile file = XA_AvrToolsDeviceFile.load(CPU_2ENGTest.class.getResource("/com/atmel/devices/ATmega8.xml"));
    ModuleVector vector = new ModuleVector("ATmega8",
                                           "CPU");
    XA_Module module = file.findModule(vector);
    cpu = (CPU_2E) builder.
            descriptor(file).
            notFoundStrategy(NotFoundStrategy.ERROR).
            moduleDescriptor(module).
            moduleVector(vector).
            build();
    assertNotNull("cpu==null",
                  cpu);
  }

  @Test
  public void testSetIP()
  {
    int expected = RANDOM.nextInt(Integer.MAX_VALUE);
    cpu.setIP(expected);
    int actual = cpu.getIP();
    assertEquals(expected,
                 actual);
    cpu.setIP(0);
    actual = cpu.getIP();
    assertEquals(0,
                 actual);
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
  public void testGetParam()
  {
    assertNotNull("param==null",
                  cpu.getParam());
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
  public void testReset()
  {
    for (ResetSource rs : ResetSource.values()) {
      for (Register r : cpu.getRegister()) {
        r.setValue(rs.ordinal());
      }
      cpu.reset(rs);
      for (Register r : cpu.getRegister()) {
        assertEquals("Resetvalue for resetsource " + rs.name() + " and register " + r.getName() + " is not zero",
                     0,
                     r.getValue());
      }
    }
  }

  @Test(expectedExceptions = {UnsupportedOperationException.class})
  public void testOnClock()
  {
    cpu.onClock(null);
  }

  @Test(expectedExceptions = {UnsupportedOperationException.class})
  public void testGetCurrentInstruction()
  {
    cpu.getCurrentInstruction();
  }

}
