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

import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.DeviceBuilder;
import com.avrwb.avr8.spi.InstanceFactories;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.schema.XmlPart;
import com.avrwb.schema.util.DeviceStreamer;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Wolfgang Reder
 */
public class DeviceImplTest
{

  static XmlPart file;

  public DeviceImplTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    file = DeviceStreamer.loadDevice(DeviceImplTest.class.getResource("/com/avrwb/devices/ATmega8.xml"),
                                     null);
    assertNotNull(file);
  }

  @Test
  public void testConstruct() throws NullPointerException, IllegalStateException, ItemNotFoundException
  {
    DeviceBuilder deviceBuilder = InstanceFactories.getDeviceBuilder();
    Device device = deviceBuilder.fromDescriptor(file).build();
    CPU cpu = device.getCPU();
    assertNotNull("cpu==null",
                  cpu);
    assertNotNull("flash==null",
                  device.getFlash());
    assertNotNull("sram==null",
                  device.getSRAM());
    assertNotNull("deviceKey==null",
                  device.getDeviceKey());
  }

}
