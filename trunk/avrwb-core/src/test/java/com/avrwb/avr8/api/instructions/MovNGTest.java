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

import com.avrwb.atmelschema.XA_AvrToolsDeviceFile;
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.api.InstanceFactories;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.avr8.impl.DeviceImplTest;
import com.avrwb.io.IntelHexInputStream;
import java.io.IOException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author wolfi
 */
public class MovNGTest extends AbstractInstructionTest
{

  private static XA_AvrToolsDeviceFile file;
  private final ClockStateTestImpl clockState = new ClockStateTestImpl();

  public MovNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    testClass(Mov.class);
    file = XA_AvrToolsDeviceFile.load(DeviceImplTest.class.getResource("/com/atmel/devices/ATmega8.xml"));
//    AVRWBDefaults.setDebugLoggingActive(true);
//    AVRWBDefaults.setInstructionTraceLvel(Level.INFO);
  }

  private Device initDevice(String deviceFile) throws NullPointerException, IllegalStateException, ItemNotFoundException,
                                                      IOException, SimulationException
  {
    Device device = InstanceFactories.getDeviceBuilder().fromDescriptor(file,
                                                                        null).
            //            deviceLogger(AVRWBDefaults.LOGGER).
            build();
    Memory flash = device.getFlash();
    flash.initialize(new IntelHexInputStream(getClass().getResourceAsStream("/testprojects/mov/" + deviceFile)).read());
    device.reset(ResetSource.POWER_UP);
    clockState.reset();
    return device;
  }

  @Test
  public void testDoExecute1() throws Exception
  {
    Device device = initDevice("mov1.hex");
    CPU cpu = device.getCPU();
    Instruction instruction = cpu.getCurrentInstruction();
    assertTrue(instruction instanceof Mov);
    Mov mov = (Mov) instruction;
    assertEquals(0,
                 mov.getRdAddress());
    assertEquals(31,
                 mov.getRrAddress());
    Memory sram = device.getSRAM();
    sram.setByteAt(0,
                   0xff);
    sram.setByteAt(1,
                   0x33);
    sram.setByteAt(30,
                   0x22);
    sram.setByteAt(31,
                   0x55);
    sram.setByteAt(32,
                   0x44);
    cpu.onClock(clockState,
                device);
    assertEquals(0xff,
                 sram.getByteAt(0));
    assertEquals(0x33,
                 sram.getByteAt(1));
    assertEquals(0x22,
                 sram.getByteAt(30));
    assertEquals(0x55,
                 sram.getByteAt(31));
    assertEquals(0x44,
                 sram.getByteAt(32));
    clockState.next();
    cpu.onClock(clockState,
                device);
    assertEquals(0x55,
                 sram.getByteAt(0));
    assertEquals(0x33,
                 sram.getByteAt(1));
    assertEquals(0x22,
                 sram.getByteAt(30));
    assertEquals(0x55,
                 sram.getByteAt(31));
    assertEquals(0x44,
                 sram.getByteAt(32));
  }

  @Test
  public void testDoExecute2() throws Exception
  {
    Device device = initDevice("mov2.hex");
    CPU cpu = device.getCPU();
    Instruction instruction = cpu.getCurrentInstruction();
    assertTrue(instruction instanceof Mov);
    Mov mov = (Mov) instruction;
    assertEquals(31,
                 mov.getRdAddress());
    assertEquals(0,
                 mov.getRrAddress());
    Memory sram = device.getSRAM();
    sram.setByteAt(0,
                   0xff);
    sram.setByteAt(31,
                   0x55);
    cpu.onClock(clockState,
                device);
    assertEquals(0xff,
                 sram.getByteAt(0));
    assertEquals(0x55,
                 sram.getByteAt(31));
    clockState.next();
    cpu.onClock(clockState,
                device);
    assertEquals(0xff,
                 sram.getByteAt(0));
    assertEquals(0xff,
                 sram.getByteAt(31));
  }

  /**
   * r16 und r31 werden über r0 ausgetauscht
   *
   * @throws Exception no exception should be thrown
   */
  @Test
  public void testDoExecute3() throws Exception
  {
    Device device = initDevice("mov3.hex");
    CPU cpu = device.getCPU();
    Memory sram = device.getSRAM();
    sram.setByteAt(0,
                   0xff);
    sram.setByteAt(16,
                   0x33);
    sram.setByteAt(31,
                   0x55);
    Instruction instruction = cpu.getCurrentInstruction();
    assertTrue(instruction instanceof Mov);
    // mov r0,r16
    Mov mov = (Mov) instruction;
    assertEquals(0,
                 mov.getRdAddress());
    assertEquals(16,
                 mov.getRrAddress());
    cpu.onClock(clockState,
                device);
    clockState.next();
    cpu.onClock(clockState,
                device);
    clockState.next();
    assertEquals(0x33,
                 sram.getByteAt(0));
    assertEquals(0x33,
                 sram.getByteAt(16));
    assertEquals(0x55,
                 sram.getByteAt(31));

    // mov r16,r31
    instruction = cpu.getCurrentInstruction();
    assertTrue(instruction instanceof Mov);
    mov = (Mov) instruction;
    assertEquals(16,
                 mov.getRdAddress());
    assertEquals(31,
                 mov.getRrAddress());
    cpu.onClock(clockState,
                device);
    clockState.next();
    cpu.onClock(clockState,
                device);
    clockState.next();
    assertEquals(0x33,
                 sram.getByteAt(0));
    assertEquals(0x55,
                 sram.getByteAt(16));
    assertEquals(0x55,
                 sram.getByteAt(31));

    // mov r31,r0
    instruction = cpu.getCurrentInstruction();
    assertTrue(instruction instanceof Mov);
    mov = (Mov) instruction;
    assertEquals(31,
                 mov.getRdAddress());
    assertEquals(0,
                 mov.getRrAddress());
    cpu.onClock(clockState,
                device);
    clockState.next();
    cpu.onClock(clockState,
                device);
    clockState.next();
    assertEquals(0x33,
                 sram.getByteAt(0));
    assertEquals(0x55,
                 sram.getByteAt(16));
    assertEquals(0x33,
                 sram.getByteAt(31));
  }

}
